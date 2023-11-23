package kr.mybrary.userservice.global.publisher.message;


import java.util.HashMap;
import java.util.Map;
import kr.mybrary.userservice.user.persistence.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class SnsMessageCreator {

    public static Map<String, Object> createFollowMessage(User sourceUser, User targetUser) {

        return new HashMap<>(){{
            put("sourceUserId", sourceUser.getLoginId());
            put("targetUserId", targetUser.getLoginId());
            put("targetUserNickname", targetUser.getNickname());
            put("targetUserProfileImageUrl", targetUser.getProfileImageUrl());
        }};
    }
}
