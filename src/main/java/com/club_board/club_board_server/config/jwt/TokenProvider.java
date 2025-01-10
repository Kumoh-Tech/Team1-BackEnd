package com.club_board.club_board_server.config.jwt;
import com.club_board.club_board_server.domain.user.User;
import com.club_board.club_board_server.repository.refreshToken.RefreshTokenRepository;
import com.club_board.club_board_server.response.exception.BusinessException;
import com.club_board.club_board_server.response.exception.ExceptionType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.time.Duration;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class TokenProvider {
    private final JwtProperties jwtProperties;

    private final RefreshTokenRepository refreshTokenRepository;

    /*
    토큰 생성
     */
    public String generateAccessToken(User user, Duration expiredAt) {
        Date now=new Date();
        log.info("at 발급");
        return makeToken(new Date(now.getTime()+expiredAt.toMillis()),user);
    }

    public String generateRefreshToken(User user,Duration expiredAt) {
        Date now=new Date();
        log.info("rt 발급");
        return makeToken(new Date(now.getTime()+expiredAt.toMillis()),user);
    }

    private String makeToken(Date expiry, User user) {
        try{

            List<String> authorities = List.of("ROLE_" + user.getRole());
            // JWT 발급 시간
            Date now=new Date();
            log.info("만료 시간: {}", expiry);
            return Jwts.builder()
                    .setHeaderParam(Header.TYPE,Header.JWT_TYPE)  //헤더 타입
                    .setIssuer(jwtProperties.getIssuer())    //발급자
                    .setIssuedAt(now) // 발급 일시
                    .setExpiration(expiry)  // 만료 일시
                    .setSubject(user.getUsername()) // 유저이름
                    .claim("id",user.getId())  // 유저 ID
                    .claim("authorities",authorities)
                    .signWith(SignatureAlgorithm.HS256,jwtProperties.getSecretKey())
                    .compact();
        }
        catch(Exception e)
        {
            throw new BusinessException(ExceptionType.GENERATE_TOKEN_ERROR);
        }
    }

    public boolean validToken(String token) {
        try {
            log.info("token={}", token);

            // SecretKeySpec을 사용하여 Key 객체 생성
            String base64SecretKey = jwtProperties.getSecretKey();
            byte[] secretKeyBytes = Base64.getDecoder().decode(base64SecretKey);
            Key key = new SecretKeySpec(secretKeyBytes, SignatureAlgorithm.HS256.getJcaName());

            // 최신 jjwt 방식으로 토큰 파싱
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            log.info("Token is valid. Claims: {}", claims);
            return true;
        } catch (Exception e) {
            log.error("Invalid token: {}", e.getMessage());
            return false;
        }
    }

    public boolean getUserIdFromToken(String token,Long userId) {
        Long tokenId = getClaims(token).get("id",Long.class);
        return tokenId.equals(userId);
    }

    public Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(jwtProperties.getSecretKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
