package kr.mybrary.userservice.client.apple.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AppleOAuth2TokenServiceResponse {

    private String access_token;
    private String token_type;
    private Long expires_in;
    private String refresh_token;
    private String id_token;

}
