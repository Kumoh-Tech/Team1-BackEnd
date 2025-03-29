package com.club_board.club_board_server.controller.mypage;

import com.club_board.club_board_server.domain.user.CustomUserDetails;
import com.club_board.club_board_server.dto.myPage.book.MyLoanResponse;
import com.club_board.club_board_server.dto.myPage.book.MyReservationResponse;
import com.club_board.club_board_server.dto.myPage.book.MyReturnResponse;
import com.club_board.club_board_server.response.ResponseBody;
import com.club_board.club_board_server.response.ResponseUtil;
import com.club_board.club_board_server.service.myPage.MyLoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/myPage")
@RequiredArgsConstructor
public class MyLoanController {

    private final MyLoanService myLoanService;
    @GetMapping("/loan")
    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_ADMIN','ROLE_OWNER')")
    public ResponseEntity<ResponseBody<List<MyLoanResponse>>> getLoanStatus(@AuthenticationPrincipal CustomUserDetails customUserDetails){
        Long userId=customUserDetails.getUser().getId();
        List<MyLoanResponse> myLoanResponse=myLoanService.getMyLoan(userId);
        return ResponseEntity.ok(ResponseUtil.createSuccessResponse(myLoanResponse));
    }

    @GetMapping("/reservation")
    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_ADMIN','ROLE_OWNER')")
    public ResponseEntity<ResponseBody<List<MyReservationResponse>>> getReservationStatus(@AuthenticationPrincipal CustomUserDetails customUserDetails){
        Long userId=customUserDetails.getUser().getId();
        List<MyReservationResponse> myReservationResponse=myLoanService.getMyReservations(userId);
        return ResponseEntity.ok(ResponseUtil.createSuccessResponse(myReservationResponse));
    }

    @GetMapping("/return")
    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_ADMIN','ROLE_OWNER')")
    public ResponseEntity<ResponseBody<List<MyReturnResponse>>> getReturnStatus(@AuthenticationPrincipal CustomUserDetails customUserDetails){
        Long userId=customUserDetails.getUser().getId();
        List<MyReturnResponse> myReturnResponse=myLoanService.getMyReturn(userId);
        return ResponseEntity.ok(ResponseUtil.createSuccessResponse(myReturnResponse));
    }
}
