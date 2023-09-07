package kr.mybrary.userservice.global.util;

import com.auth0.jwt.JWT;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
class JwtUtilTest {

    static long ACCESS_TOKEN_EXPIRATION_PERIOD =  3600000;
    static long REFRESH_TOKEN_EXPIRATION_PERIOD =  1209600000;
    static String ACCESS_HEADER = "Authorization";
    static String REFRESH_HEADER = "Authorization-Refresh";

    private static final String ACCESS_TOKEN_SUBJECT = "AccessToken";
    private static final String REFRESH_TOKEN_SUBJECT = "RefreshToken";
    private static final String LOGIN_ID_CLAIM = "loginId";
    private static final String BEARER = "Bearer ";

    @Autowired
    private JwtUtil jwtUtil;

    @DisplayName("loginId를 claim에 넣고 액세스 토큰을 생성한다")
    @Test
    void createAccessToken() {
        // given
        String loginId = "loginId";
        LocalDateTime date = LocalDateTime.of(2023, 1, 1, 0, 0, 0);

        // when
        String createdAccessToken = jwtUtil.createAccessToken(loginId, date);

        // then
        assertAll(
                () -> assertThat(createdAccessToken).isNotNull(),
                () -> assertThat(JWT.decode(createdAccessToken).getSubject()).isEqualTo(ACCESS_TOKEN_SUBJECT),
                () -> assertThat(JWT.decode(createdAccessToken).getExpiresAt()).isEqualTo(new Date(
                        Date.from(date.atZone(ZoneId.systemDefault()).toInstant()).getTime() + ACCESS_TOKEN_EXPIRATION_PERIOD)),
                () -> assertThat(JWT.decode(createdAccessToken).getClaim(LOGIN_ID_CLAIM).asString()).isEqualTo(loginId),
                () -> assertThat(JWT.decode(createdAccessToken).getSignature()).isNotNull()
        );
    }

    @DisplayName("리프레시 토큰을 생성한다")
    @Test
    void createRefreshToken() {
        // given
        LocalDateTime date = LocalDateTime.of(2023, 1, 1, 0, 0, 0);

        // when
        String createdRefreshToken = jwtUtil.createRefreshToken(date);

        // then
        assertAll(
                () -> assertThat(createdRefreshToken).isNotNull(),
                () -> assertThat(JWT.decode(createdRefreshToken).getSubject()).isEqualTo(REFRESH_TOKEN_SUBJECT),
                () -> assertThat(JWT.decode(createdRefreshToken).getExpiresAt()).isEqualTo(new Date(
                        Date.from(date.atZone(ZoneId.systemDefault()).toInstant()).getTime() + REFRESH_TOKEN_EXPIRATION_PERIOD)),
                () -> assertThat(JWT.decode(createdRefreshToken).getSignature()).isNotNull()
        );
    }

    @DisplayName("액세스 토큰과 리프레쉬 토큰을 헤더에 추가한다")
    @Test
    void sendAccessAndRefreshToken() {
        // given
        HttpServletResponse response = mock(HttpServletResponse.class);

        // when
        jwtUtil.sendAccessAndRefreshToken(response, "accessToken", "refreshToken");

        // then
        verify(response, times(1)).setHeader(ACCESS_HEADER, "accessToken");
        verify(response, times(1)).setHeader(REFRESH_HEADER, "refreshToken");
    }

    @DisplayName("액세스 토큰을 요청 헤더에서 추출한다")
    @Test
    void extractAccessToken() {
        // given
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader(ACCESS_HEADER)).thenReturn(BEARER + "accessToken");

        // when
        Optional<String> accessToken = jwtUtil.extractAccessToken(request);

        // then
        assertAll(
                () -> assertThat(accessToken).isPresent(),
                () -> assertThat(accessToken).contains("accessToken")
        );

    }

    @DisplayName("리프레시 토큰을 요청 헤더에서 추출한다")
    @Test
    void extractRefreshToken() {
        // given
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader(REFRESH_HEADER)).thenReturn(BEARER + "refreshToken");

        // when
        Optional<String> refreshToken = jwtUtil.extractRefreshToken(request);

        // then
        assertAll(
                () -> assertThat(refreshToken).isPresent(),
                () -> assertThat(refreshToken).contains("refreshToken")
        );
    }

    @DisplayName("유효한 액세스 토큰에서 loginId를 추출한다")
    @Test
    void getLoginIdFromValidAccessToken() {
        // given
        String loginId = "loginId";
        String accessToken = jwtUtil.createAccessToken(loginId, LocalDateTime.now());

        // when
        Optional<String> extractedLoginId = jwtUtil.getLoginIdFromValidAccessToken(accessToken);

        // then
        assertThat(extractedLoginId).contains(loginId);
    }

    @DisplayName("액세스 토큰에서 loginId를 추출할 때 토큰이 유효하지 않으면 예외가 발생한다")
    @Test
    void getLoginIdWithInvalidAccessToken() {
        // given
        String accessToken = "token";

        // when then
        assertThatThrownBy(() -> jwtUtil.getLoginIdFromValidAccessToken(accessToken))
                .isInstanceOf(JwtException.class)
                .hasMessage("유효하지 않은 토큰입니다.");
    }

    @DisplayName("액세스 토큰에서 loginId를 단순히 추출한다")
    @Test
    void parseLoginId() {
        // given
        String loginId = "loginId";
        String accessToken = jwtUtil.createAccessToken(loginId, LocalDateTime.now());

        // when
        String extractedLoginId = jwtUtil.parseLoginId(accessToken);

        // then
        assertThat(extractedLoginId).contains(loginId);
    }

    @DisplayName("토큰의 만료 기간은 0 ~ 1시간 사이이다")
    @Test
    void getExpirationDuration() {
        // given
        String loginId = "loginId";
        LocalDateTime creationDateTime = LocalDateTime.now();
        String accessToken = jwtUtil.createAccessToken(loginId, creationDateTime);
        LocalDateTime currentDateTime = LocalDateTime.now();

        // when
        Duration expirationDuration = jwtUtil.getExpirationDuration(accessToken, currentDateTime);

        // then
        assertThat(expirationDuration).isBetween(Duration.ZERO, Duration.ofHours(1));
    }

}