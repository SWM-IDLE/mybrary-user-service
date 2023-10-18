package kr.mybrary.userservice.authentication.domain.exception;

import kr.mybrary.userservice.global.exception.ApplicationException;

public class AppleClientSecretNotCreatedException extends ApplicationException {

    private static final int STATUS = 500;
    private static final String ERROR_CODE = "A-07";
    private static final String ERROR_MESSAGE = "Apple Client Secret을 생성하는데 실패했습니다.";

    public AppleClientSecretNotCreatedException() {
        super(STATUS, ERROR_CODE, ERROR_MESSAGE);
    }

}
