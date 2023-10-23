package kr.mybrary.userservice.authentication.domain.exception;

import kr.mybrary.userservice.global.exception.ApplicationException;

public class AppleCodeNotFoundException extends ApplicationException {

    private static final int STATUS = 404;
    private static final String ERROR_CODE = "A-06";
    private static final String ERROR_MESSAGE = "Apple_Code_Not_Found";

    public AppleCodeNotFoundException() {
        super(STATUS, ERROR_CODE, ERROR_MESSAGE);
    }

}
