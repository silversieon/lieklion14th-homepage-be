/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.application.result.exception;

import org.springframework.http.HttpStatus;

import com.skunivlikelion.homepage.global.exception.model.BaseErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ApplicationResultErrorCode implements BaseErrorCode {
  INVALID_REQUEST("APR4001", "요청 값이 올바르지 않습니다.", HttpStatus.BAD_REQUEST),
  ONLY_SUBMITTED_RECORD_ALLOWED("APR4002", "제출된 지원서만 처리할 수 있습니다.", HttpStatus.BAD_REQUEST),
  ONLY_PASSED_DOCUMENT_ALLOWED(
      "APR4003", "서류 결과가 합격 처리된 지원서만 면접 합격 요청이 가능합니다.", HttpStatus.BAD_REQUEST),
  NOT_FOUND_RECORD("APR4041", "진행중 공고에 대한 지원서가 존재하지 않습니다.", HttpStatus.NOT_FOUND),
  DOCUMENT_RESULT_ALREADY_ANNOUNCED(
      "APR4092", "서류 결과 발표 이후에는 서류 합격 여부를 수정할 수 없습니다.", HttpStatus.CONFLICT),
  FINAL_RESULT_ALREADY_ANNOUNCED(
      "ARP4093", "최종 결과 발표 이후에는 면접 합격 여부를 수정할 수 없습니다.", HttpStatus.CONFLICT),
  ;

  private final String code;
  private final String message;
  private final HttpStatus status;
}
