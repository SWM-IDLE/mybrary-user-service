package kr.mybrary.userservice.user.domain.exception.report;

import kr.mybrary.userservice.global.exception.ApplicationException;

public class SameReporterReportedUserException extends ApplicationException {

    private static final int STATUS = 400;
    private static final String ERROR_CODE = "UR-01";
    private static final String ERROR_MESSAGE = "자기 자신을 신고할 수 없습니다.";

    public SameReporterReportedUserException() {
        super(STATUS, ERROR_CODE, ERROR_MESSAGE);
    }
}
