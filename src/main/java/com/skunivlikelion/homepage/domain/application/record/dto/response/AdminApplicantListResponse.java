/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.application.record.dto.response;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
@Schema(title = "AdminApplicantListResponse: 관리자 지원자 목록 조회 응답 DTO")
public class AdminApplicantListResponse {

  @Schema(description = "지원자 목록")
  private List<AdminApplicantListItem> lists;
}
