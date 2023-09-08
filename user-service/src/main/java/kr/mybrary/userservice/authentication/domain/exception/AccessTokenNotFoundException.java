package kr.mybrary.userservice.authentication.domain.exception;

import kr.mybrary.userservice.global.exception.ApplicationException;

public class AccessTokenNotFoundException extends ApplicationException {

    private static final int STATUS = 401;
    private static final String ERROR_CODE = "A-01";
    private static final String ERROR_MESSAGE = "액세스 토큰이 존재하지 않습니다.";

    public AccessTokenNotFoundException() {
        super(STATUS, ERROR_CODE, ERROR_MESSAGE);
    }

}
