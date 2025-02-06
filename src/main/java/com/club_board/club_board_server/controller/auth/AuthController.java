package com.club_board.club_board_server.controller.auth;
import com.club_board.club_board_server.dto.auth.ResetPasswordRequest;
import com.club_board.club_board_server.dto.auth.UserLoginRequest;
import com.club_board.club_board_server.dto.auth.UserLoginResponse;
import com.club_board.club_board_server.response.ResponseBody;
import com.club_board.club_board_server.response.ResponseUtil;
import com.club_board.club_board_server.service.auth.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
@Slf4j
@RequiredArgsConstructor
@RestController
public class AuthController {

    private final AuthService authService;

    /*
    로그인
     */
    @PostMapping("/login")
    public ResponseEntity<ResponseBody<UserLoginResponse>> login(@Valid @RequestBody UserLoginRequest loginRequest,
                                                                 HttpServletRequest request, HttpServletResponse response){
        UserLoginResponse userLoginResponse= authService.login(loginRequest,request,response);
        return ResponseEntity.ok(ResponseUtil.createSuccessResponse(userLoginResponse));
    }
    /*
    임시 비밀번호
     */
    @PostMapping("/login/password")
    public ResponseEntity<ResponseBody<String>> findPassword(@RequestBody ResetPasswordRequest resetPasswordRequest) {
        authService.forgotPassword(resetPasswordRequest);
        return ResponseEntity.ok(ResponseUtil.createSuccessResponse("임시 비밀번호 생성 완료"));
    }
    /*
    로그아웃
     */
    @PostMapping("/logout")
    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_ADMIN','ROLE_OWNER')")
    public ResponseEntity<ResponseBody<String>> logout(@CookieValue(value = "refresh-token", required = false) String refreshToken,
                                                       HttpServletResponse response) {
        authService.logout(refreshToken, response);
        return ResponseEntity.ok(ResponseUtil.createSuccessResponse("로그아웃 성공"));
    }
}

