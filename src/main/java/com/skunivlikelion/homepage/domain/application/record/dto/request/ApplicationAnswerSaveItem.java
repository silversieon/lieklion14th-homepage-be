/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.application.record.dto.request;

import jakarta.validation.constraints.Size;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
@Schema(title = "ApplicationAnswerSaveItem: 답변 저장 단건 DTO")
public class ApplicationAnswerSaveItem {

  @Schema(description = "질문 ID", example = "1")
  private Long questionId;

  @Schema(description = "답변 내용", example = "지원 이유는 운영진이 너무 좋기 때문입니다.")
  @Size(max = 500)
  private String content;
}
