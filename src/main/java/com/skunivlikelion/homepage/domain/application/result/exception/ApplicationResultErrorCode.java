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
  ONLY_SUBMITTED_RECORD_ALLOWED("APPRES4001", "제출된 지원서만 처리할 수 있습니다.", HttpStatus.BAD_REQUEST),
  ONLY_PASSED_DOCUMENT_ALLOWED(
      "APPRES4002", "서류 결과가 합격 처리된 지원서만 면접 합격 요청이 가능합니다.", HttpStatus.BAD_REQUEST),

  NOT_FOUND_RECORD("APPRES4041", "진행중 공고에 대한 지원서가 존재하지 않습니다.", HttpStatus.NOT_FOUND),
  DOCUMENT_RESULT_ALREADY_ANNOUNCED(
      "APPRES4091", "서류 결과 발표 이후에는 서류 합격 여부를 수정할 수 없습니다.", HttpStatus.CONFLICT),
  FINAL_RESULT_ALREADY_ANNOUNCED(
      "APPRES4092", "최종 결과 발표 이후에는 면접 합격 여부를 수정할 수 없습니다.", HttpStatus.CONFLICT),
  ;

  private final String code;
  private final String message;
  private final HttpStatus status;
}
