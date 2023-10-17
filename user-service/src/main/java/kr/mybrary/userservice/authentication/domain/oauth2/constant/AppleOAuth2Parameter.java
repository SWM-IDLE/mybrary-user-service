package kr.mybrary.userservice.authentication.domain.oauth2.constant;

public class AppleOAuth2Parameter {

    public static final String CODE = "code";
    public static final String SUB = "sub";
    public static final String EMAIL = "email";
    public static final String USER = "user";
    public static final String APPLE_KEY_PATH = "apple/AuthKey_8LCJC23RYD.p8";
    public static final String APPLE_AUTH_URL = "https://appleid.apple.com";
    public static final String CALLBACK_URL = "kr.mybrary://";
    public static final String ACCESS_TOKEN_PARAMETER = "Authorization";
    public static final String REFRESH_TOKEN_PARAMETER = "Authorization-Refresh";
    public static final String TOKEN_TYPE_HINT_REFRESH_TOKEN = "refresh_token";
    public static final String APPLE_TOKEN_PREFIX = "APPLE_";
    public static final int REFRESH_TOKEN_EXPIRATION = 14;

    private AppleOAuth2Parameter() {
    }

}
