package com.club_board.club_board_server.config.jwt;
import com.club_board.club_board_server.response.ResponseBody;
import com.club_board.club_board_server.response.ResponseUtil;
import com.club_board.club_board_server.response.exception.BusinessException;
import com.club_board.club_board_server.response.exception.ExceptionType;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.io.PrintWriter;


@Component
@Slf4j
public class TokenExceptionHandlerFilter extends OncePerRequestFilter { // OncePerRequestFilter : 한 요청당 필터가 딱 한 번만 실행되도록 보장하는 추상 클래스

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try{
            filterChain.doFilter(request, response);

        }catch(BusinessException e){
            handleBusinessException(request,response,e);
        }

    }
    private void handleBusinessException(HttpServletRequest request, HttpServletResponse response, BusinessException e) throws IOException {
        log.info("예외 필터 동작");
        ExceptionType exceptionType = e.getExceptionType();
        response.setStatus(exceptionType.getStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        // CORS 관련 헤더 설정 (와일드카드 대신 실제 origin 사용)
        String origin = request.getHeader("Origin"); // 요청 헤더의 Origin을 가져옴
        if(origin == null || origin.isEmpty()){
            origin = "https://chipsatbooks.vercel.app"; // fallback 값, 필요에 따라 설정
        }
        response.setHeader("Access-Control-Allow-Origin", origin);
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type");
        response.setHeader("Access-Control-Allow-Credentials", "true"); // credentials를 허용하도록
        response.setHeader("Vary", "Origin, Access-Control-Request-Method, Access-Control-Request-Headers");

        ResponseBody<Void> body = ResponseUtil.createFailureResponse(exceptionType);
        writeErrorResponse(response, body);
    }

    private void writeErrorResponse(HttpServletResponse response, ResponseBody<Void> body) throws IOException{
        ObjectMapper objectMapper = new ObjectMapper(); // Jackson ObjectMapper 인스턴스 생성
        String json = objectMapper.writeValueAsString(body);  // ResponseBody 객체를 JSON 문자열로 직렬화
        try (PrintWriter writer = response.getWriter()) { // PrintWriter 자원을 안전하게 닫기 위해 try-with-resource 사용
            writer.write(json);
            writer.flush();
        }
    }
}
