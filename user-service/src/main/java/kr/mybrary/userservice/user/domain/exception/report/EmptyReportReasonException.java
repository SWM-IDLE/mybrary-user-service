package kr.mybrary.userservice.user.domain.exception.report;

import kr.mybrary.userservice.global.exception.ApplicationException;

public class EmptyReportReasonException extends ApplicationException {

    private static final int STATUS = 400;
    private static final String ERROR_CODE = "UR-02";
    private static final String ERROR_MESSAGE = "신고 사유가 없습니다. 신고 사유를 입력해주세요.";

    public EmptyReportReasonException() {
        super(STATUS, ERROR_CODE, ERROR_MESSAGE);
    }
}
