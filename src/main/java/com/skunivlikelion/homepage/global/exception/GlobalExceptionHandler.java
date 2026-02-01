/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.global.exception;

import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.skunivlikelion.homepage.global.common.BaseResponse;
import com.skunivlikelion.homepage.global.exception.model.BaseErrorCode;

import io.jsonwebtoken.MalformedJwtException;
import lombok.extern.slf4j.Slf4j;

/**
 * 전역 예외 처리 클래스입니다.
 *
 * <p>Spring Controller에서 발생하는 예외를 처리하고, 클라이언트에게 일관된 형태의 {@link BaseResponse}를 반환합니다.
 *
 * <p>처리 범위:
 *
 * <ul>
 *   <li>커스텀 예외 (CustomException)
 *   <li>유효성 검증 실패 (MethodArgumentNotValidException)
 *   <li>정적 리소스 미존재 (NoResourceFoundException)
 *   <li>기타 예상치 못한 예외 (Exception)
 * </ul>
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  /**
   * CustomException 발생 시 처리합니다.
   *
   * @param ex 발생한 {@link CustomException}
   * @return {@link ResponseEntity} 형태의 {@link BaseResponse} 에러 응답
   */
  @ExceptionHandler(CustomException.class)
  public ResponseEntity<BaseResponse<Object>> handleCustomException(CustomException ex) {
    BaseErrorCode errorCode = ex.getErrorCode();
    log.warn("CustomException 발생: {} - {}", errorCode.getCode(), errorCode.getMessage());
    return ResponseEntity.status(errorCode.getStatus())
        .body(BaseResponse.error(errorCode.getStatus().value(), errorCode.getMessage()));
  }

  /**
   * MethodArgumentNotValidException 발생 시 처리합니다.
   *
   * @param ex 발생한 {@link MethodArgumentNotValidException}
   * @return {@link ResponseEntity} 형태의 {@link BaseResponse} 에러 응답
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<BaseResponse<Object>> handleValidationException(
      MethodArgumentNotValidException ex) {
    String errorMessages =
        ex.getBindingResult().getFieldErrors().stream()
            .map(e -> String.format("[%s] %s", e.getField(), e.getDefaultMessage()))
            .collect(Collectors.joining(" / "));
    log.warn("Validation 오류 발생: {}", errorMessages);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(BaseResponse.error(400, errorMessages));
  }

  /**
   * NoResourceFoundException 발생 시 처리합니다.
   *
   * @param ex 발생한 {@link NoResourceFoundException}
   * @return {@link ResponseEntity} 형태의 {@link BaseResponse} 에러 응답
   */
  @ExceptionHandler(NoResourceFoundException.class)
  public ResponseEntity<BaseResponse<Object>> handleNoResourceFound(NoResourceFoundException ex) {
    log.debug("정적 리소스 없음: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(BaseResponse.error(404, "리소스를 찾을 수 없습니다."));
  }

  /**
   * Json 포맷 예외 발생시 처리합니다.
   *
   * @param ex 발생한 {@link HttpMessageNotReadableException}
   * @return {@link ResponseEntity} 형태의 {@link BaseResponse} 에러 응답
   */
  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<BaseResponse<Object>> handleJsonParseError(
      HttpMessageNotReadableException ex) {

    Throwable cause = ex.getMostSpecificCause();
    String detail = java.util.Objects.toString(cause.getMessage(), "");
    log.warn("[Interview] 요청 바디 파싱 실패 - detail={}", detail, ex);

    // 날짜/시간 포맷 오류 (LocalDate / LocalTime 파싱 실패: 2026-02-40, 19:90:00 등)
    if (detail.contains("LocalDate")
        || detail.contains("LocalTime")
        || detail.contains("java.time")
        || detail.contains("DateTimeParseException")
        || detail.contains("could not be parsed")
        || detail.contains("Invalid value")
        || detail.contains("MinuteOfHour")
        || detail.contains("HourOfDay")
        || detail.contains("SecondOfMinute")) {

      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(
              BaseResponse.error(400, "date/time 형식이 올바르지 않습니다. (date=yyyy-MM-dd, time=HH:mm:ss)"));
    }

    // 그 외 JSON 파싱 오류 (구조/타입 불일치 등)
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(BaseResponse.error(400, "요청 바디 형식이 올바르지 않습니다."));
  }

  /**
   * 요청 파라미터 타입 예외 발생시 처리합니다.
   *
   * @param ex 발생한 {@link MethodArgumentTypeMismatchException}
   * @return {@link ResponseEntity} 형태의 {@link BaseResponse} 에러 응답
   */
  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<BaseResponse<Object>> handleTypeMismatch(
      MethodArgumentTypeMismatchException ex) {

    log.warn("[Interview] 요청 파라미터 타입 오류 - name={}, value={}", ex.getName(), ex.getValue());

    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(BaseResponse.error(400, String.format("요청 파라미터 '%s' 값이 올바르지 않습니다.", ex.getName())));
  }

  /**
   * 유효하지 않은 Jwt 형식을 처리합니다.
   *
   * @param ex 발생한 {@link MalformedJwtException}
   * @return {@link ResponseEntity} 형태의 {@link BaseResponse} 에러 응답
   */
  @ExceptionHandler({MalformedJwtException.class})
  public ResponseEntity<BaseResponse<Object>> handleMalformedJwtException(
      MalformedJwtException ex) {
    log.warn("MalformedJwtException 오류 발생: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(BaseResponse.error(400, "유효하지 않은 JWT 값 입력"));
  }

  /**
   * 유효하지 입력 형식을 처리합니다.
   *
   * @param ex 발생한 {@link IllegalArgumentException}
   * @return {@link ResponseEntity} 형태의 {@link BaseResponse} 에러 응답
   */
  @ExceptionHandler({IllegalArgumentException.class})
  public ResponseEntity<BaseResponse<Object>> handleIllegalAccessException(
      IllegalArgumentException ex) {
    log.warn("IllegalArgumentException 오류 발생: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(BaseResponse.error(400, "유효하지 않은 입력 요청 발생"));
  }

  /**
   * 그 외 예상치 못한 예외 발생 시 처리합니다.
   *
   * @param ex 발생한 {@link Exception}
   * @return {@link ResponseEntity} 형태의 {@link BaseResponse} 에러 응답
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<BaseResponse<Object>> handleException(Exception ex) {
    log.error("Server 오류 발생", ex);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(BaseResponse.error(500, "예상치 못한 서버 오류가 발생했습니다."));
  }
}
