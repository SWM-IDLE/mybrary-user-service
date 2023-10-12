package kr.mybrary.userservice.authentication.domain.oauth2.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.ECDSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import jakarta.servlet.http.HttpServletRequest;
import kr.mybrary.userservice.authentication.domain.exception.*;
import kr.mybrary.userservice.authentication.domain.oauth2.userinfo.AppleOAuth2TokenInfo;
import kr.mybrary.userservice.authentication.domain.oauth2.userinfo.AppleOAuth2UserInfo;
import kr.mybrary.userservice.client.apple.api.AppleOAuth2ServiceClient;
import kr.mybrary.userservice.client.apple.dto.AppleOAuth2TokenServiceResponse;
import kr.mybrary.userservice.global.util.JwtUtil;
import kr.mybrary.userservice.global.util.RedisUtil;
import kr.mybrary.userservice.user.persistence.Role;
import kr.mybrary.userservice.user.persistence.SocialType;
import kr.mybrary.userservice.user.persistence.User;
import kr.mybrary.userservice.user.persistence.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import org.apache.commons.lang3.RandomStringUtils;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.FileReader;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.interfaces.ECPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.text.ParseException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

import static kr.mybrary.userservice.authentication.domain.oauth2.constant.AppleOAuth2Parameter.*;
import static kr.mybrary.userservice.global.constant.ImageConstant.*;

@RequiredArgsConstructor
@Service
@Slf4j
public class AppleOAuth2UserService {

    @Value("${spring.security.oauth2.client.registration.apple.client-id}")
    private String APPLE_CLIENT_ID;

    @Value("${spring.security.oauth2.client.registration.apple.client-secret}")
    private String APPLE_CLIENT_SECRET;

    @Value("${spring.security.oauth2.client.registration.apple.redirect-uri}")
    private String APPLE_REDIRECT_URI;

    @Value("${spring.security.oauth2.client.registration.apple.authorization-grant-type}")
    private String APPLE_AUTHORIZATION_GRANT_TYPE;

    private final AppleOAuth2ServiceClient appleOAuth2ServiceClient;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RedisUtil redisUtil;
    private final ObjectMapper objectMapper;

    public String authenticateWithApple(HttpServletRequest request) {
        User user = getAppleUserByRequest(request);
        String refreshToken = jwtUtil.createRefreshToken(LocalDateTime.now());
        redisUtil.set(user.getLoginId(), refreshToken, Duration.ofDays(REFRESH_TOKEN_EXPIRATION));

        return UriComponentsBuilder.fromUriString(CALLBACK_URL)
                .queryParam(ACCESS_TOKEN_PARAMETER, jwtUtil.createAccessToken(user.getLoginId(), LocalDateTime.now()))
                .queryParam(REFRESH_TOKEN_PARAMETER, refreshToken)
                .toUriString();
    }

    @NotNull
    private User getAppleUserByRequest(HttpServletRequest request) {
        if(request.getParameter(USER) == null) {
            return getAppleUserFromRequest(request);
        }
        return signUpAndGetAppleUserFromRequest(request);
    }

    private User getAppleUserFromRequest(HttpServletRequest request) {
        if (request.getParameter(CODE) == null) {
            throw new AppleCodeNotFoundException();
        }
        AppleOAuth2TokenServiceResponse tokenResponse = appleOAuth2ServiceClient.getAppleToken(APPLE_CLIENT_ID,
                createAppleClientSecret(APPLE_CLIENT_ID, APPLE_CLIENT_SECRET), request.getParameter(CODE),
                APPLE_AUTHORIZATION_GRANT_TYPE, APPLE_REDIRECT_URI);
        return getExistingAppleUser(String.valueOf(parseAppleToken(tokenResponse).get(SUB)));
    }

    private User getExistingAppleUser(String socialId) {
        User findUser = userRepository.findBySocialTypeAndSocialId(SocialType.APPLE, socialId).orElse(null);
        if (findUser == null) {
            throw new AppleUserNotFoundException();
        }
        return findUser;
    }

    private User signUpAndGetAppleUserFromRequest(HttpServletRequest request) {
        AppleOAuth2UserInfo appleOAuth2UserInfo = getAppleOAuth2UserInfo(request);
        if (request.getParameter(CODE) == null) {
            throw new AppleCodeNotFoundException();
        }
        AppleOAuth2TokenServiceResponse tokenResponse = appleOAuth2ServiceClient.getAppleToken(APPLE_CLIENT_ID,
                createAppleClientSecret(APPLE_CLIENT_ID, APPLE_CLIENT_SECRET), request.getParameter(CODE),
                APPLE_AUTHORIZATION_GRANT_TYPE, APPLE_REDIRECT_URI);

        JSONObject payload = parseAppleToken(tokenResponse);
        AppleOAuth2TokenInfo appleOAuth2TokenInfo = AppleOAuth2TokenInfo.builder()
                .id(String.valueOf(payload.get(SUB)))
                .token(String.valueOf(tokenResponse.getAccess_token()))
                .email(String.valueOf(payload.get(EMAIL)))
                .fullName(appleOAuth2UserInfo.getName().getLastName() + appleOAuth2UserInfo.getName().getFirstName())
                .build();

        redisUtil.set(APPLE_TOKEN_PREFIX + payload.get(SUB), String.valueOf(tokenResponse.getRefresh_token()), null);
        return saveNewAppleUser(appleOAuth2TokenInfo);
    }

    private AppleOAuth2UserInfo getAppleOAuth2UserInfo(HttpServletRequest request) {
        try {
            return objectMapper.readValue(request.getParameter(USER), AppleOAuth2UserInfo.class);
        } catch (JsonProcessingException e) {
            throw new AppleUserInfoReadException();
        }
    }

    private JSONObject parseAppleToken(AppleOAuth2TokenServiceResponse tokenResponse) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(tokenResponse.getId_token());
            JWTClaimsSet getPayload = signedJWT.getJWTClaimsSet();
            return objectMapper.convertValue(getPayload.getClaims(), JSONObject.class);
        } catch (ParseException e) {
            throw new AppleTokenParseException();
        }
    }

    private User saveNewAppleUser(AppleOAuth2TokenInfo appleOAuth2TokenInfo) {
        User createdUser = User.builder()
                .socialType(SocialType.APPLE)
                .socialId(appleOAuth2TokenInfo.getId())
                .loginId(UUID.randomUUID().toString())
                .password(UUID.randomUUID().toString())
                .email(appleOAuth2TokenInfo.getEmail())
                .nickname(appleOAuth2TokenInfo.getFullName() + RandomStringUtils.randomNumeric(5))
                .introduction("")
                .role(Role.USER)
                .build();
        createdUser.updatePassword(passwordEncoder.encode(createdUser.getPassword()));
        setDefaultProfileImage(createdUser);
        return userRepository.save(createdUser);
    }

    private void setDefaultProfileImage(User createdUser) {
        createdUser.updateProfileImageUrl(DEFAULT_PROFILE_IMAGE.getUrl());
        createdUser.updateProfileImageThumbnailTinyUrl(DEFAULT_PROFILE_IMAGE_TINY.getUrl());
        createdUser.updateProfileImageThumbnailSmallUrl(DEFAULT_PROFILE_IMAGE_SMALL.getUrl());
    }

    public void withdrawApple(String socialId) {
        String clientSecret = createAppleClientSecret(APPLE_CLIENT_ID, APPLE_CLIENT_SECRET);
        String refreshToken = (String) redisUtil.get(APPLE_TOKEN_PREFIX + socialId);
        appleOAuth2ServiceClient.revokeAppleToken(APPLE_CLIENT_ID, clientSecret, refreshToken, TOKEN_TYPE_HINT_REFRESH_TOKEN);
        redisUtil.delete(APPLE_TOKEN_PREFIX + socialId);
    }

    private String createAppleClientSecret(String clientId, String clientSecret) {
        String[] secretKeyResourceArr = clientSecret.split("/");
        String appleKeyId = secretKeyResourceArr[1];
        String appleTeamId = secretKeyResourceArr[2];
        String appleClientId = clientId;

        JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.ES256).keyID(appleKeyId).build();
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .issuer(appleTeamId)
                .subject(appleClientId)
                .issueTime(new Date())
                .expirationTime(new Date(System.currentTimeMillis() + 1000 * 60 * 5))
                .audience(APPLE_AUTH_URL)
                .build();

        SignedJWT jwt = new SignedJWT(header, claimsSet);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(readPrivateKey(APPLE_KEY_PATH));

        try {
            KeyFactory kf = KeyFactory.getInstance("EC");
            ECPrivateKey privateKey = (ECPrivateKey) kf.generatePrivate(spec);
            JWSSigner jwsSigner = new ECDSASigner(privateKey);
            jwt.sign(jwsSigner);
        } catch (Exception e) {
            throw new AppleClientSecretNotCreatedException();
        }

        return jwt.serialize();
    }

    private byte[] readPrivateKey(String keyPath) {
        Resource resource = new ClassPathResource(keyPath);
        try {
            FileReader keyReader = new FileReader(resource.getFile());
            PemReader pemReader = new PemReader(keyReader);
            PemObject pemObject = pemReader.readPemObject();
            return pemObject.getContent();
        } catch (IOException e) {
            throw new ApplePrivateKeyReadException();
        }
    }

}
