package kr.mybrary.userservice.user.domain.exception.user;

import kr.mybrary.userservice.global.exception.ApplicationException;

public class UserNotFoundException extends ApplicationException {

    private static final int STATUS = 404;
    private static final String ERROR_CODE = "U-01";
    private static final String ERROR_MESSAGE = "존재하지 않는 사용자입니다.";

    public UserNotFoundException() {
        super(STATUS, ERROR_CODE, ERROR_MESSAGE);
    }

}
