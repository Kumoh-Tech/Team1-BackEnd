package com.club_board.club_board_server.controller.auth;
import com.club_board.club_board_server.dto.auth.ResetPasswordRequest;
import com.club_board.club_board_server.dto.auth.UserLoginRequest;
import com.club_board.club_board_server.dto.auth.UserLoginResponse;
import com.club_board.club_board_server.response.ResponseBody;
import com.club_board.club_board_server.response.ResponseUtil;
import com.club_board.club_board_server.service.auth.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ResponseBody<UserLoginResponse>> login(@Valid @RequestBody UserLoginRequest loginRequest){
        log.info("로그인 컨트롤러 진입");
        UserLoginResponse userLoginResponse= authService.login(loginRequest);
        return ResponseEntity.ok(ResponseUtil.createSuccessResponse(userLoginResponse));
    }
    @PostMapping("/login/password")
    public ResponseEntity<ResponseBody<String>> findPassword(@RequestBody ResetPasswordRequest resetPasswordRequest) {
        authService.forgotPassword(resetPasswordRequest);
        return ResponseEntity.ok(ResponseUtil.createSuccessResponse("임시 비밀번호 생성 완료"));
    }
    @PreAuthorize("@tokenProvider.hasRoleInClub(#clubId,'일반회원')")
    @PostMapping("/club/{clubId}/manage")
    public ResponseEntity<ResponseBody<String>> manageClub(@PathVariable Long clubId) {
        // 동아리 관리 로직
        return ResponseEntity.ok(ResponseUtil.createSuccessResponse("동아리 관리 화면 접근 성공"));
    }


}
