/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.interview.booking.dto.response;

import java.time.LocalDate;
import java.time.LocalTime;

import com.skunivlikelion.homepage.domain.common.enums.Track;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(title = "UserInterviewBookingResponse: 사용자 면접 예약 조회 응답 DTO")
public record UserInterviewBookingResponse(
    @Schema(description = "기수", example = "13") int semester,
    @Schema(description = "예약 정보 (예약 없으면 null)") Booking booking) {

  @Schema(title = "Booking: 예약 상세")
  public record Booking(
      @Schema(description = "예약 ID", example = "1") Long bookingId,
      @Schema(description = "트랙", example = "BACKEND") Track track,
      @Schema(description = "면접 일정 ID", example = "4") Long scheduleId,
      @Schema(description = "면접 날짜", example = "2026-01-31") LocalDate date,
      @Schema(description = "시작 시간", example = "18:30:00") LocalTime startTime,
      @Schema(description = "종료 시간", example = "19:00:00") LocalTime endTime) {}
}
