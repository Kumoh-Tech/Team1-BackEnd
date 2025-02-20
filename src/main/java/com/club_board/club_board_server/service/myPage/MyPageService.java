package com.club_board.club_board_server.service.myPage;
import com.club_board.club_board_server.domain.user.User;
import com.club_board.club_board_server.dto.myPage.MyPageResponse;
import com.club_board.club_board_server.dto.myPage.UpdateMyPageRequest;
import com.club_board.club_board_server.dto.myPage.UpdateUserCommand;
import com.club_board.club_board_server.repository.user.UserRepository;
import com.club_board.club_board_server.response.exception.BusinessException;
import com.club_board.club_board_server.response.exception.ExceptionType;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MyPageService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    public MyPageResponse getMyPage(Long userId){
        User user=userRepository.findById(userId)
                .orElseThrow(()->new BusinessException(ExceptionType.USER_NOT_FOUND));
        return MyPageResponse.builder()
                .role(user.getRole().getDisplayName())
                .username(user.getUsername())
                .name(user.getName())
                .department(user.getDepartment())
                .studentId(user.getStudent_id())
                .grade(user.getGrade())
                .phoneNumber(user.getPhoneNumber())
                .departmentPublic(user.is_department_public())
                .studentIdPublic(user.is_student_public())
                .phonePublic(user.is_phone_public())
                .gradePublic(user.is_grade_public())
                .build();
    }

    @Transactional
    public void updateMyPage(Long userId, UpdateMyPageRequest updateMyPageRequest){
        User user=userRepository.findById(userId)
                .orElseThrow(()->new BusinessException(ExceptionType.USER_NOT_FOUND));
        if(!passwordEncoder.matches(updateMyPageRequest.getPrePassword(), user.getPassword())){
            throw new BusinessException(ExceptionType.NOT_CORRECT_PASSWORD);
        }
        String encodePassword=passwordEncoder.encode(updateMyPageRequest.getNewPassword());
        UpdateUserCommand command = UpdateUserCommand.builder()
                .password(encodePassword)
                .department(updateMyPageRequest.getDepartment())
                .grade(updateMyPageRequest.getGrade())
                .phoneNumber(updateMyPageRequest.getPhoneNumber())
                .departmentPublic(updateMyPageRequest.getDepartmentPublic())
                .gradePublic(updateMyPageRequest.getGradePublic())
                .studentIdPublic(updateMyPageRequest.getStudentIdPublic())
                .phonePublic(updateMyPageRequest.getPhonePublic())
                .build();
        user.updateUserInfo(command);
        userRepository.save(user);
    }
}
