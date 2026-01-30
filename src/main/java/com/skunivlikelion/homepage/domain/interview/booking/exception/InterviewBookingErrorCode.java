/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.interview.booking.exception;

import org.springframework.http.HttpStatus;

import backend.boilerplate.exception.model.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum InterviewBookingErrorCode implements BaseErrorCode {
  NOT_PASSED_DOCUMENT("IVTBK4031", "서류 합격자만 면접 일정을 예약할 수 있습니다.", HttpStatus.FORBIDDEN),
  PAST_SCHEDULE("IVTBK4032", "이미 시작된 면접 일정의 예약은 삭제할 수 없습니다.", HttpStatus.FORBIDDEN),

  NOT_FOUND_SCHEDULE("IVTBK4041", "존재하지 않는 면접 일정입니다.", HttpStatus.NOT_FOUND),
  NOT_FOUND_APPLICATION_RECORD("IVTBK4042", "제출된 지원서를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
  NOT_FOUND_BOOKING("IVTBK4043", "예약된 면접 일정이 없습니다.", HttpStatus.NOT_FOUND),
  NOT_FOUND_CURRENT_FORM("IVTBK4044", "현재 모집중인 공고를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
  BOOKING_NOT_FOUND("IVTBK4045", "삭제할 면접 예약이 존재하지 않습니다.", HttpStatus.NOT_FOUND),

  ALREADY_BOOKED_SCHEDULE("IVTBK4091", "이미 예약된 면접 일정입니다.", HttpStatus.CONFLICT),
  ALREADY_BOOKED_USER("IVTBK4092", "이미 면접 일정을 예약했습니다.", HttpStatus.CONFLICT),
  TRACK_MISMATCH("IVTBK4093", "지원서 트랙과 면접 일정 트랙이 일치하지 않습니다.", HttpStatus.CONFLICT),
  SAME_SCHEDULE("IVTBK4094", "동일한 면접 일정으로는 변경할 수 없습니다.", HttpStatus.CONFLICT);

  private final String code;
  private final String message;
  private final HttpStatus status;
}
