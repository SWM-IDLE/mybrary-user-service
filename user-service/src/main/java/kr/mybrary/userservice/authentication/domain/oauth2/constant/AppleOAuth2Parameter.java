package kr.mybrary.userservice.authentication.domain.oauth2.constant;

public class AppleOAuth2Parameter {

    public final static String CODE = "code";
    public final static String SUB = "sub";
    public final static String EMAIL = "email";
    public final static String USER = "user";
    public final static String APPLE_KEY_PATH = "apple/AuthKey_8LCJC23RYD.p8";
    public final static String APPLE_AUTH_URL = "https://appleid.apple.com";
    public final static String CALLBACK_URL = "kr.mybrary://";
    public final static String ACCESS_TOKEN_PARAMETER = "Authorization";
    public final static String REFRESH_TOKEN_PARAMETER = "Authorization-Refresh";
    public final static String TOKEN_TYPE_HINT_REFRESH_TOKEN = "refresh_token";
    public final static String APPLE_TOKEN_PREFIX = "APPLE_";
    public final static int REFRESH_TOKEN_EXPIRATION = 14;

}
