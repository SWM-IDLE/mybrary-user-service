package kr.mybrary.userservice.user.presentation;

import jakarta.validation.Valid;
import kr.mybrary.userservice.global.dto.response.FeignClientResponse;
import kr.mybrary.userservice.global.dto.response.SuccessResponse;
import kr.mybrary.userservice.user.domain.UserService;
import kr.mybrary.userservice.user.domain.dto.request.*;
import kr.mybrary.userservice.user.domain.dto.response.*;
import kr.mybrary.userservice.user.presentation.dto.request.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    @PostMapping("/sign-up")
    public ResponseEntity<SuccessResponse<SignUpServiceResponse>> signUp(
            @Valid @RequestBody SignUpRequest signUpRequest) {
        return ResponseEntity.status(200).body(
                SuccessResponse.of(HttpStatus.CREATED.toString(), "회원 가입에 성공했습니다.",
                        userService.signUp(SignUpServiceRequest.of(signUpRequest)))
        );
    }

    @GetMapping("/{userId}/profile")
    public ResponseEntity<SuccessResponse<ProfileServiceResponse>> getProfile(
            @PathVariable("userId") String userId) {
        return ResponseEntity.ok().body(
                SuccessResponse.of(HttpStatus.OK.toString(), "사용자의 프로필 정보입니다.",
                        userService.getProfile(userId))
        );
    }

    @PutMapping("/{userId}/profile")
    public ResponseEntity<SuccessResponse<ProfileServiceResponse>> updateProfile(
            @PathVariable("userId") String userId,
            @RequestHeader("USER-ID") String loginId,
            @Valid @RequestBody ProfileUpdateRequest profileUpdateRequest) {
        return ResponseEntity.ok().body(
                SuccessResponse.of(HttpStatus.OK.toString(), "로그인 된 사용자의 프로필 정보를 수정했습니다.",
                        userService.updateProfile(ProfileUpdateServiceRequest.of(profileUpdateRequest, userId, loginId)))
        );
    }

    @GetMapping("/{userId}/profile/image")
    public ResponseEntity<SuccessResponse<ProfileImageUrlServiceResponse>> getProfileImageUrl(
            @PathVariable("userId") String userId,
            @RequestParam(value = "size", required = false, defaultValue = "original") String size) {
        return ResponseEntity.ok().body(
                SuccessResponse.of(HttpStatus.OK.toString(), "사용자의 프로필 이미지 URL입니다.",
                        userService.getProfileImageUrl(ProfileImageUrlServiceRequest.of(userId, size))));
    }

    @PutMapping("/{userId}/profile/image")
    public ResponseEntity<SuccessResponse<ProfileImageUrlServiceResponse>> updateProfileImage(
            @PathVariable("userId") String userId,
            @RequestHeader("USER-ID") String loginId,
            @RequestParam("profileImage") MultipartFile profileImage) {
        return ResponseEntity.ok().body(
                SuccessResponse.of(HttpStatus.OK.toString(), "로그인 된 사용자의 프로필 이미지를 등록했습니다.",
                        userService.updateProfileImage(ProfileImageUpdateServiceRequest.of(profileImage, loginId, userId, LocalDateTime.now()))));
    }

    @DeleteMapping("/{userId}/profile/image")
    public ResponseEntity<SuccessResponse<ProfileImageUrlServiceResponse>> deleteProfileImage(
            @PathVariable("userId") String userId,
            @RequestHeader("USER-ID") String loginId) {
        return ResponseEntity.ok().body(
                SuccessResponse.of(HttpStatus.OK.toString(), "로그인 된 사용자의 프로필 이미지를 삭제했습니다.",
                        userService.deleteProfileImage(ProfileImageUpdateServiceRequest.of(loginId, userId)))
        );
    }

    @GetMapping("/{userId}/profile/email")
    public ResponseEntity<SuccessResponse<ProfileEmailServiceResponse>> getProfileEmail(
            @PathVariable("userId") String userId) {
        return ResponseEntity.ok().body(
                SuccessResponse.of(HttpStatus.OK.toString(), "사용자의 로그인된 이메일 계정을 조회했습니다.",
                        userService.getProfileEmail(userId))
        );
    }

    @GetMapping("/{userId}/followers")
    public ResponseEntity<SuccessResponse<FollowerServiceResponse>> getFollowers(
            @PathVariable("userId") String userId) {
        return ResponseEntity.ok().body(
                SuccessResponse.of(HttpStatus.OK.toString(), "사용자의 팔로워 목록을 조회했습니다.",
                        userService.getFollowers(userId))
        );
    }

    @GetMapping("/{userId}/followings")
    public ResponseEntity<SuccessResponse<FollowingServiceResponse>> getFollowings(
            @PathVariable("userId") String userId) {
        return ResponseEntity.ok().body(
                SuccessResponse.of(HttpStatus.OK.toString(), "사용자의 팔로잉 목록을 조회했습니다.",
                        userService.getFollowings(userId))
        );
    }

    @PostMapping("/follow")
    public ResponseEntity<SuccessResponse<Void>> follow(
            @RequestHeader("USER-ID") String loginId,
            @RequestBody FollowRequest followRequest) {
        userService.follow(FollowServiceRequest.of(loginId, followRequest.getTargetId()));

        return ResponseEntity.ok().body(
                SuccessResponse.of(HttpStatus.OK.toString(), "사용자를 팔로우했습니다.", null)
        );
    }

    @DeleteMapping("/follow")
    public ResponseEntity<SuccessResponse<Void>> unfollow(
            @RequestHeader("USER-ID") String loginId,
            @RequestBody FollowRequest followRequest) {
        userService.unfollow(FollowServiceRequest.of(loginId, followRequest.getTargetId()));

        return ResponseEntity.ok().body(
                SuccessResponse.of(HttpStatus.OK.toString(), "사용자를 언팔로우했습니다.", null)
        );
    }

    @DeleteMapping("/follower")
    public ResponseEntity<SuccessResponse<Void>> unfollowing(
            @RequestHeader("USER-ID") String loginId,
            @RequestBody FollowerRequest followerRequest) {
        userService.deleteFollower(FollowerServiceRequest.of(loginId, followerRequest.getSourceId()));

        return ResponseEntity.ok().body(
                SuccessResponse.of(HttpStatus.OK.toString(), "사용자를 팔로워 목록에서 삭제했습니다.", null)
        );
    }

    @GetMapping("/follow")
    public ResponseEntity<SuccessResponse<FollowStatusServiceResponse>> isFollowing(
            @RequestHeader("USER-ID") String loginId,
            @RequestParam("targetId") String targetId) {
        return ResponseEntity.ok().body(
                SuccessResponse.of(HttpStatus.OK.toString(), "사용자를 팔로우 중인지 확인했습니다.",
                        userService.getFollowStatus(FollowServiceRequest.of(loginId, targetId)))
        );
    }

    @GetMapping("/search")
    public ResponseEntity<SuccessResponse<SearchServiceResponse>> searchByNickname(
            @RequestParam(value = "nickname") String nickname) {
        return ResponseEntity.ok().body(
                SuccessResponse.of(HttpStatus.OK.toString(), "닉네임으로 사용자를 검색했습니다.",
                        userService.searchByNickname(nickname))
        );
    }

    @DeleteMapping("/account")
    public ResponseEntity<SuccessResponse<Void>> withdrawal(
            @RequestHeader("USER-ID") String loginId) {
        userService.deleteAccount(loginId);

        return ResponseEntity.ok().body(
                SuccessResponse.of(HttpStatus.OK.toString(), "회원 탈퇴에 성공했습니다.", null)
        );
    }

    @GetMapping("/info")
    public ResponseEntity<FeignClientResponse<UserInfoServiceResponse>> getUserInfoCalledByFeignClient(
            @RequestParam("userId") List<String> userIds) {
        return ResponseEntity.ok().body(
                FeignClientResponse.of(userService.getUserInfo(UserInfoServiceRequest.of(userIds)))
        );
    }

}
