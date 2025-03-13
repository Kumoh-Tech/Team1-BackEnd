package com.club_board.club_board_server.controller.mypage;
import com.club_board.club_board_server.domain.user.CustomUserDetails;
import com.club_board.club_board_server.dto.myPage.userInfo.UserInfoResponse;
import com.club_board.club_board_server.dto.myPage.userInfo.UpdateUserInfoRequest;
import com.club_board.club_board_server.response.ResponseBody;
import com.club_board.club_board_server.response.ResponseUtil;
import com.club_board.club_board_server.service.myPage.MyPageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/myPage")
public class MyPageController {

    private final MyPageService myPageService;

    @GetMapping()
    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_ADMIN','ROLE_OWNER')")
    public ResponseEntity<ResponseBody<UserInfoResponse>> showMyPage(@AuthenticationPrincipal CustomUserDetails customUserDetails){
        Long userId=customUserDetails.getUser().getId();
        UserInfoResponse userInfoResponse =myPageService.getMyPage(userId);
        return ResponseEntity.ok(ResponseUtil.createSuccessResponse(userInfoResponse));
    }

    @PatchMapping()
    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_ADMIN','ROLE_OWNER')")
    public ResponseEntity<ResponseBody<String>> updateMyPage(@AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestBody UpdateUserInfoRequest updateUserInfoRequest){
        Long userId=customUserDetails.getUser().getId();
        myPageService.updateMyPage(userId, updateUserInfoRequest);
        return ResponseEntity.ok(ResponseUtil.createSuccessResponse("유저 정보 업데이트 성공"));
    }
    @DeleteMapping
    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_ADMIN','ROLE_OWNER')")
    public ResponseEntity<ResponseBody<?>> softDeleteAccount(@AuthenticationPrincipal CustomUserDetails customUserDetails){
        Long userId=customUserDetails.getUser().getId();
        myPageService.softDeleteAccount(userId);
        return ResponseEntity.ok(ResponseUtil.createSuccessResponse("회원탈퇴 완료"));
    }
}


