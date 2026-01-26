/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.interview.schedule.exception;

import org.springframework.http.HttpStatus;

import backend.boilerplate.exception.model.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum InterviewScheduleErrorCode implements BaseErrorCode {
  INVALID_TIME_RANGE("IVTSCH4001", "시작 시간은 종료 시간보다 빨라야 합니다.", HttpStatus.BAD_REQUEST),
  OVERLAPPING_SCHEDULE("IVTSCH4002", "같은 기수/트랙/날짜에 겹치는 면접 일정이 이미 존재합니다.", HttpStatus.BAD_REQUEST),

  NOT_FOUND_SEMESTER("IVTSCH4041", "존재하지 않는 기수입니다.", HttpStatus.NOT_FOUND);

  private final String code;
  private final String message;
  private final HttpStatus status;
}
