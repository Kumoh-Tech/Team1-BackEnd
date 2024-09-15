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


@Slf4j
@RequiredArgsConstructor
@RestController
public class RegisterController {
    private final UserService userService;
    @PostMapping("/register")
    public ResponseEntity<ResponseBody<String>> register(@Valid @RequestBody UserRegisterRequest userRegisterRequest){
        userService.register(userRegisterRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseUtil.createSuccessResponse("회원가입 성공"));
    }

    @PostMapping("/register/mail")
    public ResponseEntity<ResponseBody<String>> mailSend(@RequestBody MailRequest mailRequest){
        String mail=mailRequest.getUsername();
        userService.sendMail(mail);
        return ResponseEntity.ok(ResponseUtil.createSuccessResponse("이메일 전송 성공"));
    }


    @PostMapping("/register/mail/verification")
    public ResponseEntity<ResponseBody<String>> verifyMail(@RequestBody MailVerifyRequest mailVerifyRequest) {
        log.info("검증 컨트롤러는 통과함");
        userService.checkVerificationNumber(mailVerifyRequest);
        return ResponseEntity.ok(ResponseUtil.createSuccessResponse("인증이 완료되었습니다."));
    }
}
