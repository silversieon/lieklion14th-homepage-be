/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.interview.schedule.exception;

import org.springframework.http.HttpStatus;

import com.skunivlikelion.homepage.global.exception.model.BaseErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum InterviewScheduleErrorCode implements BaseErrorCode {
  INVALID_TIME_RANGE("IVTSCH4001", "시작 시간은 종료 시간보다 빨라야 합니다.", HttpStatus.BAD_REQUEST),
  OVERLAPPING_SCHEDULE("IVTSCH4002", "같은 기수/트랙/날짜에 겹치는 면접 일정이 이미 존재합니다.", HttpStatus.BAD_REQUEST),
  REQUIRED_SEMESTER(
      "IVTSCH4003", "semester가 없습니다. 조회할 기수를 선택해서 다시 요청해주세요.", HttpStatus.BAD_REQUEST),
  INVALID_TRACK("IVTSCH4004", "track은 필수입니다.", HttpStatus.BAD_REQUEST),

  NOT_PASSED_APPLICATION("IVTSCH4011", "서류 합격자만 면접 일정을 조회할 수 있습니다.", HttpStatus.UNAUTHORIZED),

  NOT_FOUND_SEMESTER("IVTSCH4041", "존재하지 않는 기수입니다.", HttpStatus.NOT_FOUND),
  NOT_FOUND_APPLICATION_RECORD("IVTSCH4042", "지원 내역이 없습니다.", HttpStatus.NOT_FOUND),
  NOT_FOUND_SCHEDULE("IVTSCH4043", "존재하지 않는 면접 일정입니다.", HttpStatus.NOT_FOUND),

  CANNOT_DELETE_BOOKED_SCHEDULE("IVTSCH4091", "예약된 면접 일정은 삭제할 수 없습니다.", HttpStatus.CONFLICT);
  private final String code;
  private final String message;
  private final HttpStatus status;
}
