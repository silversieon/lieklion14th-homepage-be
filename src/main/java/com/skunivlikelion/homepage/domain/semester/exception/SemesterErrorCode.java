/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.semester.exception;

import org.springframework.http.HttpStatus;

import backend.boilerplate.exception.model.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SemesterErrorCode implements BaseErrorCode {
  ALREADY_EXIST_SEMESTER("SEMESTER4001", "이미 존재하는 기수입니다.", HttpStatus.BAD_REQUEST),

  NOT_FOUND_SEMESTER("SEMESTER4041", "존재하지 않는 기수입니다.", HttpStatus.NOT_FOUND),

  SEMESTER_IN_USE("SEMESTER4091", "해당 기수에 모집 공고가 존재하여 삭제할 수 없습니다.", HttpStatus.CONFLICT);

  private final String code;
  private final String message;
  private final HttpStatus status;
}
