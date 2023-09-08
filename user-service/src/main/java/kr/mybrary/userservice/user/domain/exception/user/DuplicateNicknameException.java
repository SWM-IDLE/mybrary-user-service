package kr.mybrary.userservice.user.domain.exception.user;

import kr.mybrary.userservice.global.exception.ApplicationException;

public class DuplicateNicknameException extends ApplicationException {

    private static final int STATUS = 400;
    private static final String ERROR_CODE = "U-03";
    private static final String ERROR_MESSAGE = "이미 존재하는 닉네임입니다.";

    public DuplicateNicknameException() {
        super(STATUS, ERROR_CODE, ERROR_MESSAGE);
    }

}
