package kr.mybrary.userservice.authentication.domain.oauth2.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.ECDSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import jakarta.servlet.http.HttpServletRequest;
import kr.mybrary.userservice.authentication.domain.oauth2.userinfo.AppleOAuth2TokenInfo;
import kr.mybrary.userservice.authentication.domain.oauth2.userinfo.AppleOAuth2UserInfo;
import kr.mybrary.userservice.global.util.JwtUtil;
import kr.mybrary.userservice.global.util.RedisUtil;
import kr.mybrary.userservice.user.persistence.Role;
import kr.mybrary.userservice.user.persistence.SocialType;
import kr.mybrary.userservice.user.persistence.User;
import kr.mybrary.userservice.user.persistence.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import org.apache.commons.lang3.RandomStringUtils;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.FileReader;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.ECPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

import static kr.mybrary.userservice.global.constant.ImageConstant.*;

@RequiredArgsConstructor
@Service
@Slf4j
public class AppleOAuth2UserService {

    private String APPLE_TEAM_ID = "KU782MK78R";

    private String APPLE_LOGIN_KEY = "8LCJC23RYD";

    private String APPLE_CLIENT_ID = "kr.mybrary.service";

    private String APPLE_REDIRECT_URL = "https://f97d-1-235-157-76.ngrok-free.app/auth/v1/apple/callback";

    private String APPLE_KEY_PATH = "apple/AuthKey_8LCJC23RYD.p8";
    static final String CALLBACK_URL = "kr.mybrary://";
    static final String ACCESS_TOKEN_PARAMETER = "Authorization";
    static final String REFRESH_TOKEN_PARAMETER = "Authorization-Refresh";
    static final int REFRESH_TOKEN_EXPIRATION = 14;

    private final static String APPLE_AUTH_URL = "https://appleid.apple.com";
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RedisUtil redisUtil;

    public String getAppleInfo(HttpServletRequest request) throws Exception {
        log.info("Apple Login Start");
        ObjectMapper objectMapper = new ObjectMapper();
        AppleOAuth2UserInfo appleUser;
        String fullName = "";

        // user 정보가 있으면 AppleUser 객체로 변환
        if(request.getParameter("user") != null) {
            appleUser = objectMapper.readValue(request.getParameter("user"), AppleOAuth2UserInfo.class);
            fullName = appleUser.getName().getLastName() + appleUser.getName().getFirstName();
//            String name = appleUser.getName().getLastName() + appleUser.getName().getFirstName();
//            String email = appleUser.getEmail();
        }

        // code가 없으면 예외 발생
        if (request.getParameter("code") == null) throw new Exception("Failed get authorization code");

        // Client Secret 생성
        String clientSecret = createAppleClientSecret(APPLE_CLIENT_ID, "AuthKey_8LCJC23RYD.p8/8LCJC23RYD/KU782MK78R");
        String userId = "";
        String email  = "";
        String accessToken = "";
        String refreshToken = "";


        try {
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-type", "application/x-www-form-urlencoded");

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("grant_type"   , "authorization_code");
            params.add("client_id"    , APPLE_CLIENT_ID);
            params.add("client_secret", clientSecret);
            params.add("code"         , request.getParameter("code"));
            params.add("redirect_uri" , APPLE_REDIRECT_URL);


            RestTemplate restTemplate = new RestTemplate(); // TODO: FeignClient로 변경
            HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(params, headers);

            // id token 발급
            var response = restTemplate.exchange(
                    APPLE_AUTH_URL + "/auth/token",
                    HttpMethod.POST,
                    httpEntity,
                    String.class
            );

            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObj = (JSONObject) jsonParser.parse(response.getBody());

            accessToken = String.valueOf(jsonObj.get("access_token"));
            refreshToken = String.valueOf(jsonObj.get("refresh_token"));

            //ID TOKEN을 통해 회원 고유 식별자 받기 - decode 안해도 되는 것인가?
            SignedJWT signedJWT = SignedJWT.parse(String.valueOf(jsonObj.get("id_token")));
            JWTClaimsSet getPayload = signedJWT.getJWTClaimsSet();

            JSONObject payload = objectMapper.convertValue(getPayload.getClaims(), JSONObject.class);

            userId = String.valueOf(payload.get("sub"));
            email  = String.valueOf(payload.get("email"));
        } catch (Exception e) {
            throw new Exception("API call failed");
        }

        // 가입된 회원인지 확인
        AppleOAuth2TokenInfo appleOAuth2TokenInfo = AppleOAuth2TokenInfo.builder()
                .id(userId)
                .token(accessToken)
                .email(email)
                .fullName(fullName)
                .build();


        User createdUser = getUser(appleOAuth2TokenInfo);

        String my_accessToken = jwtUtil.createAccessToken(createdUser.getLoginId(), LocalDateTime.now());
        String my_refreshToken = jwtUtil.createRefreshToken(LocalDateTime.now());

        redisUtil.set(createdUser.getLoginId(),  my_refreshToken, Duration.ofDays(REFRESH_TOKEN_EXPIRATION));
        redisUtil.set("APPLE_"+createdUser.getSocialId(),  refreshToken, null);

        String url = UriComponentsBuilder.fromUriString(CALLBACK_URL)
                .queryParam(ACCESS_TOKEN_PARAMETER, my_accessToken)
                .queryParam(REFRESH_TOKEN_PARAMETER, my_refreshToken)
                .toUriString();

        return url;
    }

    public void withdrawApple(String socialId) {
        String clientSecret = createAppleClientSecret(APPLE_CLIENT_ID, "AuthKey_8LCJC23RYD.p8/8LCJC23RYD/KU782MK78R");
        String refreshToken = (String) redisUtil.get("APPLE_"+socialId);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_secret", clientSecret); // 생성한 client_secret
        params.add("token", refreshToken); // 생성한 refresh_token
        params.add("client_id", APPLE_CLIENT_ID); // app bundle id

        try {
            RestTemplate restTemplate = new RestTemplate(); // TODO: FeignClient로 변경
            HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(params, headers);

            // revoke 요청
            var response = restTemplate.exchange(
                    APPLE_AUTH_URL + "/auth/revoke",
                    HttpMethod.POST,
                    httpEntity,
                    String.class
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
        redisUtil.delete("APPLE_"+socialId);
    }

    private User getUser(AppleOAuth2TokenInfo appleOAuth2TokenInfo) {
        User findUser = userRepository.findBySocialTypeAndSocialId(SocialType.APPLE, appleOAuth2TokenInfo.getId()).orElse(null);

        if (findUser == null) {
            return saveUser(appleOAuth2TokenInfo);
        }
        return findUser;
    }

    private User saveUser(AppleOAuth2TokenInfo appleOAuth2TokenInfo) {
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
                .audience("https://appleid.apple.com")
                .build();

        SignedJWT jwt = new SignedJWT(header, claimsSet);

        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(readPrivateKey("/apple/AuthKey_8LCJC23RYD.p8"));
        KeyFactory kf;
        try {
            kf = KeyFactory.getInstance("EC");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        try {
            ECPrivateKey privateKey = (ECPrivateKey) kf.generatePrivate(spec);
            JWSSigner jwsSigner = new ECDSASigner(privateKey);
            jwt.sign(jwsSigner);
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException(e);
        } catch (JOSEException e) {
            throw new RuntimeException(e);
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
            throw new RuntimeException(e);
        }
    }

}
