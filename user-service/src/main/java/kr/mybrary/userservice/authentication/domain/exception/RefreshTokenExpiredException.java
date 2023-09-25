package kr.mybrary.userservice.authentication.domain.exception;

import kr.mybrary.userservice.global.exception.ApplicationException;

public class RefreshTokenExpiredException extends ApplicationException {

    private static final int STATUS = 401;
    private static final String ERROR_CODE = "A-04";
    private static final String ERROR_MESSAGE = "만료된 리프레쉬 토큰입니다.";

    public RefreshTokenExpiredException() {
        super(STATUS, ERROR_CODE, ERROR_MESSAGE);
    }
}
