package com.club_board.club_board_server.service.user;
import com.club_board.club_board_server.domain.user.Department;
import com.club_board.club_board_server.domain.user.Role;
import com.club_board.club_board_server.domain.user.User;
import com.club_board.club_board_server.dto.mail.MailVerifyRequest;
import com.club_board.club_board_server.dto.user.UserRegisterRequest;
import com.club_board.club_board_server.repository.user.UserRepository;
import com.club_board.club_board_server.response.exception.BusinessException;
import com.club_board.club_board_server.response.exception.ExceptionType;
import com.club_board.club_board_server.service.mail.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.Duration;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Arrays;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {

    private final StringRedisTemplate stringRedisTemplate;
    @Value("${verification.code.expiry-minutes}")
    private int verificationCodeExpiryMinutes;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;



    public List<String> showRegisterForm() {
        return Department.showDepartment();
    }

    public void register(UserRegisterRequest userRegisterRequest) {
        String key = "auth:email:" + userRegisterRequest.getUsername();
        String verificationStatus = stringRedisTemplate.opsForValue().get(key);
        if (!"VERIFIED".equals(verificationStatus)) { // 이메일 인증이 아직 완료되지 않았을 때
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
            userRepository.save(user);
            stringRedisTemplate.delete(key);
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException(ExceptionType.UNEXPECTED_SERVER_ERROR);
        }
    }



    public void sendMail(String username) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new BusinessException(ExceptionType.USER_ALREADY_EXIST);
        }

        int number = createNumber();
        emailService.sendMail(username, number);
        String key="auth:email:"+username;
        stringRedisTemplate.opsForValue().set(key, String.valueOf(number), Duration.ofMinutes(verificationCodeExpiryMinutes));
    }

    private int createNumber() { // 메일 코드 생성
        return (int)(Math.random() * (900000)) + 100000; // 최소 100000인 6자리 숫자
    }

    public void checkVerificationNumber(MailVerifyRequest mailVerifyRequest) { // 이메일 코드 일치 검증
        String key="auth:email:"+mailVerifyRequest.getUsername();
        String requestCode=String.valueOf(mailVerifyRequest.getMailCode());
        String storedValue = stringRedisTemplate.opsForValue().get(key);// 인증 코드를 레디스에서 가져와야함
        if(storedValue==null){  // 저장된 인증번호가 없다
            throw new BusinessException(ExceptionType.EXPIRED_EMAIL_CODE);
        }
        if("VERIFIED".equals(storedValue))
            return;
        // 입력한 코드와 redis에 저장된 코드가 일치하지 않을 때
        if(!requestCode.equals(storedValue)){
            throw new BusinessException(ExceptionType.INVALID_EMAIL_CODE);
        }
        stringRedisTemplate.opsForValue().set(key, "VERIFIED", Duration.ofMinutes(verificationCodeExpiryMinutes));
    }
    @Transactional
    public void setProfileImageUrl(Long userId, String objectName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ExceptionType.USER_NOT_FOUND));

        user.setProfileImageUrl(objectName);
    }

    public String getProfileImageUrl(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ExceptionType.USER_NOT_FOUND));

        return user.getProfileImageUrl();
    }
}
