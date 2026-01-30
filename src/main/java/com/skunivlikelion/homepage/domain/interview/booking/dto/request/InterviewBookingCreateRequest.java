/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.interview.booking.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(title = "InterviewBookingCreateRequest: 면접 예약 요청 DTO")
public class InterviewBookingCreateRequest {

  @NotNull @Positive @Schema(description = "예약할 면접 일정 id", example = "1")
  private Long scheduleId;
}
