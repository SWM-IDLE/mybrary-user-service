package kr.mybrary.userservice.authentication.domain.oauth2.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AppleOAuth2UtilServiceTest {

    @Test
    @DisplayName("Apple OAuth2 토큰 요청을 위한 clientSecret을 생성한다.")
    void createAppleClientSecret() {
        // given
        String clientId = "clientId";
        String clientSecret = "secretKey/keyId/teamId";
        AppleOAuth2UtilService appleOAuth2UtilService = new AppleOAuth2UtilService();

        // when
        String appleClientSecret = appleOAuth2UtilService.createAppleClientSecret(clientId, clientSecret);

        // then
        assertNotNull(appleClientSecret);
    }
}