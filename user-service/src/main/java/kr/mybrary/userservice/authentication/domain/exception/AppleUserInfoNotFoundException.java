package kr.mybrary.userservice.authentication.domain.exception;

import kr.mybrary.userservice.global.exception.ApplicationException;

public class AppleUserInfoNotFoundException extends ApplicationException {

    private static final int STATUS = 404;
    private static final String ERROR_CODE = "A-05";
    private static final String ERROR_MESSAGE = "Apple 사용자 정보를 찾을 수 없습니다.";

    public AppleUserInfoNotFoundException() {
        super(STATUS, ERROR_CODE, ERROR_MESSAGE);
    }

}
