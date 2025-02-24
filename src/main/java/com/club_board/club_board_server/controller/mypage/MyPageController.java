package com.club_board.club_board_server.controller.mypage;
import com.club_board.club_board_server.dto.myPage.MyPageResponse;
import com.club_board.club_board_server.dto.myPage.UpdateMyPageRequest;
import com.club_board.club_board_server.response.ResponseBody;
import com.club_board.club_board_server.response.ResponseUtil;
import com.club_board.club_board_server.service.myPage.MyPageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MyPageController {

    private final MyPageService myPageService;

    @GetMapping("/myPage/{userId}")
    @PreAuthorize("@tokenProvider.getUserIdFromToken(authentication.getCredentials(), #userId)")
    public ResponseEntity<ResponseBody<MyPageResponse>> showMyPage(@PathVariable("userId") @P("userId") Long userId){
        MyPageResponse myPageResponse=myPageService.getMyPage(userId);
        return ResponseEntity.ok(ResponseUtil.createSuccessResponse(myPageResponse));
    }

    @PatchMapping("/myPage/{userId}")
    @PreAuthorize("@tokenProvider.getUserIdFromToken(authentication.getCredentials(), #userId)")
    public ResponseEntity<ResponseBody<String>> updateMyPage(@PathVariable("userId") @P("userId") Long userId, @Valid @RequestBody UpdateMyPageRequest updateMyPageRequest){
        myPageService.updateMyPage(userId,updateMyPageRequest);
        return ResponseEntity.ok(ResponseUtil.createSuccessResponse("유저 정보 업데이트 성공"));
    }
}


