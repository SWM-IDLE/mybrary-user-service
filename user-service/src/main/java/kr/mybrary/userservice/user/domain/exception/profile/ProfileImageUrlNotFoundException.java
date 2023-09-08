package kr.mybrary.userservice.user.domain.exception.profile;

import kr.mybrary.userservice.global.exception.ApplicationException;

public class ProfileImageUrlNotFoundException extends ApplicationException {

    private static final int STATUS = 404;
    private static final String ERROR_CODE = "P-01";
    private static final String ERROR_MESSAGE = "프로필 이미지 URL이 존재하지 않습니다.";

    public ProfileImageUrlNotFoundException() {
        super(STATUS, ERROR_CODE, ERROR_MESSAGE);
    }

}
