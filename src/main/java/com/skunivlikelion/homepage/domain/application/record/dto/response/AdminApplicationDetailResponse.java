/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.application.record.dto.response;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "AdminApplicationDetailResponse: 관리자 특정 지원서 조회 응답 DTO")
public class AdminApplicationDetailResponse {

  @Schema(description = "지원서 메타 정보")
  private ApplicationRecordMeta meta;

  @Schema(description = "사용자 정보")
  private AdminApplicantUserInfo userInfo;

  @Schema(description = "공통 질문/답변 목록")
  private List<ApplicationQuestionAnswerItem> commonQuestions;

  @Schema(description = "트랙 질문/답변 목록")
  private List<ApplicationQuestionAnswerItem> trackQuestions;
}
