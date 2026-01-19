/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.application.form.dto.response;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
@Schema(title = "ApplicationFormResponse: 모집 공고 응답 DTO")
public class ApplicationFormResponse {

  @Schema(description = "모집 공고 식별자", example = "1")
  private Long id;

  @Schema(description = "기수 값", example = "13")
  private Long semester;

  @Schema(description = "모집 시작 일시", example = "2026-02-01T10:00:00")
  private LocalDateTime openAt;

  @Schema(description = "모집 마감 일시", example = "2026-02-15T23:59:59")
  private LocalDateTime closeAt;

  @Schema(description = "서류 결과 발표 일시", example = "2026-02-18T18:00:00")
  private LocalDateTime applicationResultAt;

  @Schema(description = "최종 결과 발표 일시", example = "2026-02-25T18:00:00")
  private LocalDateTime finalResultAt;
}
