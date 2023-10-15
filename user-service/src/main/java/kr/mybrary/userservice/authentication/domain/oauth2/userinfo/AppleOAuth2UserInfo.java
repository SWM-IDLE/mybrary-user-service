package kr.mybrary.userservice.authentication.domain.oauth2.userinfo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AppleOAuth2UserInfo {

    private Name name;
    private String email;

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Name {
        private String firstName;
        private String lastName;
    }

}
