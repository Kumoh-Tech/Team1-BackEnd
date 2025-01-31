package com.club_board.club_board_server.service.mail;

import com.club_board.club_board_server.response.exception.BusinessException;
import com.club_board.club_board_server.response.exception.ExceptionType;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender javaMailSender;

    @Value("${email.sender}")
    private String senderEmail;

    private static final String AUTH_CODE_EMAIL_SUBJECT = "[CHIP_SAT] 인증번호 발송";
    private static final String AUTH_CODE_EMAIL_BODY = """
            <h3>요청하신 인증 번호입니다.</h3>
            <h1>%s</h1>
            <h3>감사합니다.<h3>
            """;

    @Async
    public void sendMail(String email, int number) {
        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            message.setFrom(senderEmail);
            message.setRecipients(MimeMessage.RecipientType.TO, email);
            message.setSubject(AUTH_CODE_EMAIL_SUBJECT);
            String body = String.format(AUTH_CODE_EMAIL_BODY, number);
            message.setText(body,"UTF-8", "html");
            javaMailSender.send(message);
        } catch (MessagingException e) {
            throw new BusinessException(ExceptionType.EMAIL_SEND_ERROR);
        }
    }
}
