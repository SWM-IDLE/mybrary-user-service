package kr.mybrary.userservice.authentication.domain.exception;

import kr.mybrary.userservice.global.exception.ApplicationException;

public class AppleClientSecretFormatException extends ApplicationException {

    private static final int STATUS = 500;
    private static final String ERROR_CODE = "A-11";
    private static final String ERROR_MESSAGE = "Apple Client Secret 설정값의 형식이 올바르지 않습니다.";

    public AppleClientSecretFormatException() {
        super(STATUS, ERROR_CODE, ERROR_MESSAGE);
    }

}
