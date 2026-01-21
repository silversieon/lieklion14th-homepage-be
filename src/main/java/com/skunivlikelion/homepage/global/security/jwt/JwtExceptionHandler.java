/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.global.security.jwt;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import backend.boilerplate.response.BaseResponse;
import io.jsonwebtoken.MalformedJwtException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class JwtExceptionHandler {

  @ExceptionHandler({MalformedJwtException.class})
  public ResponseEntity<BaseResponse<Object>> handleMalformedJwtException(
      MalformedJwtException ex) {
    log.warn("MalformedJwtException 오류 발생: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body(BaseResponse.error(401, "유효하지 않은 JWT 값 입력"));
  }
}
