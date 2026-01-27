/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.interview.schedule.dto.response;

import java.time.LocalDate;
import java.time.LocalTime;

import com.skunivlikelion.homepage.domain.common.enums.Track;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
@Schema(title = "InterviewScheduleResponse: 면접 일정 응답 DTO")
public class InterviewScheduleResponse {

  @Schema(description = "면접 일정 고유 id", example = "1")
  private Long id;

  @Schema(description = "기수 값", example = "13")
  private Long semester;

  @Schema(description = "트랙", example = "BACKEND")
  private Track track;

  @Schema(description = "면접 날짜", example = "2026-02-20")
  private LocalDate date;

  @Schema(description = "시작 시간", example = "18:00:00")
  private LocalTime startTime;

  @Schema(description = "종료 시간", example = "18:30:00")
  private LocalTime endTime;

  @Schema(description = "예약 여부(booking 존재 여부로 계산, true=예약됨)", example = "false")
  private boolean booked;
}
