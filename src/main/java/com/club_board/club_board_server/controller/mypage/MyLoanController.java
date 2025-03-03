package com.club_board.club_board_server.controller.mypage;

import com.club_board.club_board_server.dto.myPage.book.MyLoanResponse;
import com.club_board.club_board_server.dto.myPage.book.MyReservationResponse;
import com.club_board.club_board_server.response.ResponseBody;
import com.club_board.club_board_server.response.ResponseUtil;
import com.club_board.club_board_server.service.myPage.MyLoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/myPage")
@RequiredArgsConstructor
public class MyLoanController {

    private final MyLoanService myLoanService;
    @GetMapping("/loan/{userId}")
    @PreAuthorize("@tokenProvider.getUserIdFromToken(authentication.getCredentials(), #userId)")
    public ResponseEntity<ResponseBody<List<MyLoanResponse>>> getLoanStatus(@PathVariable("userId") @P("userId") Long userId){
        List<MyLoanResponse> myLoanResponse=myLoanService.getMyLoan(userId);
        return ResponseEntity.ok(ResponseUtil.createSuccessResponse(myLoanResponse));
    }

    @GetMapping("/reservation/{userId}")
    @PreAuthorize("@tokenProvider.getUserIdFromToken(authentication.getCredentials(), #userId)")
    public ResponseEntity<ResponseBody<List<MyReservationResponse>>> getReservationStatus(@PathVariable("userId") @P("userId") Long userId){
        List<MyReservationResponse> myReservationResponse=myLoanService.getMyReservations(userId);
        return ResponseEntity.ok(ResponseUtil.createSuccessResponse(myReservationResponse));
    }

    @GetMapping("/return/{userId}")
    @PreAuthorize("@tokenProvider.getUserIdFromToken(authentication.getCredentials(), #userId)")
    public ResponseEntity<?> getReturnStatus(@PathVariable("userId") @P("userId") Long userId){
        List<MyLoanResponse> myLoanResponse=myLoanService.getMyLoan(userId);
        return ResponseEntity.ok(ResponseUtil.createSuccessResponse(myLoanResponse));
    }
}
