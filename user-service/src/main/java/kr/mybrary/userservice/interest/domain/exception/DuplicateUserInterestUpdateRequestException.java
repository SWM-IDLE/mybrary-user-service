package kr.mybrary.userservice.interest.domain.exception;

import kr.mybrary.userservice.global.exception.ApplicationException;

public class DuplicateUserInterestUpdateRequestException extends ApplicationException {

    private static final int STATUS = 400;
    private static final String ERROR_CODE = "I-03";
    private static final String ERROR_MESSAGE = "관심사는 중복해서 설정할 수 없습니다.";

    public DuplicateUserInterestUpdateRequestException() {
        super(STATUS, ERROR_CODE, ERROR_MESSAGE);
    }

}
