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
@Schema(title = "ApplicationQuestionUpsertResponse: 지원서 질문 등록/수정 응답 DTO")
public class ApplicationQuestionUpsertResponse {

  @Schema(description = "기수 값", example = "13")
  private Long semester;

  @Schema(description = "트랙별 질문 묶음")
  private List<TrackQuestionGroupResponse> groups;

  @Getter
  @Builder
  @AllArgsConstructor
  public static class TrackQuestionGroupResponse {

    @Schema(description = "트랙", example = "BACKEND")
    private Track track;

    @Schema(description = "해당 트랙의 질문 목록(문항번호/질문 내용)")
    private List<QuestionItemResponse> questions;
  }

  @Getter
  @Builder
  @AllArgsConstructor
  public static class QuestionItemResponse {

    @Schema(description = "문항 번호", example = "1")
    private Integer orderNumber;

    @Schema(description = "질문 내용", example = "지원 동기를 작성해주세요.")
    private String content;
  }
}
