/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.project.dto.response;

import com.skunivlikelion.homepage.domain.common.enums.Track;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProjectMemberResponse {

  @Schema(description = "프로젝트 멤버 식별자", example = "1")
  private Long projectMemberId;

  @Schema(description = "프로젝트 멤버 이름", example = "정영진")
  private String projectMemberName;

  @Schema(description = "트랙", example = "PO")
  private Track track;
}
