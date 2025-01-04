package com.club_board.club_board_server.service.myPage;
import com.club_board.club_board_server.domain.User;
import com.club_board.club_board_server.dto.myPage.MyPageResponse;
import com.club_board.club_board_server.dto.myPage.UpdateMyPageRequest;
import com.club_board.club_board_server.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MyPageService {
    private final UserRepository userRepository;
    public MyPageResponse getMyPage(Long userId){
        User user=userRepository.findById(userId);
        return MyPageResponse.builder()
                .name(user.getName())
                .department(user.getDepartment())
                .studentId(user.getStudent_id())
                .grade(user.getGrade())
                .build();
    }

    @Transactional
    public void updateMyPage(Long userId, UpdateMyPageRequest updateMyPageRequest){
        User user=userRepository.findById(userId);
        user.updateUserInfo(
                updateMyPageRequest.getName(),
                updateMyPageRequest.getDepartment(),
                updateMyPageRequest.getStudentId(),
                updateMyPageRequest.getGrade(),
                updateMyPageRequest.getPhoneNumber());
    }
}
