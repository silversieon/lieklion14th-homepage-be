/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.interview;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import backend.boilerplate.response.BaseResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice(basePackages = "com.skunivlikelion.homepage.domain.interview")
public class InterviewExceptionHandler {

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

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<BaseResponse<Object>> handleTypeMismatch(
      MethodArgumentTypeMismatchException ex) {

    log.warn("[Interview] 요청 파라미터 타입 오류 - name={}, value={}", ex.getName(), ex.getValue());

    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(BaseResponse.error(400, String.format("요청 파라미터 '%s' 값이 올바르지 않습니다.", ex.getName())));
  }
}
