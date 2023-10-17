package kr.mybrary.userservice.authentication.presentation;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.mybrary.userservice.authentication.domain.AuthenticationService;
import kr.mybrary.userservice.authentication.domain.oauth2.service.AppleOAuth2UserService;
import kr.mybrary.userservice.global.dto.response.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/auth/v1")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final AppleOAuth2UserService appleOAuth2UserService;

    @GetMapping("/refresh")
    public ResponseEntity<SuccessResponse<Void>> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        authenticationService.reIssueToken(request, response);

        return ResponseEntity.status(200).body(
                SuccessResponse.of(HttpStatus.CREATED.toString(), "토큰 재발급에 성공했습니다.", null)
        );
    }

    @PostMapping("/apple/callback")
    public void callback(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.sendRedirect(appleOAuth2UserService.authenticateWithApple(request));
    }

}
