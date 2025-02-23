package com.club_board.club_board_server.service.auth;
import com.club_board.club_board_server.config.jwt.TokenProvider;
import com.club_board.club_board_server.config.jwt.TokenType;
import com.club_board.club_board_server.domain.RefreshToken;
import com.club_board.club_board_server.domain.user.CustomUserDetails;
import com.club_board.club_board_server.domain.user.User;
import com.club_board.club_board_server.dto.auth.ResetPasswordRequest;
import com.club_board.club_board_server.dto.auth.UserLoginRequest;
import com.club_board.club_board_server.dto.auth.UserLoginResponse;
import com.club_board.club_board_server.repository.refreshToken.RefreshTokenRepository;
import com.club_board.club_board_server.repository.user.UserRepository;
import com.club_board.club_board_server.response.exception.BusinessException;
import com.club_board.club_board_server.response.exception.ExceptionType;
import com.club_board.club_board_server.service.mail.EmailService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.Optional;
@RequiredArgsConstructor
@Service
@Slf4j
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final TokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;
    private final EmailService emailService;
    private static final String UPPER_CASE="ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWER_CASE="abcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS="0123456789";
    private static final String SPECIAL_CHARACTERS="@!#$%^&*()_+";
    private static final String ALL_CHARACTERS=UPPER_CASE + LOWER_CASE +DIGITS+SPECIAL_CHARACTERS;
    private static final int MIN_PASSWORD_LENGTH=10;
    private static final int MAX_PASSWORD_LENGTH=20;
    private static final SecureRandom RANDOM=new SecureRandom();

    /*
    로그인 메소드
     */
    @Transactional
    public UserLoginResponse login(UserLoginRequest userLoginRequest, HttpServletRequest request, HttpServletResponse response)
    {
        try{
            Authentication authentication=authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            userLoginRequest.getUsername(),
                            userLoginRequest.getPassword()
                    )
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            CustomUserDetails userDetails=(CustomUserDetails) authentication.getPrincipal();
            // User-Agent 정보 불러오기
            User user=userDetails.getUser();
            String userAgent=request.getHeader("User-Agent");
            //토큰 발급
            String accessToken=tokenProvider.generateAccessToken(user, Duration.ofMinutes(30));
            String refreshToken = generateAndStoreRefreshToken(user, userAgent);
            addRefreshTokenCookie(response,refreshToken);
            String message = "로그인 성공";
            return new UserLoginResponse(message,accessToken);
        }
        catch (Exception e)
        {
            throw new BusinessException(ExceptionType.INVALID_LOGIN);
        }
    }
    /*
    로그아웃 메소드
     */
    public void logout(String refreshToken, HttpServletResponse response){
        refreshTokenRepository.findByRefreshToken(refreshToken)
                .ifPresent(refreshTokenRepository::delete);
        Cookie cookie = new Cookie("refresh-token",null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    /*
    임시 비밀번호 찾기 메소드
     */
    @Transactional
    public void forgotPassword(ResetPasswordRequest resetPasswordRequest)
    {
        String username=resetPasswordRequest.getUsername();
        String name=resetPasswordRequest.getName();
        String studentId= resetPasswordRequest.getStudent_id();
        // 존재하는 유저인지 확인
        User userObj=userRepository.findByUsername(username).orElseThrow(
                ()->new BusinessException(ExceptionType.FIND_PASSWORD_ERROR));
        if(userObj.getUsername().equals(username) && userObj.getName().equals(name) && userObj.getStudent_id().equals(studentId))
        {
            String newPassword=generateTemporaryPassword();
            emailService.sendPasswordMail(username, newPassword);
            String encodedPassword=passwordEncoder.encode(newPassword);
            userObj.issuePassword(encodedPassword);
            userRepository.save(userObj);
        }
        else
        {
            throw new BusinessException(ExceptionType.FIND_PASSWORD_ERROR);
        }
    }

    /*
    임시 비밀번호 생성 로직
     */
    public static String generateTemporaryPassword(){
        try{
            int passwordLength=RANDOM.nextInt(MAX_PASSWORD_LENGTH-MIN_PASSWORD_LENGTH+1)+MIN_PASSWORD_LENGTH;
            StringBuilder password=new StringBuilder(passwordLength);
            password.append(UPPER_CASE.charAt(RANDOM.nextInt(UPPER_CASE.length())));
            password.append(LOWER_CASE.charAt(RANDOM.nextInt(LOWER_CASE.length())));
            password.append(DIGITS.charAt(RANDOM.nextInt(DIGITS.length())));
            password.append(SPECIAL_CHARACTERS.charAt(RANDOM.nextInt(SPECIAL_CHARACTERS.length())));
            for(int i=4;i<passwordLength;i++){
                password.append(ALL_CHARACTERS.charAt(RANDOM.nextInt(ALL_CHARACTERS.length())));
            }
            return shuffleString(password.toString());
        }
        catch (Exception e)
        {
            throw new BusinessException(ExceptionType.TEMPORARY_PASSWORD_ERROR);
        }
    }

    /*
    Refresh-Token 검증
     */
    @Transactional
    public String validateAndHandleRefreshToken(String refreshToken, HttpServletResponse response, String requestUserAgent) {
        // DB에서 Refresh Token 확인
        RefreshToken existingRefreshToken=refreshTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new BusinessException(ExceptionType.INVALID_REFRESH_TOKEN));
        // 저장된 기기와 요청 기기 정보 비교
        if(!existingRefreshToken.getUserAgent().equals(requestUserAgent)){
            throw new BusinessException(ExceptionType.INVALID_REFRESH_TOKEN);
        }
        // Refresh Token 유효성 검사
        tokenProvider.validToken(refreshToken, TokenType.REFRESH);

        // 토큰에서 유저 ID 추출 및 유저 조회
        Long userId = tokenProvider.getClaims(refreshToken).get("id", Long.class);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ExceptionType.USER_NOT_FOUND));

        // Refresh Token 만료 임박 시 새로 발급
        refreshTokenRepository.delete(existingRefreshToken); // 기존 요청한 리프레시 토큰을 삭제
        String newRefreshToken = tokenProvider.generateRefreshToken(user, Duration.ofMinutes(10));
        tokenProvider.updateRefreshToken(newRefreshToken, user,requestUserAgent);
            // Cookie에 새 Refresh Token 저장
        addRefreshTokenCookie(response,newRefreshToken);
        // Access Token 발급
        return tokenProvider.generateAccessToken(user, Duration.ofMinutes(30));
    }

    /*
    refresh-token 쿠키 설정
     */
    public void addRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        ResponseCookie cookie = ResponseCookie.from("refresh-token", refreshToken)
                .httpOnly(true)
                .secure(true)          // 운영 환경에서는 HTTPS 사용 시 true, 개발 환경에서는 false로 설정 가능
                .path("/")
                .maxAge(60*60)
                .sameSite("None")      // SameSite를 None으로 설정
                .build();
        response.addHeader("Set-Cookie", cookie.toString());
    }

    /*
    임시 비밀번호 shuffle
     */
    private static String shuffleString(String input){
        StringBuilder shuffled=new StringBuilder(input.length());
        char[] characters=input.toCharArray();
        for(int i=characters.length;i>0;i--){
            int randomIndex=RANDOM.nextInt(i);
            shuffled.append(characters[randomIndex]);
            characters[randomIndex]=characters[i-1];
        }
        return shuffled.toString();
    }

    /**
     *  로그인 시 Refresh Token 기기별로 저장
     */
    @Transactional
    public String generateAndStoreRefreshToken(User user, String userAgent){
        Optional<RefreshToken> existingToken = refreshTokenRepository.findByUserIdAndUserAgent(user.getId(), userAgent);
        existingToken.ifPresent(refreshTokenRepository::delete);

        String refreshToken = tokenProvider.generateRefreshToken(user,Duration.ofMinutes(10));

        RefreshToken refreshTokenEntity=new RefreshToken(user.getId(), refreshToken , userAgent);
        refreshTokenRepository.save(refreshTokenEntity);
        return refreshToken;
    }
}
