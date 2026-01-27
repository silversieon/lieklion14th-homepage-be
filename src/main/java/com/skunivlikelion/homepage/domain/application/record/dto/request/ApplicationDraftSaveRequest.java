/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.application.record.dto.request;

import java.util.List;

import jakarta.validation.constraints.NotNull;

import com.skunivlikelion.homepage.domain.common.enums.Track;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
@Schema(title = "ApplicationDraftSaveRequest: 지원서 임시저장 요청 DTO")
public class ApplicationDraftSaveRequest {

  @NotNull @Schema(description = "선택 트랙", example = "BACKEND")
  private Track track;

  @Schema(description = "공통 질문 답변들")
  private List<ApplicationAnswerSaveItem> commonAnswers;

  @Schema(description = "트랙 질문 답변들")
  private List<ApplicationAnswerSaveItem> trackAnswers;
}
