/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.interview.booking.exception;

import org.springframework.http.HttpStatus;

import com.skunivlikelion.homepage.global.exception.model.BaseErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum InterviewBookingErrorCode implements BaseErrorCode {
  INVALID_CURSOR("IVTBK4001", "잘못된 커서 형식입니다.", HttpStatus.BAD_REQUEST),
  INVALID_REQUEST("IVTBK4002", "잘못된 요청입니다.", HttpStatus.BAD_REQUEST),

  NOT_PASSED_DOCUMENT("IVTBK4031", "서류 합격자만 면접 일정을 예약할 수 있습니다.", HttpStatus.FORBIDDEN),
  PAST_SCHEDULE("IVTBK4032", "이미 시작된 면접 일정의 예약은 삭제할 수 없습니다.", HttpStatus.FORBIDDEN),
  BOOKING_WINDOW_CLOSED(
      "IVTBK4033", "예약/변경 가능 시간이 아닙니다. (서류 결과 발표 이후 ~ 면접 일정 확정 이전)", HttpStatus.FORBIDDEN),
  PAST_SCHEDULE_BOOKING("IVTBK4034", "이미 시작된 면접 일정은 예약/변경할 수 없습니다.", HttpStatus.FORBIDDEN),

  NOT_FOUND_SCHEDULE("IVTBK4041", "존재하지 않는 면접 일정입니다.", HttpStatus.NOT_FOUND),
  NOT_FOUND_APPLICATION_RECORD("IVTBK4042", "제출된 지원서를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
  NOT_FOUND_BOOKING("IVTBK4043", "예약된 면접 일정이 없습니다.", HttpStatus.NOT_FOUND),
  NOT_FOUND_CURRENT_FORM("IVTBK4044", "현재 모집중인 공고를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
  BOOKING_NOT_FOUND("IVTBK4045", "삭제할 면접 예약이 존재하지 않습니다.", HttpStatus.NOT_FOUND),
  APPLICATION_FORM_NOT_FOUND("IVTBK4046", "해당 기수의 모집 공고를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),

  ALREADY_BOOKED_SCHEDULE("IVTBK4091", "이미 예약된 면접 일정입니다.", HttpStatus.CONFLICT),
  ALREADY_BOOKED_USER("IVTBK4092", "이미 면접 일정을 예약했습니다.", HttpStatus.CONFLICT),
  TRACK_MISMATCH("IVTBK4093", "지원서 트랙과 면접 일정 트랙이 일치하지 않습니다.", HttpStatus.CONFLICT),
  SAME_SCHEDULE("IVTBK4094", "동일한 면접 일정으로는 변경할 수 없습니다.", HttpStatus.CONFLICT);

  private final String code;
  private final String message;
  private final HttpStatus status;
}
