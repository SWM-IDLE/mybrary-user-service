package kr.mybrary.userservice.authentication.domain.exception;

import kr.mybrary.userservice.global.exception.ApplicationException;

public class AppleTokenParseException extends ApplicationException {

    private static final int STATUS = 500;
    private static final String ERROR_CODE = "A-09";
    private static final String ERROR_MESSAGE = "Apple 토큰을 파싱하는데 실패했습니다.";

    public AppleTokenParseException() {
        super(STATUS, ERROR_CODE, ERROR_MESSAGE);
    }

}
