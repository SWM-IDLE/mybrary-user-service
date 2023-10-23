package kr.mybrary.userservice.authentication.domain.oauth2.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

import static kr.mybrary.userservice.authentication.domain.oauth2.OAuth2Exception.SOCIAL_LOGIN_FAILED;

@Component
@Slf4j
public class OAuth2LoginFailureHandler implements AuthenticationFailureHandler {

    static final String CALLBACK_URL = "kr.mybrary://";
    static final String ERROR_CODE = "errorCode";
    static final String ERROR_MESSAGE = "errorMessage";

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException exception) throws IOException {
        log.info("OAuth2 Login Failure Handler 실행 - OAuth2 로그인 실패 : " + exception.getMessage());

        String url = UriComponentsBuilder.fromUriString(CALLBACK_URL)
                .queryParam(ERROR_CODE, getErrorCode(exception))
                .queryParam(ERROR_MESSAGE, getErrorMessage(exception))
                .toUriString();

        response.sendRedirect(url);
        log.info("Redirect Url: " + url);
    }

    private String getErrorCode(AuthenticationException exception) {
        if(exception instanceof OAuth2AuthenticationException) {
            return SOCIAL_LOGIN_FAILED.getErrorCode();
        }
        return null;
    }

    private String getErrorMessage(AuthenticationException exception) {
        if(exception instanceof OAuth2AuthenticationException) {
            return String.format(SOCIAL_LOGIN_FAILED.getErrorMessage(), exception.getMessage());
        }
        return exception.getMessage();
    }


}
