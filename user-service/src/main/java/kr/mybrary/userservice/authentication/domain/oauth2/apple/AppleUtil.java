package kr.mybrary.userservice.authentication.domain.oauth2.apple;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.ECDSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.ECPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class AppleUtil {

    public String createAppleClientSecret(String clientId, String clientSecret) {
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

    public static byte[] readPrivateKey(String keyPath) {
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

    //JWT Payload부분 decode 메서드
    public Map<String, Object> decodeJwtTokenPayload(String jwtToken){
        Map<String, Object> jwtClaims = new HashMap<>();
        try {
            String[] parts = jwtToken.split("\\.");
            Base64.Decoder decoder = Base64.getUrlDecoder();

            byte[] decodedBytes = decoder.decode(parts[1].getBytes(StandardCharsets.UTF_8));
            String decodedString = new String(decodedBytes, StandardCharsets.UTF_8);
            ObjectMapper mapper = new ObjectMapper();

            Map<String, Object> map = mapper.readValue(decodedString, Map.class);
            jwtClaims.putAll(map);

        } catch (JsonProcessingException e) {
            throw new RuntimeException("JWT Payload decode 실패");
        }
        return jwtClaims;
    }

}
