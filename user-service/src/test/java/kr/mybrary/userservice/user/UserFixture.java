package kr.mybrary.userservice.user;

import kr.mybrary.userservice.user.persistence.*;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@AllArgsConstructor
public enum UserFixture {

    COMMON_USER(1L, "loginId", "nickname", "encodedPassword", Role.USER,
            "socialId", SocialType.GOOGLE, "email@mail.com", "introduction", "profileImageUrl",
            "profileImageThumbnailTinyUrl", "profileImageThumbnailSmallUrl",
            Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), Collections.emptyList()),

    APPLE_USER(1L, "loginId", "nickname", "encodedPassword", Role.USER,
            "socialId", SocialType.APPLE, "email@mail.com", "introduction", "profileImageUrl",
            "profileImageThumbnailTinyUrl", "profileImageThumbnailSmallUrl",
            Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), Collections.emptyList()),
    
    USER_WITHOUT_PROFILE_IMAGE_URL(1L, "loginId", "nickname", "encodedPassword", Role.USER,
            "socialId", SocialType.GOOGLE, "email@mail.com", "introduction", null,
            "profileImageThumbnailTinyUrl", "profileImageThumbnailSmallUrl",
            Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), Collections.emptyList()),

    USER_WITHOUT_EMAIL(1L, "loginId", "nickname", "encodedPassword", Role.USER,
            "socialId", SocialType.GOOGLE, null, "introduction", "profileImageUrl",
            "profileImageThumbnailTinyUrl", "profileImageThumbnailSmallUrl",
            Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), Collections.emptyList()),

    USER_WITH_FOLLOWER(1L, "followingId", "nickname", "encodedPassword", Role.USER,
            "socialId", SocialType.GOOGLE, "email@mail.com", "introduction", "profileImageUrl",
            "profileImageThumbnailTinyUrl", "profileImageThumbnailSmallUrl",
            List.of(Follow.builder().source(User.builder().loginId("followerId").build()).target(User.builder().loginId("followingId").build()).build()),
            Collections.emptyList(), Collections.emptyList(), Collections.emptyList()),

    USER_AS_FOLLOWER(1L, "followerId", "nickname", "encodedPassword", Role.USER,
            "socialId", SocialType.GOOGLE, "email@mail.com", "introduction", "profileImageUrl",
            "profileImageThumbnailTinyUrl", "profileImageThumbnailSmallUrl",
            Collections.emptyList(),
            List.of(Follow.builder().source(User.builder().loginId("followerId").build()).target(User.builder().loginId("followingId").build()).build()),
            Collections.emptyList(), Collections.emptyList()),

    USER_WITH_SIMILAR_NICKNAME(2L, "loginId123", "nickname123", "encodedPassword", Role.USER,
            "socialId123", SocialType.GOOGLE, "email@mail.com", "introduction", "profileImageUrl",
            "profileImageThumbnailTinyUrl", "profileImageThumbnailSmallUrl",
            Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), Collections.emptyList()),

    USER_WITH_NO_EMAIL(1L, "loginId", "nickname", "encodedPassword", Role.USER,
            "socialId", SocialType.GOOGLE, null, "introduction", "profileImageUrl",
            "profileImageThumbnailTinyUrl", "profileImageThumbnailSmallUrl",
            Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), Collections.emptyList()),

    REPORTER_USER(1L, "reporterUserId", "nickname", "encodedPassword", Role.USER,
            "socialId", SocialType.GOOGLE, "email@mail.com", "introduction", "profileImageUrl",
            "profileImageThumbnailTinyUrl", "profileImageThumbnailSmallUrl",
            Collections.emptyList(), Collections.emptyList(), new ArrayList<>(), Collections.emptyList()),

    REPORTED_USER(1L, "reportedUserId", "nickname", "encodedPassword", Role.USER,
            "socialId", SocialType.GOOGLE, "email@mail.com", "introduction", "profileImageUrl",
                        "profileImageThumbnailTinyUrl", "profileImageThumbnailSmallUrl",
                Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), new ArrayList<>());

    private final Long id;
    private final String loginId;
    private final String nickname;
    private final String password;
    private final Role role;
    private final String socialId;
    private final SocialType socialType;
    private final String email;
    private final String introduction;
    private final String profileImageUrl;
    private final String profileImageThumbnailTinyUrl;
    private final String profileImageThumbnailSmallUrl;
    private final List<Follow> followers;
    private final List<Follow> followings;
    private final List<UserReport> userReports;
    private final List<UserReport> reporteds;

    public User getUser() {
        return User.builder()
                .id(id)
                .loginId(loginId)
                .nickname(nickname)
                .password(password)
                .role(role)
                .socialId(socialId)
                .socialType(socialType)
                .email(email)
                .introduction(introduction)
                .profileImageUrl(profileImageUrl)
                .profileImageThumbnailTinyUrl(profileImageThumbnailTinyUrl)
                .profileImageThumbnailSmallUrl(profileImageThumbnailSmallUrl)
                .followers(followers)
                .followings(followings)
                .userReports(userReports)
                .reporteds(reporteds)
                .build();
    }

}
