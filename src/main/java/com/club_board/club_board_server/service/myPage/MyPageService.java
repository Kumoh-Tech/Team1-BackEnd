package com.club_board.club_board_server.service.myPage;
import com.club_board.club_board_server.domain.user.Department;
import com.club_board.club_board_server.domain.user.User;
import com.club_board.club_board_server.dto.myPage.userInfo.UserInfoResponse;
import com.club_board.club_board_server.dto.myPage.userInfo.UpdateUserInfoRequest;
import com.club_board.club_board_server.dto.myPage.userInfo.UpdateUserCommand;
import com.club_board.club_board_server.repository.user.UserRepository;
import com.club_board.club_board_server.response.exception.BusinessException;
import com.club_board.club_board_server.response.exception.ExceptionType;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
@Slf4j
@Service
@RequiredArgsConstructor
public class MyPageService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    public UserInfoResponse getMyPage(Long userId){
        User user=userRepository.findById(userId)
                .orElseThrow(()->new BusinessException(ExceptionType.USER_NOT_FOUND));
        List<String> departments= Department.showDepartment();
        return UserInfoResponse.builder()
                .role(user.getRole().getDisplayName())
                .username(user.getUsername())
                .name(user.getName())
                .department(user.getDepartment())
                .studentId(user.getStudent_id())
                .grade(user.getGrade())
                .departments(departments)
                .phoneNumber(user.getPhoneNumber())
                .departmentPublic(user.is_department_public())
                .studentIdPublic(user.is_student_public())
                .phonePublic(user.is_phone_public())
                .gradePublic(user.is_grade_public())
                .build();
    }

    @Transactional
    public void updateMyPage(Long userId, UpdateUserInfoRequest updateUserInfoRequest){
        User user=userRepository.findById(userId)
                .orElseThrow(()->new BusinessException(ExceptionType.USER_NOT_FOUND));
        if(!passwordEncoder.matches(updateUserInfoRequest.getPrePassword(), user.getPassword())){
            throw new BusinessException(ExceptionType.NOT_CORRECT_PASSWORD);
        }
        String encodePassword=passwordEncoder.encode(updateUserInfoRequest.getNewPassword());
        UpdateUserCommand command = UpdateUserCommand.builder()
                .password(encodePassword)
                .department(updateUserInfoRequest.getDepartment())
                .grade(updateUserInfoRequest.getGrade())
                .phoneNumber(updateUserInfoRequest.getPhoneNumber())
                .departmentPublic(updateUserInfoRequest.getDepartmentPublic())
                .gradePublic(updateUserInfoRequest.getGradePublic())
                .studentIdPublic(updateUserInfoRequest.getStudentIdPublic())
                .phonePublic(updateUserInfoRequest.getPhonePublic())
                .build();
        user.updateUserInfo(command);
        userRepository.save(user);
    }
    @Transactional
    public void softDeleteAccount(Long userId){
        // 유저 아이디와 동일한 user 찾음
        User user=userRepository.findById(userId)
                .orElseThrow(()->new BusinessException(ExceptionType.USER_NOT_FOUND));
        // 해당 상태 탈퇴로 변경
        userRepository.delete(user);
    }
}
