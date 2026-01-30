/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.application.record.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ApplicationAnswerItem {

  @Schema(description = "질문 식별자", example = "10")
  private Long questionId;

  @Schema(description = "답변 내용 (답변 없으면 빈 문자열)", example = "안녕하세요. 저는 ...")
  private String answer;
}
