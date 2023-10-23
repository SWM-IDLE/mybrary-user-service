package kr.mybrary.userservice.authentication.domain.exception;

import kr.mybrary.userservice.global.exception.ApplicationException;

public class AppleClientSecretFormatException extends ApplicationException {

    private static final int STATUS = 500;
    private static final String ERROR_CODE = "A-11";
    private static final String ERROR_MESSAGE = "Apple_Client_Secret_Format_Error";

    public AppleClientSecretFormatException() {
        super(STATUS, ERROR_CODE, ERROR_MESSAGE);
    }

}
