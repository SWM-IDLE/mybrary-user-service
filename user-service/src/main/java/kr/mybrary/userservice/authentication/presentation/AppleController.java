package kr.mybrary.userservice.authentication.presentation;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.mybrary.userservice.authentication.domain.oauth2.apple.AppleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AppleController {

    private final AppleService appleService;

    @PostMapping("/apple/callback")
    public void callback(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.sendRedirect(appleService.getAppleInfo(request));
    }

}
