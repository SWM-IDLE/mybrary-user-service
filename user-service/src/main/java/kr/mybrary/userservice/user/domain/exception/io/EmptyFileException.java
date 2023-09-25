package kr.mybrary.userservice.user.domain.exception.io;

import kr.mybrary.userservice.global.exception.ApplicationException;

public class EmptyFileException extends ApplicationException {

    private static final int STATUS = 400;
    private static final String ERROR_CODE = "IO-01";
    private static final String ERROR_MESSAGE = "파일이 비어있습니다. 파일을 선택해주세요.";

    public EmptyFileException() {
        super(STATUS, ERROR_CODE, ERROR_MESSAGE);
    }

}
