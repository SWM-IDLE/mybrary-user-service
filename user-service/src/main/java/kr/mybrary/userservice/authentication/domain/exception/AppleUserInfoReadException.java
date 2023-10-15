package kr.mybrary.userservice.authentication.domain.exception;

import kr.mybrary.userservice.global.exception.ApplicationException;

public class AppleUserInfoReadException extends ApplicationException {

    private static final int STATUS = 500;
    private static final String ERROR_CODE = "A-10";
    private static final String ERROR_MESSAGE = "Apple 사용자 정보를 읽어오는데 실패했습니다.";

    public AppleUserInfoReadException() {
        super(STATUS, ERROR_CODE, ERROR_MESSAGE);
    }

}
