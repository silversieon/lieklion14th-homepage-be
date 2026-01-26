/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.auth.exception;

import org.springframework.http.HttpStatus;

import backend.boilerplate.exception.model.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AuthErrorCode implements BaseErrorCode {
  INVALID_INPUT_EMAIL("AUTH4001", "유효하지 않은 이메일 값입니다.", HttpStatus.BAD_REQUEST),
  ALREADY_EXIST_EMAIL("AUTH4002", "이미 존재하는 이메일 값입니다.", HttpStatus.BAD_REQUEST),
  ALREADY_EXIST_STUDENTNUMBER("AUTH4003", "이미 존재하는 학번입니다.", HttpStatus.BAD_REQUEST),
  ALREADY_EXIST_PHONENUMBER("AUTH4004", "이미 존재하는 전화번호입니다.", HttpStatus.BAD_REQUEST),
  INCORRECT_PASSWORD("AUTH4005", "비밀번호가 일치하지 않습니다.", HttpStatus.BAD_REQUEST),
  LOGIN_FAIL("AUTH4006", "로그인에 실패했습니다.", HttpStatus.BAD_REQUEST),
  NOT_VERIFICATED_EMAIL("AUTH4007", "확인되지 않은 이메일의 인증코드 값입니다.", HttpStatus.BAD_REQUEST),
  EXPIRED_ACCESS_TOKEN("AUTH4011", "유효하지 않은 JWT 액세스 토큰입니다.", HttpStatus.UNAUTHORIZED),
  UNAUTHORIZED_TOKEN("AUTH4012", "유효하지 않은 토큰 입력입니다.", HttpStatus.UNAUTHORIZED),
  UNAUTHORIZED("AUTH4013", "로그인 되지 않은 사용자입니다.", HttpStatus.UNAUTHORIZED),
  NOT_FOUND_EMAIL("AUTH4041", "존재하지 않는 이메일 값입니다.", HttpStatus.NOT_FOUND),
  INTERNAL_SERVER_ERROR_EMAIL("AUTH5001", "인증 코드 발송에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);

  private final String code;

  private final String message;

  private final HttpStatus status;
}
