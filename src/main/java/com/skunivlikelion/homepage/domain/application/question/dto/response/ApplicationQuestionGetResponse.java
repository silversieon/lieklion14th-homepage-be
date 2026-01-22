/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.application.question.dto.response;

import java.util.List;

import com.skunivlikelion.homepage.domain.common.enums.Track;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
@Schema(title = "ApplicationQuestionGetResponse: 지원서 질문 조회 응답 DTO")
public class ApplicationQuestionGetResponse {

  @Schema(description = "기수 값", example = "13")
  private Long semester;

  @Schema(description = "트랙", example = "BACKEND")
  private Track track;

  @Schema(description = "질문 목록(문항번호/질문 내용)")
  private List<ApplicationQuestionUpsertResponse.QuestionItemResponse> questions;
}
