/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.interview.booking.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(title = "InterviewBookingUpdateRequest: 사용자 면접 예약 변경 요청 DTO")
public record InterviewBookingUpdateRequest(
    @Schema(description = "변경할 면접 일정(슬롯) ID", example = "10") @NotNull @Positive Long newScheduleId) {}
