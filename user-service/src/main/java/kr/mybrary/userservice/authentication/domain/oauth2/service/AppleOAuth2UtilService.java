package kr.mybrary.userservice.authentication.domain.oauth2.service;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.ECDSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import kr.mybrary.userservice.authentication.domain.exception.AppleClientSecretFormatException;
import kr.mybrary.userservice.authentication.domain.exception.AppleClientSecretNotCreatedException;
import kr.mybrary.userservice.authentication.domain.exception.ApplePrivateKeyReadException;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.FileReader;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.interfaces.ECPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Date;

import static kr.mybrary.userservice.authentication.domain.oauth2.constant.AppleOAuth2Parameter.APPLE_AUTH_URL;
import static kr.mybrary.userservice.authentication.domain.oauth2.constant.AppleOAuth2Parameter.APPLE_KEY_PATH;

@RequiredArgsConstructor
@Service
@Slf4j
@Setter
public class AppleOAuth2UtilService {

    public String createAppleClientSecret(String clientId, String clientSecret) {
        checkClientSecret(clientSecret);
        String[] secretKeyResourceArr = clientSecret.split("/");
        String appleKeyId = secretKeyResourceArr[1];
        String appleTeamId = secretKeyResourceArr[2];

        JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.ES256).keyID(appleKeyId).build();
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .issuer(appleTeamId)
                .subject(clientId)
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

    private void checkClientSecret(String clientSecret) {
        if(clientSecret.split("/").length != 3) {
            throw new AppleClientSecretFormatException();
        }
    }

    private byte[] readPrivateKey(String keyPath) {
        Resource resource = new ClassPathResource(keyPath);
        log.info("resource : {}", resource);
        log.info("resource.exists() : {}", resource.exists());
        try {
            FileReader keyReader = new FileReader(resource.getFile());
            PemReader pemReader = new PemReader(keyReader);
            log.info("keyReader : {}", keyReader);
            log.info("pemReader : {}", pemReader);
            PemObject pemObject = pemReader.readPemObject();
            log.info("pemObject : {}", pemObject);
            byte[] content = pemObject.getContent();
            log.info("pemObject.getContent() : {}", content);
            return content;
        } catch (Exception e) {
            log.info("error Message : {}", e.getMessage());
            throw new ApplePrivateKeyReadException();
        }
    }

}
