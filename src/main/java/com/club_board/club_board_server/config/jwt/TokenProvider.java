package com.club_board.club_board_server.config.jwt;
import com.club_board.club_board_server.domain.RefreshToken;
import com.club_board.club_board_server.domain.user.User;
import com.club_board.club_board_server.repository.refreshToken.RefreshTokenRepository;
import com.club_board.club_board_server.response.exception.BusinessException;
import com.club_board.club_board_server.response.exception.ExceptionType;
import io.jsonwebtoken.*;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
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
        return makeToken(new Date(now.getTime()+expiredAt.toMillis()),user);
    }

    public String generateRefreshToken(User user,Duration expiredAt) {
        Date now=new Date();
        return makeToken(new Date(now.getTime()+expiredAt.toMillis()),user);
    }

    private String makeToken(Date expiry, User user) {
        try{

            List<String> authorities = List.of("ROLE_" + user.getRole());
            // JWT 발급 시간
            Date now=new Date();
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

    public void validToken(String token, TokenType tokenType, HttpServletResponse response) {
        try {

            // SecretKeySpec을 사용하여 Key 객체 생성
            String base64SecretKey = jwtProperties.getSecretKey();
            byte[] secretKeyBytes = Base64.getDecoder().decode(base64SecretKey);
            Key key = new SecretKeySpec(secretKeyBytes, SignatureAlgorithm.HS256.getJcaName());

            // 최신 jjwt 방식으로 토큰 파싱
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)// 토큰의 만료, 서명 오류, 구조적 문제 검사
                    .getBody();
        } catch (ExpiredJwtException e) { // 토큰이 만료되었을 때
            if(tokenType==TokenType.ACCESS){
                clearAccessTokenCookie(response);
                throw new BusinessException(ExceptionType.EXPIRED_ACCESS_TOKEN);
            }
            else {
                throw new BusinessException(ExceptionType.EXPIRED_REFRESH_TOKEN);
            }
        }
        catch (Exception e) { //토큰이 유효하지 않을 때
            if(tokenType==TokenType.ACCESS){
                throw new BusinessException(ExceptionType.INVALID_ACCESS_TOKEN);
            }
            else {
                throw new BusinessException(ExceptionType.INVALID_REFRESH_TOKEN);
            }
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
    public void updateRefreshToken(String refreshToken, User user, String userAgent){
        RefreshToken refreshTokenEntity = refreshTokenRepository.findByUserId(user.getId())
                .map(existingToken -> {
                    existingToken.update(refreshToken); // 기존 토큰 업데이트
                    return existingToken;
                })
                .orElseGet(() -> new RefreshToken(user.getId(), refreshToken, userAgent)); // 없으면 새로 생성

        refreshTokenRepository.save(refreshTokenEntity);
    }
    /*
Access-Token 쿠키 삭제
*/
    public void clearAccessTokenCookie(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from("access-token", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();
        response.addHeader("Set-Cookie", cookie.toString());
    }
    /*
    Refresh-Token 쿠키 삭제
     */
    public void clearRefreshTokenCookie(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from("refresh-token", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();
        response.addHeader("Set-Cookie", cookie.toString());
    }
}

