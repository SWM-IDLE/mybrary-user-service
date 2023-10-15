package kr.mybrary.userservice.authentication.domain.exception;

import kr.mybrary.userservice.global.exception.ApplicationException;

public class ApplePrivateKeyReadException extends ApplicationException {

    private static final int STATUS = 500;
    private static final String ERROR_CODE = "A-08";
    private static final String ERROR_MESSAGE = "Apple Private Key를 읽어오는데 실패했습니다.";

    public ApplePrivateKeyReadException() {
        super(STATUS, ERROR_CODE, ERROR_MESSAGE);
    }

}
