/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.project.dto.response;

import com.skunivlikelion.homepage.domain.project.entity.ProjectType;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
@Schema(title = "ProjectTypeResponse: 프로젝트 타입 조회 응답 DTO")
public class ProjectTypeResponse {
  @Schema(description = "프로젝트 타입 식별자", example = "1")
  private Long projectTypeId;

  @Schema(description = "프로젝트 타입", example = "중앙해커톤")
  private String projectTypeName;

  public static ProjectTypeResponse from(ProjectType entity) {
    return ProjectTypeResponse.builder()
        .projectTypeId(entity.getId())
        .projectTypeName(entity.getProjectTypeName())
        .build();
  }
}
