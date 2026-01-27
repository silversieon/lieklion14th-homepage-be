/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.application.record.exception;

import org.springframework.http.HttpStatus;

import backend.boilerplate.exception.model.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ApplicationRecordErrorCode implements BaseErrorCode {
  INVALID_STEP("APP4001", "유효하지 않은 단계 입니다.", HttpStatus.BAD_REQUEST),
  INVALID_QUESTION("APP4002", "질문 정보가 올바르지 않습니다.", HttpStatus.BAD_REQUEST),
  INVALID_ANSWER_PAYLOAD("APP4003", "답변 데이터가 올바르지 않습니다.", HttpStatus.BAD_REQUEST),
  FORM_NOT_OPENED("APP4004", "현재 모집 기간이 아닙니다.", HttpStatus.BAD_REQUEST),
  ANSWER_TOO_LONG("APP4005", "답변은 최대 500자까지 입력할 수 있습니다.", HttpStatus.BAD_REQUEST),

  NOT_FOUND_APPLICATION_FORM("APP4041", "모집 공고가 존재하지 않습니다.", HttpStatus.NOT_FOUND),
  NOT_FOUND_DRAFT("APP4042", "임시 저장된 지원서가 존재하지 않습니다.", HttpStatus.NOT_FOUND),
  NOT_FOUND_RECORD("APP4043", "지원서가 존재하지 않습니다.", HttpStatus.NOT_FOUND),

  ALREADY_SUBMITTED("APP4091", "이미 제출된 지원서입니다.", HttpStatus.CONFLICT);

  private final String code;
  private final String message;
  private final HttpStatus status;
}
