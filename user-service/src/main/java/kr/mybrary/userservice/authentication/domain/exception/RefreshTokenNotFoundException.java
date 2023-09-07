package kr.mybrary.userservice.authentication.domain.exception;

import kr.mybrary.userservice.global.exception.ApplicationException;

public class RefreshTokenNotFoundException extends ApplicationException {

    private static final int STATUS = 401;
    private static final String ERROR_CODE = "A-02";
    private static final String ERROR_MESSAGE = "리프레쉬 토큰이 존재하지 않습니다.";

    public RefreshTokenNotFoundException() {
        super(STATUS, ERROR_CODE, ERROR_MESSAGE);
    }
}
