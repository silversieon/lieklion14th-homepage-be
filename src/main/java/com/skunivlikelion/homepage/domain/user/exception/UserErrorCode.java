/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.user.exception;

import org.springframework.http.HttpStatus;

import com.skunivlikelion.homepage.global.exception.model.BaseErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements BaseErrorCode {
  USER_NOT_FOUND("USER4041", "사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
  CURRENT_PASSWORD_MISMATCH("USER4001", "현재 비밀번호와 일치하지 않습니다.", HttpStatus.BAD_REQUEST),
  NEW_PASSWORD_MISMATCH("USER4002", "입력한 새 비밀번호와 새 비밀번호 확인이 일치하지 않습니다.", HttpStatus.BAD_REQUEST),
  CONFLICT_NEW_PASSWORD("USER4091", "현재 비밀번호와 동일한 새 비밀번호를 입력했습니다.", HttpStatus.CONFLICT),
  CLUBMEMBER_NOT_FOUND("USER4042", "사용자의 구성원 이력을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
  CLUBMEMBER_SEMESTER_CONFLICT("USER4091", "현재 기수에 다른 이력이 존재합니다.", HttpStatus.CONFLICT);

  private final String code;

  private final String message;

  private final HttpStatus status;
}
