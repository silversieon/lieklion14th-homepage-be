/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.application.form.exception;

import org.springframework.http.HttpStatus;

import backend.boilerplate.exception.model.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ApplicationFormErrorCode implements BaseErrorCode {
  INVALID_SEMESTER_VALUE("APPFORM4001", "유효하지 않은 기수 값입니다.", HttpStatus.BAD_REQUEST),
  INVALID_DATE_RANGE("APPFORM4002", "모집 공고 날짜 범위가 올바르지 않습니다.", HttpStatus.BAD_REQUEST),
  ALREADY_EXIST_APPLICATION_FORM("APPFORM4003", "해당 기수의 모집 공고가 이미 존재합니다.", HttpStatus.BAD_REQUEST),

  NOT_FOUND_APPLICATION_FORM("APPFORM4041", "해당 기수의 모집 공고가 존재하지 않습니다.", HttpStatus.NOT_FOUND),
  NOT_FOUND_SEMESTER("APPFORM4042", "존재하지 않는 기수입니다.", HttpStatus.NOT_FOUND),

  CANNOT_DELETE_FORM_WITH_QUESTIONS(
      "APPFORM4092", "등록된 지원서 질문이 있는 모집 공고는 삭제할 수 없습니다.", HttpStatus.CONFLICT);

  private final String code;
  private final String message;
  private final HttpStatus status;
}
