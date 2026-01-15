/* 
 * Copyright (c) SKU LIKELION 
 */
package com.likelionskuniv.website.domain.auth.exception;

import org.springframework.http.HttpStatus;

import backend.boilerplate.exception.model.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AuthErrorCode implements BaseErrorCode {
  INVALID_INPUT_EMAIL("AUTH4001", "유효하지 않은 이메일 값입니다.", HttpStatus.BAD_REQUEST),
  INTERNAL_SERVER_ERROR_EMAIL("AUTH5001", "인증 코드 발송에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);

  private final String code;

  private final String message;

  private final HttpStatus status;
}
