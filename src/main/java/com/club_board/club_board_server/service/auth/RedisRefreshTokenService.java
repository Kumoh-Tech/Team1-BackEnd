package com.club_board.club_board_server.service.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class RedisRefreshTokenService {

    private final StringRedisTemplate stringRedisTemplate;

    @Value("${refresh.token.expiry-days}")
    private int refreshTokenExpiryDays;

    private static final String REFRESH_TOKEN_PREFIX = "refresh_token:";
    private static final String USER_DEVICE_PREFIX = "user_device:";

    public void storeRefreshToken(Long userId, String userAgent, String refreshToken) {
        String deviceKey = USER_DEVICE_PREFIX + userId + ":" + userAgent;
        String tokenKey = REFRESH_TOKEN_PREFIX + refreshToken;
        
        String existingToken = stringRedisTemplate.opsForValue().get(deviceKey);
        if (existingToken != null) {
            stringRedisTemplate.delete(REFRESH_TOKEN_PREFIX + existingToken);
        }
        
        stringRedisTemplate.opsForValue().set(deviceKey, refreshToken, Duration.ofDays(refreshTokenExpiryDays));
        stringRedisTemplate.opsForValue().set(tokenKey, userId + ":" + userAgent, Duration.ofDays(refreshTokenExpiryDays));
    }

    public Optional<String> getRefreshTokenInfo(String refreshToken) {
        String tokenKey = REFRESH_TOKEN_PREFIX + refreshToken;
        String info = stringRedisTemplate.opsForValue().get(tokenKey);
        return Optional.ofNullable(info);
    }

    public boolean validateRefreshToken(String refreshToken, Long userId, String userAgent) {
        return getRefreshTokenInfo(refreshToken)
                .map(info -> info.equals(userId + ":" + userAgent))
                .orElse(false);
    }

    public void deleteRefreshToken(String refreshToken) {
        String tokenKey = REFRESH_TOKEN_PREFIX + refreshToken;
        
        getRefreshTokenInfo(refreshToken).ifPresent(info -> {
            String[] parts = info.split(":");
            if (parts.length >= 2) {
                String userId = parts[0];
                String userAgent = info.substring(userId.length() + 1);
                String deviceKey = USER_DEVICE_PREFIX + userId + ":" + userAgent;
                stringRedisTemplate.delete(deviceKey);
            }
        });
        
        stringRedisTemplate.delete(tokenKey);
    }

    public void deleteAllUserTokens(Long userId) {
        String pattern = USER_DEVICE_PREFIX + userId + ":*";
        stringRedisTemplate.keys(pattern).forEach(key -> {
            String refreshToken = stringRedisTemplate.opsForValue().get(key);
            if (refreshToken != null) {
                stringRedisTemplate.delete(REFRESH_TOKEN_PREFIX + refreshToken);
            }
            stringRedisTemplate.delete(key);
        });
    }
}