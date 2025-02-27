package com.club_board.club_board_server.response.exception;

import com.club_board.club_board_server.response.ResponseBody;
import com.club_board.club_board_server.response.ResponseUtil;
import jakarta.persistence.LockTimeoutException;
import jakarta.persistence.PessimisticLockException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RequiredArgsConstructor
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ResponseBody<Void>> businessException(BusinessException e) {
        ExceptionType exceptionType = e.getExceptionType();
        return ResponseEntity.status(exceptionType.getStatus())
                .body(ResponseUtil.createFailureResponse(exceptionType));
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ResponseBody<Void>> authorizationDeniedException(AuthorizationDeniedException e){
        return ResponseEntity
                .status(ExceptionType.AUTHORIZATION_DENIED.getStatus())
                .body(ResponseUtil.createFailureResponse(ExceptionType.AUTHORIZATION_DENIED));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseBody<Void>> methodArgumentNotValidException(MethodArgumentNotValidException e){
        String customMessage=e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        return ResponseEntity
                .status(ExceptionType.BINDING_ERROR.getStatus())
                .body(ResponseUtil.createFailureResponse(ExceptionType.BINDING_ERROR, customMessage));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ResponseBody<Void>> handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        return ResponseEntity
                .status(ExceptionType.ESSENTIAL_FIELD_MISSING_ERROR.getStatus())
                .body(ResponseUtil.createFailureResponse(ExceptionType.ESSENTIAL_FIELD_MISSING_ERROR));
    }

    @ExceptionHandler(PessimisticLockException.class)
    public ResponseEntity<ResponseBody<Void>> handleLockTimeout(PessimisticLockException e) {
        return ResponseEntity
                .status(ExceptionType.CONCURRENCY_CONFLICT.getStatus())
                .body(ResponseUtil.createFailureResponse(ExceptionType.CONCURRENCY_CONFLICT));
    }

    @ExceptionHandler(LockTimeoutException.class)
    public ResponseEntity<ResponseBody<Void>> handleLockTimeoutException(LockTimeoutException e) {
        return ResponseEntity
                .status(ExceptionType.LOCK_TIMEOUT.getStatus())
                .body(ResponseUtil.createFailureResponse(ExceptionType.LOCK_TIMEOUT));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ResponseBody<Void>> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        return ResponseEntity
                .status(ExceptionType.INVALID_JSON_FORMAT.getStatus())
                .body(ResponseUtil.createFailureResponse(ExceptionType.INVALID_JSON_FORMAT));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseBody<Void>> exception(Exception e){
        e.printStackTrace();
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseUtil.createFailureResponse(ExceptionType.UNEXPECTED_SERVER_ERROR));
    }
}