package kr.mybrary.userservice.interest.domain.exception;

import kr.mybrary.userservice.global.exception.ApplicationException;

public class InterestNotFoundException extends ApplicationException {

    private static final int STATUS = 404;
    private static final String ERROR_CODE = "I-01";
    private static final String ERROR_MESSAGE = "관심사를 찾을 수 없습니다.";

    public InterestNotFoundException() {
        super(STATUS, ERROR_CODE, ERROR_MESSAGE);
    }

}