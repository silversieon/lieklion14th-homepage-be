/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.application.record.exception;

import org.springframework.http.HttpStatus;

import com.skunivlikelion.homepage.global.exception.model.BaseErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ApplicationRecordErrorCode implements BaseErrorCode {
  INVALID_SUBMIT_SNAPSHOT("APPREC4001", "제출 요청은 모든 문항의 답변을 포함해야 합니다.", HttpStatus.BAD_REQUEST),
  INVALID_SUBMIT_QUESTION_SET(
      "APPREC4002", "제출된 문항 구성(questionId)이 현재 모집 공고와 일치하지 않습니다.", HttpStatus.BAD_REQUEST),
  INVALID_ANSWER_PAYLOAD("APPREC4003", "답변 데이터가 올바르지 않습니다.", HttpStatus.BAD_REQUEST),
  NOT_FOUND_SUBMITTABLE_APPLICATION_FORM(
      "APPREC4004", "현재 서류 지원 기간이 아닙니다.", HttpStatus.BAD_REQUEST),
  ANSWER_TOO_LONG("APPREC4005", "답변은 최대 500자까지 입력할 수 있습니다.", HttpStatus.BAD_REQUEST),
  INVALID_REQUEST_TRACK("APPREC4006", "지원서 트랙 항목에는 COMMON을 입력할 수 없습니다.", HttpStatus.BAD_REQUEST),
  INVALID_ANSWER_QUESTION_NOT_FOUND(
      "APPREC4007", "존재하지 않는 문항(questionId)입니다.", HttpStatus.BAD_REQUEST),
  INVALID_ANSWER_TRACK_MISMATCH("APPREC4008", "문항 트랙이 요청 트랙과 일치하지 않습니다.", HttpStatus.BAD_REQUEST),

  NOT_FOUND_DRAFT("APPREC4041", "임시 저장된 지원서가 존재하지 않습니다.", HttpStatus.NOT_FOUND),
  NOT_FOUND_RECORD("APPREC4042", "지원서가 존재하지 않습니다.", HttpStatus.NOT_FOUND),

  ALREADY_SUBMITTED("APPREC4091", "이미 제출된 지원서입니다.", HttpStatus.CONFLICT),
  ALREADY_RECORD_EXISTS("APPREC4092", "이미 작성중 또는 제출된 지원서가 존재합니다.", HttpStatus.CONFLICT),
  ;

  private final String code;
  private final String message;
  private final HttpStatus status;
}
