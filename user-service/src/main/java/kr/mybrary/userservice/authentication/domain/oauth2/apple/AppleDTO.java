package kr.mybrary.userservice.authentication.domain.oauth2.apple;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class AppleDTO {

    private String id;
    private String token;
    private String email;
    private String fullName;

}
