package kr.mybrary.userservice.authentication.domain.oauth2.userinfo;

import java.util.Map;

public class AppleOAuth2UserInfo extends OAuth2UserInfo {

    public AppleOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getId() {
        return (String) attributes.get("id_token");
    }

    @Override
    public String getNickname() {
        Map<String, Object> name = getName();
        return (String) name.get("lastName")+name.get("firstName");
    }

    @Override
    public String getEmail() {
        Map<String, Object> user = getUser();
        if (user == null) {
            return null;
        }
        return (String) user.get("email");
    }

    private Map<String, Object> getName() {
        Map<String, Object> user = getUser();
        if (user == null) {
            return null;
        }
        Map<String, Object> name = (Map<String, Object>) user.get("name");
        return name;
    }

    private Map<String, Object> getUser() {
        Map<String, Object> user = (Map<String, Object>) attributes.get("user");
        return user;
    }

}
