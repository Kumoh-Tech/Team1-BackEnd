package com.club_board.club_board_server.controller.user;
import com.club_board.club_board_server.dto.mail.MailRequest;
import com.club_board.club_board_server.dto.mail.MailVerifyRequest;
import com.club_board.club_board_server.dto.user.UserRegisterRequest;
import com.club_board.club_board_server.response.ResponseBody;
import com.club_board.club_board_server.response.ResponseUtil;
import com.club_board.club_board_server.service.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/register")
public class RegisterController {
    private final UserService userService;

    @GetMapping()
    public ResponseEntity<ResponseBody<List<String>>> showRegisterForm(){
        List<String> department=userService.showRegisterForm();
        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseUtil.createSuccessResponse(department));
    }
    @PostMapping()
    public ResponseEntity<ResponseBody<String>> register(@Valid @RequestBody UserRegisterRequest userRegisterRequest){
        userService.register(userRegisterRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseUtil.createSuccessResponse("회원가입 성공"));
    }

    @PostMapping("/mail")
    public ResponseEntity<ResponseBody<String>> mailSend(@RequestBody MailRequest mailRequest){
        String mail=mailRequest.getUsername();
        //TODO: 추후 임시 비밀번호 sendMail랑 리팩토링 필요
        userService.isUsernameAvailable(mail);
        userService.sendMail(mail);
        return ResponseEntity.ok(ResponseUtil.createSuccessResponse("이메일 전송 성공"));
    }


    @PostMapping("/mail/verification")
    public ResponseEntity<ResponseBody<String>> verifyMail(@RequestBody MailVerifyRequest mailVerifyRequest) {
        userService.checkVerificationNumber(mailVerifyRequest);
        return ResponseEntity.ok(ResponseUtil.createSuccessResponse("인증이 완료되었습니다."));
    }
}
