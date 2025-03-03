package com.club_board.club_board_server.controller.mypage;
import com.club_board.club_board_server.dto.myPage.userInfo.UserInfoResponse;
import com.club_board.club_board_server.dto.myPage.userInfo.UpdateUserInfoRequest;
import com.club_board.club_board_server.response.ResponseBody;
import com.club_board.club_board_server.response.ResponseUtil;
import com.club_board.club_board_server.service.myPage.MyPageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/myPage")
public class MyPageController {

    private final MyPageService myPageService;

    @GetMapping("/{userId}")
    @PreAuthorize("@tokenProvider.getUserIdFromToken(authentication.getCredentials(), #userId)")
    public ResponseEntity<ResponseBody<UserInfoResponse>> showMyPage(@PathVariable("userId") @P("userId") Long userId){
        UserInfoResponse userInfoResponse =myPageService.getMyPage(userId);
        return ResponseEntity.ok(ResponseUtil.createSuccessResponse(userInfoResponse));
    }

    @PatchMapping("/{userId}")
    @PreAuthorize("@tokenProvider.getUserIdFromToken(authentication.getCredentials(), #userId)")
    public ResponseEntity<ResponseBody<String>> updateMyPage(@PathVariable("userId") @P("userId") Long userId, @RequestBody UpdateUserInfoRequest updateUserInfoRequest){
        myPageService.updateMyPage(userId, updateUserInfoRequest);
        return ResponseEntity.ok(ResponseUtil.createSuccessResponse("유저 정보 업데이트 성공"));
    }
}


