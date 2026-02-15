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
  INVALID_TIME_RANGE("IVTSCH4001", "시작 시간은 종료 시간보다 이전이어야 합니다.", HttpStatus.BAD_REQUEST),
  OVERLAPPING_SCHEDULE("IVTSCH4002", "같은 기수/트랙/날짜에 이미 등록된 면접 일정이 존재합니다.", HttpStatus.BAD_REQUEST),
  REQUIRED_SEMESTER("IVTSCH4003", "semester는 필수입니다. 조회할 기수를 선택해 주세요.", HttpStatus.BAD_REQUEST),
  INVALID_TRACK("IVTSCH4004", "track은 필수입니다.", HttpStatus.BAD_REQUEST),
  INTERVIEW_DATE_OUT_OF_RANGE(
      "IVTSCH4005", "면접 일정의 일시는 면접 일정 확정일부터 최종 결과 발표일까지의 기간 내에 있어야 합니다.", HttpStatus.BAD_REQUEST),
  INTERVIEW_SCHEDULE_CREATE_AFTER_CLOSE(
      "IVTSCH4006", "면접 일정 확정일 이후에는 새로운 면접 일정을 생성할 수 없습니다.", HttpStatus.BAD_REQUEST),
  NOT_PASSED_APPLICATION("IVTSCH4007", "서류 합격자만 면접 일정을 조회할 수 있습니다.", HttpStatus.BAD_REQUEST),

  NOT_FOUND_SEMESTER("IVTSCH4041", "존재하지 않는 기수입니다.", HttpStatus.NOT_FOUND),
  NOT_FOUND_APPLICATION_RECORD("IVTSCH4042", "지원 내역이 존재하지 않습니다.", HttpStatus.NOT_FOUND),
  NOT_FOUND_SCHEDULE("IVTSCH4043", "존재하지 않는 면접 일정입니다.", HttpStatus.NOT_FOUND),
  APPLICATION_FORM_NOT_FOUND("IVTSCH4044", "해당 기수의 모집 공고가 존재하지 않습니다.", HttpStatus.NOT_FOUND),

  CANNOT_DELETE_BOOKED_SCHEDULE("IVTSCH4091", "이미 예약된 면접 일정은 삭제할 수 없습니다.", HttpStatus.CONFLICT);
  private final String code;
  private final String message;
  private final HttpStatus status;
}
