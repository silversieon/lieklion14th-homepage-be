/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.interview.booking.dto.response;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(title = "AdminInterviewBookingResponse: 관리자 면접 예약 일정 조회 응답 DTO")
public record AdminInterviewBookingResponse(
    @Schema(description = "기수", example = "13") Integer semester,
    @Schema(description = "트랙별 예약 일정 목록") List<TrackGroup> tracks) {

  @Schema(title = "TrackGroup: 트랙 그룹")
  public record TrackGroup(
      @Schema(description = "트랙", example = "BACKEND") String track,
      @Schema(description = "날짜별 예약 일정 목록") List<DateGroup> dates) {}

  @Schema(title = "DateGroup: 날짜 그룹")
  public record DateGroup(
      @Schema(description = "면접 날짜", example = "2026-02-15") LocalDate date,
      @Schema(description = "예약된 시간 슬롯 목록") List<TimeSlot> times) {}

  @Schema(title = "TimeSlot: 예약된 시간 슬롯")
  public record TimeSlot(
      @Schema(description = "면접 일정 ID", example = "10") Long scheduleId,
      @Schema(description = "시작 시간", example = "18:00:00") LocalTime startTime,
      @Schema(description = "종료 시간", example = "18:30:00") LocalTime endTime,
      @Schema(description = "예약 정보") BookingInfo bookingInfo) {}

  @Schema(title = "BookingInfo: 예약자 정보")
  public record BookingInfo(
      @Schema(description = "예약 ID", example = "10") Long bookingId,
      @Schema(description = "이름", example = "홍길동") String name,
      @Schema(description = "학과 (유저 존재 시만 값, 삭제 시 null)", example = "소프트웨어학과") String department,
      @Schema(description = "학번", example = "2023216056") String studentNumber,
      @Schema(description = "전화번호 (유저 존재 시만 값, 삭제 시 null)", example = "010-1234-5678") String phone,
      @Schema(description = "지원서 ID", example = "123") Long applicationRecordId) {}
}
