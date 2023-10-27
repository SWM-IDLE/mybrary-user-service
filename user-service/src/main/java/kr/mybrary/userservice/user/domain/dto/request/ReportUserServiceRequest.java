package kr.mybrary.userservice.user.domain.dto.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReportUserServiceRequest {

    private String reporterUserId;
    private String reportedUserId;
    private String reportReason;

    public static ReportUserServiceRequest of(String reporterUserId, String reportedUserId, String reportReason) {
        return ReportUserServiceRequest.builder()
                .reporterUserId(reporterUserId)
                .reportedUserId(reportedUserId)
                .reportReason(reportReason)
                .build();
    }
}
