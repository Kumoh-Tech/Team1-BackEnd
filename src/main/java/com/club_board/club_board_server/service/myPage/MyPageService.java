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
    public UserInfoResponse getMyPage(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ExceptionType.USER_NOT_FOUND));
        return buildUserInfoResponse(user);
    }

    @Transactional
    public UserInfoResponse updateMyPage(Long userId, UpdateUserInfoRequest updateUserInfoRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ExceptionType.USER_NOT_FOUND));
        String encodedPassword = user.getPassword();
        // 사용자가 비밀번호 변경을 요청했을 때만 처리
        if (updateUserInfoRequest.getPrePassword() != null && !updateUserInfoRequest.getPrePassword().isBlank()) {
            // 1. 기존 비밀번호가 올바른지 확인 (주의: raw password가 먼저, encoded password가 두번째여야 함)
            if (!passwordEncoder.matches(updateUserInfoRequest.getPrePassword(), user.getPassword()))
                throw new BusinessException(ExceptionType.NOT_CORRECT_PASSWORD);
            // 2. 새 비밀번호가 비어있으면 안 됨
            if (updateUserInfoRequest.getNewPassword() == null || updateUserInfoRequest.getNewPassword().isBlank()) {
                throw new BusinessException(ExceptionType.PASSWORD_REQUIRED);
            }
            // 3. 새 비밀번호와 비밀번호 확인이 일치하는지 확인
            if (!updateUserInfoRequest.getNewPassword().equals(updateUserInfoRequest.getNewPasswordConfirm()))
                throw new BusinessException(ExceptionType.INVALID_PASSWORD_CONFIRM);
            // 4. 새 비밀번호를 암호화 후 업데이트
            encodedPassword = passwordEncoder.encode(updateUserInfoRequest.getNewPassword());
        }
        UpdateUserCommand command = UpdateUserCommand.builder()
                .password(encodedPassword)
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
        return buildUserInfoResponse(user);
    }
    private UserInfoResponse buildUserInfoResponse(User user) {
        List<String> departments = Department.showDepartment().stream()
                .filter(dept -> !dept.equals(user.getDepartment()))
                .toList();
        return UserInfoResponse.builder()
                .role(user.getRole().getDisplayName())
                .username(user.getUsername())
                .name(user.getName())
                .userDepartment(user.getDepartment())
                .studentId(user.getStudent_id())
                .grade(user.getGrade())
                .phoneNumber(user.getPhoneNumber())
                .departmentPublic(user.is_department_public())
                .studentIdPublic(user.is_student_public())
                .phonePublic(user.is_phone_public())
                .gradePublic(user.is_grade_public())
                .departments(departments)
                .build();
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
