/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.interview.schedule.dto.request;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.validation.constraints.NotNull;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
@Schema(title = "InterviewScheduleCreateRequest: 면접 일정 생성 요청 DTO")
public class InterviewScheduleCreateRequest {

  @NotNull @Schema(description = "면접 날짜", example = "2026-02-20")
  private LocalDate date;

  @NotNull @Schema(description = "시작 시간", example = "18:00:00")
  private LocalTime startTime;

  @NotNull @Schema(description = "종료 시간", example = "18:30:00")
  private LocalTime endTime;
}
