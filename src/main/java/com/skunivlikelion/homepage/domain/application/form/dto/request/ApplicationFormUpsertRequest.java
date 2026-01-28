/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.application.form.dto.request;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
@Schema(title = "ApplicationFormUpsertRequest: 모집 공고 등록/수정 요청 DTO")
public class ApplicationFormUpsertRequest {

  @NotNull @Schema(description = "모집 시작 일시", example = "2026-02-01T10:00:00")
  private LocalDateTime openAt;

  @NotNull @Schema(description = "모집 마감 일시", example = "2026-02-15T23:59:59")
  private LocalDateTime closeAt;

  @NotNull @Schema(description = "서류 결과 발표 일시", example = "2026-02-18T18:00:00")
  private LocalDateTime applicationResultAt;

  @NotNull @Schema(description = "면접 일정 확정 일시", example = "2026-02-20T18:00:00")
  private LocalDateTime interviewScheduleConfirmedAt;

  @NotNull @Schema(description = "최종 결과 발표 일시", example = "2026-02-25T18:00:00")
  private LocalDateTime finalResultAt;
}
