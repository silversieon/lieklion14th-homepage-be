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
@Schema(title = "ApplicationFormSummaryResponse: 질문 등록용 모집 공고 요약 응답 DTO")
public class ApplicationFormSummaryResponse {

  @Schema(description = "기수", example = "13")
  private Long semester;

  @Schema(description = "토글에 표시할 제목", example = "13기")
  private String title;

  @Schema(description = "모집 마감 일시", example = "2026-01-31T23:59:59")
  private LocalDateTime closeAt;
}
