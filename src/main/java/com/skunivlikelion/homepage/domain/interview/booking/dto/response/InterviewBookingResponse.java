/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.interview.booking.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
@Schema(title = "InterviewBookingResponse: 면접 예약 응답 DTO")
public class InterviewBookingResponse {

  @Schema(description = "예약 id", example = "10")
  private Long bookingId;

  @Schema(description = "예약된 면접 일정 id", example = "1")
  private Long scheduleId;
}
