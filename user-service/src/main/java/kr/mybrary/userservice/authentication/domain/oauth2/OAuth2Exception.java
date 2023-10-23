package kr.mybrary.userservice.authentication.domain.oauth2;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OAuth2Exception {

    SOCIAL_LOGIN_FAILED("U-08", "OAuth2 Login Failed: %s");

    private final String errorCode;
    private final String errorMessage;

}
