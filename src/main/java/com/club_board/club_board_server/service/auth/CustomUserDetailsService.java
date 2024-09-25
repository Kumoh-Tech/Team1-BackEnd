package com.club_board.club_board_server.service.auth;

import com.club_board.club_board_server.domain.CustomUserDetails;
import com.club_board.club_board_server.domain.User;
import com.club_board.club_board_server.repository.UserRepository;
import com.club_board.club_board_server.response.exception.BusinessException;
import com.club_board.club_board_server.response.exception.ExceptionType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;


@RequiredArgsConstructor
@Slf4j
@Component
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("비밀번호 검사 시작");
        log.info("username: {}", username);
        User user=userRepository.findByUsername(username)
                .orElseThrow(()->new BusinessException(ExceptionType.USER_NOT_FOUND));
        return new CustomUserDetails(user);
    }
}








