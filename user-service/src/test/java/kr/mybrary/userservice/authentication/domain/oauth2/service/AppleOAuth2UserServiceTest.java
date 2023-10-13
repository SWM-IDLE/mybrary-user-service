package kr.mybrary.userservice.authentication.domain.oauth2.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.mybrary.userservice.authentication.domain.exception.AppleCodeNotFoundException;
import kr.mybrary.userservice.authentication.domain.exception.AppleUserInfoReadException;
import kr.mybrary.userservice.authentication.domain.exception.AppleUserNotFoundException;
import kr.mybrary.userservice.authentication.domain.oauth2.userinfo.AppleOAuth2UserInfo;
import kr.mybrary.userservice.client.apple.api.AppleOAuth2ServiceClient;
import kr.mybrary.userservice.client.apple.dto.AppleOAuth2TokenServiceResponse;
import kr.mybrary.userservice.global.util.JwtUtil;
import kr.mybrary.userservice.global.util.RedisUtil;
import kr.mybrary.userservice.user.UserFixture;
import kr.mybrary.userservice.user.persistence.repository.UserRepository;
import net.minidev.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppleOAuth2UserServiceTest {

    @Mock
    AppleOAuth2ServiceClient appleOAuth2ServiceClient;
    @Mock
    UserRepository userRepository;
    @Mock
    PasswordEncoder passwordEncoder;
    @Mock
    JwtUtil jwtUtil;
    @Mock
    RedisUtil redisUtil;
    @Mock
    ObjectMapper objectMapper;
    @InjectMocks
    AppleOAuth2UserService appleOAuth2UserService;

    private static final String ID_TOKEN_SAMPLE = "eyJraWQiOiJZdXlYb1kiLCJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJodHRwczovL2FwcGxlaWQuYXBwbGUuY29tIiwiYXVkIjoia3IubXlicmFyeS5zZXJ2aWNlIiwiZXhwIjoxNjk3MTc0MDAyLCJpYXQiOjE2OTcwODc2MDIsInN1YiI6IjAwMDg3NS40ZTY3NmY1MjdiNGU0MzYwYjM2NmM0OWVhMzAzMzg3ZS4wODM4IiwiYXRfaGFzaCI6IlpHdHl1VGhIelpiTFhLa3J1TjBxX1EiLCJlbWFpbCI6InJyeDJ4dGZkNDdAcHJpdmF0ZXJlbGF5LmFwcGxlaWQuY29tIiwiZW1haWxfdmVyaWZpZWQiOiJ0cnVlIiwiaXNfcHJpdmF0ZV9lbWFpbCI6InRydWUiLCJhdXRoX3RpbWUiOjE2OTcwODc1OTksIm5vbmNlX3N1cHBvcnRlZCI6dHJ1ZX0.AfHApudw3-Kvc0v8eFlhx4437utg6btS374yJdKhGGY6Hv3XTLYsq2l06rDjvZ1T2LUalPShthRT2LhPKekD5pbnpl6o0WTSoeaEyCS_0RNvOqEH-3O9vpDJiXhh9VEKLICnJSt5v2tbiHs1TOM8OgIMLvLvWJaeTtZ3opjPl7a_Vj82Rb16nqZhIjZAkgGcHb5fRPPr5VlEM8vnlPkRDo2w_HXWGriZ-UKjvq-K-d5sY9dbeKPRNxfq8Ce46OGLfyoxGZw7uVTJZiQWdBB0IoMHWBVikmRUOGEhV5XTLd_MvxXH3Ffh30caY-5fH0Ceps9diVIIzYVBQNcH5fVipw";

    @Test
    @DisplayName("이미 가입된 애플 소셜 로그인 회원을 로그인한다.")
    void authenticateRegisteredUser() {
        // given
        setUpAppleInfo();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter("code", "code");

        given(appleOAuth2ServiceClient.getAppleToken(anyString(), anyString(), anyString(), anyString(), anyString()))
                .willReturn(AppleOAuth2TokenServiceResponse.builder()
                        .access_token("access_token")
                        .token_type("token_type")
                        .expires_in(1000L)
                        .refresh_token("refresh_token")
                        .id_token(ID_TOKEN_SAMPLE)
                        .build());
        given(objectMapper.convertValue(any(), (Class<Object>) any())).willReturn(mock(JSONObject.class));
        given(userRepository.findBySocialTypeAndSocialId(any(), any())).willReturn(Optional.ofNullable(UserFixture.APPLE_USER.getUser()));
        given(jwtUtil.createAccessToken(any(), any())).willReturn("accessToken");
        given(jwtUtil.createRefreshToken(any())).willReturn("refreshToken");
        doNothing().when(redisUtil).set(any(), any(), any());

        // when
        String redirectUrl = appleOAuth2UserService.authenticateWithApple(request);

        // then
        assertEquals("kr.mybrary://?Authorization=accessToken&Authorization-Refresh=refreshToken", redirectUrl);
        verify(appleOAuth2ServiceClient, times(1)).getAppleToken(anyString(), anyString(), anyString(), anyString(), anyString());
        verify(userRepository, times(1)).findBySocialTypeAndSocialId(any(), any());
        verify(jwtUtil, times(1)).createAccessToken(any(), any());
        verify(jwtUtil, times(1)).createRefreshToken(any());
        verify(redisUtil, times(1)).set(any(), any(), any());
    }

    @Test
    @DisplayName("이미 가입된 애플 소셜 로그인 회원을 로그인할 때 인증 코드가 없으면 예외가 발생한다.")
    void authenticateRegisteredUserWithoutCode() {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();

        // when
        assertThatThrownBy(() -> appleOAuth2UserService.authenticateWithApple(request))
                .isInstanceOf(AppleCodeNotFoundException.class)
                .hasFieldOrPropertyWithValue("status", 404)
                .hasFieldOrPropertyWithValue("errorCode", "A-06")
                .hasFieldOrPropertyWithValue("errorMessage", "Apple 인증 코드를 찾을 수 없습니다.");

        // then
        verify(appleOAuth2ServiceClient, never()).getAppleToken(anyString(), anyString(), anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("이미 가입된 애플 소셜 로그인 회원을 로그인할 때 사용자를 찾을 수 없으면 예외가 발생한다.")
    void authenticateRegisteredUserNotFound() {
        // given
        setUpAppleInfo();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter("code", "code");

        given(appleOAuth2ServiceClient.getAppleToken(anyString(), anyString(), anyString(), anyString(), anyString()))
                .willReturn(AppleOAuth2TokenServiceResponse.builder()
                        .access_token("access_token")
                        .token_type("token_type")
                        .expires_in(1000L)
                        .refresh_token("refresh_token")
                        .id_token(ID_TOKEN_SAMPLE)
                        .build());
        given(objectMapper.convertValue(any(), (Class<Object>) any())).willReturn(mock(JSONObject.class));
        given(userRepository.findBySocialTypeAndSocialId(any(), any())).willReturn(Optional.empty());

        // then
        assertThatThrownBy(() -> appleOAuth2UserService.authenticateWithApple(request))
                .isInstanceOf(AppleUserNotFoundException.class)
                .hasFieldOrPropertyWithValue("status", 404)
                .hasFieldOrPropertyWithValue("errorCode", "A-05")
                .hasFieldOrPropertyWithValue("errorMessage", "Apple 사용자를 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("최초 애플 소셜 로그인 회원을 회원 가입 후 로그인한다.")
    void authenticateNewUser() throws Exception {
        // given
        setUpAppleInfo();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter("code", "code");
        request.setParameter("user", "user");

        given(objectMapper.convertValue(any(), (Class<Object>) any())).willReturn(mock(JSONObject.class));
        given(objectMapper.readValue(eq("user"), eq(AppleOAuth2UserInfo.class))).willReturn(AppleOAuth2UserInfo.builder()
                .email("email")
                .name(AppleOAuth2UserInfo.Name.builder().lastName("lastName").firstName("firstName").build())
                .build());
        given(appleOAuth2ServiceClient.getAppleToken(anyString(), anyString(), anyString(), anyString(), anyString()))
                .willReturn(AppleOAuth2TokenServiceResponse.builder()
                        .access_token("access_token")
                        .token_type("token_type")
                        .expires_in(1000L)
                        .refresh_token("refresh_token")
                        .id_token(ID_TOKEN_SAMPLE)
                        .build());
        given(passwordEncoder.encode(any())).willReturn("encodedPassword");
        given(userRepository.save(any())).willReturn(UserFixture.APPLE_USER.getUser());
        given(jwtUtil.createAccessToken(any(), any())).willReturn("accessToken");
        given(jwtUtil.createRefreshToken(any())).willReturn("refreshToken");
        doNothing().when(redisUtil).set(any(), any(), any());

        // when
        String redirectUrl = appleOAuth2UserService.authenticateWithApple(request);

        // then
        assertEquals("kr.mybrary://?Authorization=accessToken&Authorization-Refresh=refreshToken", redirectUrl);
        verify(objectMapper, times(1)).readValue(eq("user"), eq(AppleOAuth2UserInfo.class));
        verify(objectMapper, times(1)).convertValue(any(), (Class<Object>) any());
        verify(appleOAuth2ServiceClient, times(1)).getAppleToken(anyString(), anyString(), anyString(), anyString(), anyString());
        verify(passwordEncoder, times(1)).encode(any());
        verify(userRepository, times(1)).save(any());
        verify(jwtUtil, times(1)).createAccessToken(any(), any());
        verify(jwtUtil, times(1)).createRefreshToken(any());
        verify(redisUtil, times(2)).set(any(), any(), any());
    }

    @Test
    @DisplayName("애플 소셜 로그인 회원을 회원 가입 후 로그인할 때 인증 코드가 없으면 예외가 발생한다.")
    void authenticateNewUserWithoutCode() {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter("user", "user");

        // when
        assertThatThrownBy(() -> appleOAuth2UserService.authenticateWithApple(request))
                .isInstanceOf(AppleCodeNotFoundException.class)
                .hasFieldOrPropertyWithValue("status", 404)
                .hasFieldOrPropertyWithValue("errorCode", "A-06")
                .hasFieldOrPropertyWithValue("errorMessage", "Apple 인증 코드를 찾을 수 없습니다.");

        // then
        verify(appleOAuth2ServiceClient, never()).getAppleToken(anyString(), anyString(), anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("애플 소셜 로그인 회원을 회원 가입 후 로그인할 때 사용자 정보를 읽어오는데 실패하면 예외가 발생한다.")
    void userInfoReadErrorWhenAuthenticatingNewUser() throws Exception {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter("code", "code");
        request.setParameter("user", "user");
        given(objectMapper.readValue(eq("user"), eq(AppleOAuth2UserInfo.class))).willThrow(JsonProcessingException.class);

        // when
        assertThatThrownBy(() -> appleOAuth2UserService.authenticateWithApple(request))
                .isInstanceOf(AppleUserInfoReadException.class)
                .hasFieldOrPropertyWithValue("status", 500)
                .hasFieldOrPropertyWithValue("errorCode", "A-10")
                .hasFieldOrPropertyWithValue("errorMessage", "Apple 사용자 정보를 읽어오는데 실패했습니다.");

        // then
        verify(appleOAuth2ServiceClient, never()).getAppleToken(anyString(), anyString(), anyString(), anyString(), anyString());
        verify(objectMapper, times(1)).readValue(eq("user"), eq(AppleOAuth2UserInfo.class));
    }

    @Test
    @DisplayName("애플 소셜 로그인 회원의 토큰을 만료하고 연동을 해제한다.")
    void withdrawApple() {
        // given
        setUpAppleInfo();
        given(redisUtil.get(any())).willReturn("refreshToken");
        doNothing().when(appleOAuth2ServiceClient).revokeAppleToken(any(), any(), any(), any());
        doNothing().when(redisUtil).delete(any());

        // when
        appleOAuth2UserService.withdrawApple("refreshToken");

        // then
        verify(redisUtil, times(1)).get(any());
        verify(appleOAuth2ServiceClient, times(1)).revokeAppleToken(any(), any(), any(), any());
        verify(redisUtil, times(1)).delete(any());
    }

    private void setUpAppleInfo() {
        appleOAuth2UserService.setAPPLE_CLIENT_SECRET("authKeyFile/keyId/teamId");
        appleOAuth2UserService.setAPPLE_CLIENT_ID("clientId");
        appleOAuth2UserService.setAPPLE_AUTHORIZATION_GRANT_TYPE("authorization_code");
        appleOAuth2UserService.setAPPLE_REDIRECT_URI("https://user.mybrary.kr/oauth2/apple/callback");
    }

}