package kr.mybrary.userservice.authentication.domain.oauth2.userinfo;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AppleOAuth2UserInfo {

    private Name name;
    private String email;

    @Getter
    @Builder
    public static class Name {
        private String firstName;
        private String lastName;
    }

}
