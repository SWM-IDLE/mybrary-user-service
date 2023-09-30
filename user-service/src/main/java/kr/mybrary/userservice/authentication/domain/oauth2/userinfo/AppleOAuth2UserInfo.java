package kr.mybrary.userservice.authentication.domain.oauth2.userinfo;

import java.util.Map;

public class AppleOAuth2UserInfo extends OAuth2UserInfo {

    public AppleOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getId() {
        return (String) attributes.get("sub");
    }

    @Override
    public String getNickname() {
        // TODO: Apple에서 사용자 이름을 제공받을 수 있도록 수정 예정
        return "User";
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }

}
