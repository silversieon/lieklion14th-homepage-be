/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.application.question.dto.request;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import com.skunivlikelion.homepage.domain.common.enums.Track;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
@Schema(title = "ApplicationQuestionUpsertRequest: 지원서 질문 등록/수정 요청 DTO")
public class ApplicationQuestionUpsertRequest {

  @NotNull @Schema(
      description = "트랙별 질문 묶음 리스트",
      example =
          """
              [
                {
                  "track": "BACKEND",
                  "questions": [
                    {"orderNumber": 1, "content": "백엔드를 지원한 이유를 작성해주세요."},
                    {"orderNumber": 2, "content": "프로젝트 경험이 있다면 작성해주세요."}
                  ]
                },
                {
                  "track": "FRONTEND",
                  "questions": [
                    {"orderNumber": 1, "content": "프론트엔드를 지원한 이유를 작성해주세요."}
                  ]
                }
              ]
              """)
  private List<@Valid TrackQuestionGroupRequest> groups;

  @Getter
  @Builder
  @AllArgsConstructor
  public static class TrackQuestionGroupRequest {

    @NotNull @Schema(description = "트랙", example = "BACKEND")
    private Track track;

    @Valid
    @Schema(description = "해당 트랙의 질문 목록(문항번호/질문 내용)")
    private List<@Valid QuestionItemRequest> questions;
  }

  @Getter
  @Builder
  @AllArgsConstructor
  public static class QuestionItemRequest {

    @NotNull @Schema(description = "문항 번호", example = "1")
    private Integer orderNumber;

    @NotBlank
    @Schema(description = "질문 내용", example = "지원 동기를 작성해주세요.")
    private String content;
  }
}
