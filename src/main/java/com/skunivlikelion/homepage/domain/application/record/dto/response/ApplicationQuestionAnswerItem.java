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
public class ApplicationQuestionAnswerItem {

  @Schema(description = "질문 식별자", example = "10")
  private Long questionId;

  @Schema(description = "질문 번호(오름차순 정렬 기준)", example = "1")
  private Integer orderNumber;

  @Schema(description = "질문 내용", example = "자기소개를 해주세요.")
  private String question;

  @Schema(description = "답변 내용 (답변 없으면 빈 문자열)", example = "안녕하세요. 저는 ...")
  private String answer;
}
