package com.club_board.club_board_server.repository.refreshToken;
import com.club_board.club_board_server.domain.RefreshToken;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<RefreshToken> findByRefreshToken(String refreshToken);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<RefreshToken> findByUserId(Long userId);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<RefreshToken> findByUserIdAndUserAgent(Long id, String userAgent);
}
