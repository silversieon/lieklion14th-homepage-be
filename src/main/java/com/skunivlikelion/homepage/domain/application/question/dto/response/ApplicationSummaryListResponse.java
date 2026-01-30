/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.application.question.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
@Schema(title = "ApplicationSummaryListResponse: 지원서(질문 생성된 공고) 목록 DTO")
public class ApplicationSummaryListResponse {

  @Schema(description = "진행중(마감 전) 지원서 목록")
  private List<ApplicationSummaryItem> inProgress;

  @Schema(description = "진행완료(마감 후) 지원서 목록")
  private List<ApplicationSummaryItem> completed;

  @Getter
  @Builder
  @AllArgsConstructor
  @Schema(title = "ApplicationSummaryItem: 지원서 카드")
  public static class ApplicationSummaryItem {

    @Schema(description = "모집 공고 식별자", example = "1")
    private Long applicationFormId;

    @Schema(description = "기수", example = "12")
    private Long semester;

    @Schema(description = "지원서 제목", example = "12기 아기사자 모집 지원서")
    private String title;

    @Schema(description = "마감일(closeAt)")
    private LocalDateTime closeAt;
  }
}
