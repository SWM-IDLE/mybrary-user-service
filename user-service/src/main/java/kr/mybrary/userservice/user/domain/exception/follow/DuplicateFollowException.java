package kr.mybrary.userservice.user.domain.exception.follow;

import kr.mybrary.userservice.global.exception.ApplicationException;

public class DuplicateFollowException extends ApplicationException {

        private static final int STATUS = 400;
        private static final String ERROR_CODE = "F-02";
        private static final String ERROR_MESSAGE = "이미 팔로우한 사용자입니다.";

        public DuplicateFollowException() {
            super(STATUS, ERROR_CODE, ERROR_MESSAGE);
        }
}
