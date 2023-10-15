package kr.mybrary.userservice.authentication.domain.exception;

import kr.mybrary.userservice.global.exception.ApplicationException;

public class AppleUserNotFoundException extends ApplicationException {

    private static final int STATUS = 404;
    private static final String ERROR_CODE = "A-05";
    private static final String ERROR_MESSAGE = "Apple 사용자를 찾을 수 없습니다.";

    public AppleUserNotFoundException() {
        super(STATUS, ERROR_CODE, ERROR_MESSAGE);
    }

}
