package com.club_board.club_board_server.service.user;
import com.club_board.club_board_server.domain.Accession;
import com.club_board.club_board_server.domain.Department;
import com.club_board.club_board_server.domain.User;
import com.club_board.club_board_server.domain.VerificationCode;
import com.club_board.club_board_server.dto.mail.MailVerifyRequest;
import com.club_board.club_board_server.dto.user.UserRegisterRequest;
import com.club_board.club_board_server.repository.UserRepository;
import com.club_board.club_board_server.response.exception.BusinessException;
import com.club_board.club_board_server.response.exception.ExceptionType;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {
    @Value("${email.sender}")
    private String senderEmail;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender javaMailSender;
    private static final String ALLOWED_DOMAIN="kumoh.ac.kr";
    private final Map<String, VerificationCode> emailVerificationMap=new ConcurrentHashMap<>();
    private final Set<String> verifiedEmails=ConcurrentHashMap.newKeySet();

    public List<String> showRegisterForm(){
        return Arrays.stream(Department.values())
                .map(Department::getDisplayName)
                .collect(Collectors.toList());
    }
    public void register(UserRegisterRequest userRegisterRequest)
    {
        if(!isEmailVerified(userRegisterRequest.getUsername())){  // 이메일 인증을 완료했는가
            throw new BusinessException(ExceptionType.EMAIL_NOT_VERIFIED);
        }
        try{
            String encodedPassword=passwordEncoder.encode(userRegisterRequest.getPassword());
            User user=User.builder()
                    .username(userRegisterRequest.getUsername())
                    .password(encodedPassword)
                    .name(userRegisterRequest.getName())
                    .department(Department.valueOf(userRegisterRequest.getDepartment()))
                    .student_id(userRegisterRequest.getStudent_id())
                    .grade(userRegisterRequest.getGrade())
                    .phoneNumber(userRegisterRequest.getPhone_number())
                    .registrationDate(LocalDate.now())
                    .build();
            user.addAccession(new Accession());
            verifiedEmails.remove(userRegisterRequest.getUsername());
            userRepository.save(user);
        }
        catch (Exception e)
        {
            throw new BusinessException(ExceptionType.UNEXPECTED_SERVER_ERROR);
        }

    }
    @Async
    public void sendMail(String username) {
        if(username==null || !isValidEmail(username))
        {
            throw new BusinessException(ExceptionType.INVALID_EMAIL_FORMAT);
        }
        int number=createNumber();
        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            message.setFrom(senderEmail);
            message.setRecipients(MimeMessage.RecipientType.TO, username);
            message.setSubject("이메일 인증");
            String body = "";
            body += "<h3>" + "요청하신 인증 번호입니다." + "</h3>";
            body += "<h1>" + number + "</h1>";
            body += "<h3>" + "감사합니다." + "</h3>";
            message.setText(body,"UTF-8", "html");
            javaMailSender.send(message);
            emailVerificationMap.put(username,new VerificationCode(number, LocalDateTime.now()));
            log.info("메일저장소={}",emailVerificationMap);
        } catch (MessagingException e) {
            throw new BusinessException(ExceptionType.EMAIL_SEND_ERROR);
        }
    }

    public void checkVerificationNumber(MailVerifyRequest mailVerifyRequest) // 이메일 코드 일치 검증
    {
        String mail=mailVerifyRequest.getUsername();
        log.info("이메일 코드 일치 검증 시작");
        VerificationCode verificationCode=emailVerificationMap.get(mail);
        if(verificationCode==null ||!isValidCode(verificationCode,mailVerifyRequest)){
            throw new BusinessException(ExceptionType.INVALID_EMAIL_CODE);
        }
        if(isExpired(verificationCode))
        {
            emailVerificationMap.remove(mail);
            throw new BusinessException(ExceptionType.EXPIRED_EMAIL_CODE);
        }
        emailVerificationMap.remove(mail);
        verifiedEmails.add(mail);
    }

    private int createNumber() { // 메일 코드 생성
        return (int)(Math.random() * (90000)) + 100000; //(int) Math.random() * (최댓값-최소값+1) + 최소값
    }


    public boolean isValidEmail(String mail) { // 이메일 포맷 검증
        String domain=mail.split("@")[1];
        return ALLOWED_DOMAIN.equals(domain);
    }

    
    public boolean isExpired(VerificationCode verificationCode) { //코드 만료 체크
        log.info("코드 만료 체크 시작");

        return verificationCode.getTimestamp().plusMinutes(5).isBefore(LocalDateTime.now());

    }

    public boolean isValidCode(VerificationCode verificationCode,MailVerifyRequest mailVerifyRequest) //코드 일치 체크
    {
        log.info("코드 유효검사 시작");
        return verificationCode.getCode()==mailVerifyRequest.getMailCode();
    }

    public boolean isEmailVerified(String email)
    {
        return verifiedEmails.contains(email);
    }

    public void isUsernameAvailable(String username)
    {
        if(userRepository.findByUsername(username).isPresent())
            throw new BusinessException(ExceptionType.USER_ALREADY_EXIST);
    }
}
