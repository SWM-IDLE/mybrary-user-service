package kr.mybrary.userservice.authentication.domain.oauth2.userinfo;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class AppleOAuth2TokenInfo {

    private String id;
    private String token;
    private String email;
    private String fullName;

}
