package com.club_board.club_board_server.controller.token;
import com.club_board.club_board_server.response.ResponseUtil;
import com.club_board.club_board_server.response.exception.BusinessException;
import com.club_board.club_board_server.response.exception.ExceptionType;
import com.club_board.club_board_server.service.auth.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
@Slf4j
@RequiredArgsConstructor
@RestController
public class RefreshTokenController {

    private final AuthService authService;

    @PostMapping("/refreshToken")
    public ResponseEntity<?> refreshToken(@CookieValue(value="refresh-token", required = false) String refreshToken,
                                          @RequestHeader(value = "User-Agent", required = false) String requestUserAgent,
                                          HttpServletResponse response) {
        if (refreshToken == null) {
            throw new BusinessException(ExceptionType.NOT_FOUND_REFRESH_TOKEN);
        }
        authService.validateAndHandleRefreshToken(refreshToken, response, requestUserAgent);
        return ResponseEntity.ok(ResponseUtil.createSuccessResponse(" Access Token 재발급 성공"));
    }
}
