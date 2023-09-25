package kr.mybrary.userservice.user.domain.dto.request;

import kr.mybrary.userservice.user.presentation.dto.request.UserInfoRequest;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class UserInfoServiceRequest {

    List<String> userIds;

    public static UserInfoServiceRequest of(List<String> userIds) {
        return UserInfoServiceRequest.builder()
                .userIds(userIds)
                .build();
    }

}
