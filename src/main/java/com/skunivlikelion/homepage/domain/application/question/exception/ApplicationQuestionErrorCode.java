/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.application.question.exception;

import org.springframework.http.HttpStatus;

import com.skunivlikelion.homepage.global.exception.model.BaseErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ApplicationQuestionErrorCode implements BaseErrorCode {
  APPLICATION_ALREADY_OPENED(
      "APPQ4001", "모집 시작(openAt) 이후에는 질문을 수정할 수 없습니다.", HttpStatus.BAD_REQUEST),
  DUPLICATE_ORDER_NUMBER_IN_TRACK("APPQ4002", "동일 트랙 내 문항번호가 중복되었습니다.", HttpStatus.BAD_REQUEST),
  INVALID_QUESTION_REQUEST("APPQ4003", "동일 트랙 내 문항번호는 1부터 연속이어야 합니다.", HttpStatus.BAD_REQUEST),
  COMMON_QUESTION_REQUIRED("APPQ4004", "공통 질문은 1개 이상 등록해야합니다.", HttpStatus.BAD_REQUEST),

  NOT_FOUND_APPLICATION_FORM("APPQ4041", "해당 모집 공고가 존재하지 않습니다.", HttpStatus.NOT_FOUND),
  NOT_CONFIGURED_QUESTIONS("APPQ4042", "해당 기수의 지원서 질문이 아직 설정되지 않았습니다.", HttpStatus.NOT_FOUND),

  ALREADY_CONFIGURED_QUESTIONS("APPQ4091", "이미 해당 기수의 지원서 질문이 등록되어 있습니다.", HttpStatus.CONFLICT),
  QUESTIONS_IN_USE("APPQ4092", "해당 모집 공고의 질문은 이미 답변이 존재하여 삭제할 수 없습니다.", HttpStatus.CONFLICT);

  private final String code;
  private final String message;
  private final HttpStatus status;
}
