package kr.mybrary.userservice.client.apple.api;

import feign.Headers;
import kr.mybrary.userservice.client.apple.dto.AppleOAuth2TokenServiceResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "appleOAuth2Client")
public interface AppleOAuth2ServiceClient {

    @PostMapping("/auth/token")
    @Headers("Content-Type: application/x-www-form-urlencoded")
    AppleOAuth2TokenServiceResponse getAppleToken(
            @RequestParam String client_id,
            @RequestParam String client_secret,
            @RequestParam String code,
            @RequestParam String grant_type,
            @RequestParam String redirect_uri
    );

    @PostMapping("/auth/revoke")
    @Headers("Content-Type: application/x-www-form-urlencoded")
    void revokeAppleToken(
            @RequestParam String client_id,
            @RequestParam String client_secret,
            @RequestParam String token,
            @RequestParam String token_type_hint
    );

}
