package com.club_board.club_board_server.service.user;

import com.club_board.club_board_server.domain.user.Department;
import com.club_board.club_board_server.domain.user.Role;
import com.club_board.club_board_server.domain.user.User;
import com.club_board.club_board_server.domain.mail.VerificationCode;
import com.club_board.club_board_server.dto.mail.MailVerifyRequest;
import com.club_board.club_board_server.dto.user.UserRegisterRequest;
import com.club_board.club_board_server.repository.user.UserRepository;
import com.club_board.club_board_server.response.exception.BusinessException;
import com.club_board.club_board_server.response.exception.ExceptionType;
import com.club_board.club_board_server.service.mail.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {

    @Value("${verification.code.expiry-minutes}")
    private int verificationCodeExpiryMinutes;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    private final Map<String, VerificationCode> emailVerificationMap = new ConcurrentHashMap<>();
    private final Set<String> verifiedEmails = ConcurrentHashMap.newKeySet();

    public List<String> showRegisterForm() {
        return Arrays.stream(Department.values())
                .map(Department::getDisplayName)
                .toList();
    }

    public void register(UserRegisterRequest userRegisterRequest) {
        if (!isEmailVerified(userRegisterRequest.getUsername())){  // 이메일 인증을 완료했는가
            throw new BusinessException(ExceptionType.EMAIL_NOT_VERIFIED);
        }
        try {
            String encodedPassword=passwordEncoder.encode(userRegisterRequest.getPassword());
            Department departmentEnum = Department.fromDisplayName(userRegisterRequest.getDepartment());
            User user=User.builder()
                    .username(userRegisterRequest.getUsername())
                    .password(encodedPassword)
                    .name(userRegisterRequest.getName())
                    .department(departmentEnum.getDisplayName())
                    .student_id(userRegisterRequest.getStudent_id())
                    .grade(userRegisterRequest.getGrade())
                    .phoneNumber(userRegisterRequest.getPhone_number())
                    .registrationDate(LocalDate.now())
                    .role(Role.USER)
                    .build();
            verifiedEmails.remove(userRegisterRequest.getUsername());
            userRepository.save(user);
        }
        catch (Exception e) {
            throw new BusinessException(ExceptionType.UNEXPECTED_SERVER_ERROR);
        }
    }

    private boolean isEmailVerified(String email) {
        return verifiedEmails.contains(email);
    }

    public void sendMail(String username) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new BusinessException(ExceptionType.USER_ALREADY_EXIST);
        }

        int number = createNumber();
        emailService.sendMail(username, number);
        emailVerificationMap.put(username, new VerificationCode(number, LocalDateTime.now()));
    }

    private int createNumber() { // 메일 코드 생성
        return (int)(Math.random() * (900000)) + 100000; // 최소 100000인 6자리 숫자
    }

    public void checkVerificationNumber(MailVerifyRequest mailVerifyRequest) { // 이메일 코드 일치 검증
        String mail = mailVerifyRequest.getUsername();
        VerificationCode verificationCode = emailVerificationMap.get(mail);

        synchronized (emailVerificationMap) {  // 동시성 문제 방지를 위해 동기화 블록 사용
            if (verificationCode == null || !isValidCode(verificationCode, mailVerifyRequest)) {
                throw new BusinessException(ExceptionType.INVALID_EMAIL_CODE);
            }
            if (isExpired(verificationCode)) {
                emailVerificationMap.remove(mail);
                throw new BusinessException(ExceptionType.EXPIRED_EMAIL_CODE);
            }
            emailVerificationMap.remove(mail);
            verifiedEmails.add(mail);
        }
    }

    private boolean isExpired(VerificationCode verificationCode) { //코드 만료 체크
        return verificationCode.getTimestamp().plusMinutes(verificationCodeExpiryMinutes).isBefore(LocalDateTime.now());
    }

    private boolean isValidCode(VerificationCode verificationCode,MailVerifyRequest mailVerifyRequest) { //코드 일치 체크
        return verificationCode.getCode() == mailVerifyRequest.getMailCode();
    }
}
