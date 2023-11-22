package kr.mybrary.userservice.global.publisher.message;


import java.util.Map;
import kr.mybrary.userservice.user.persistence.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SnsMessageCreator {

    public static Map<String, Object> createFollowMessage(User sourceUser, User targetUser) {

        return Map.of(
                "sourceUserId", sourceUser.getId(),
                "targetUserId", targetUser.getId(),
                "targetUserNickname", targetUser.getNickname(),
                "targetUserProfileImageUrl", targetUser.getProfileImageUrl()
        );
    }
}
