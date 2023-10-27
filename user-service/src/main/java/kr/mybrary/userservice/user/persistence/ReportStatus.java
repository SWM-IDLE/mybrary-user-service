package kr.mybrary.userservice.user.persistence;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReportStatus {

    WAITING("신고 대기"),
    PROCESSING("신고 처리중"),
    COMPLETE("신고 처리 완료");

    private final String description;

}