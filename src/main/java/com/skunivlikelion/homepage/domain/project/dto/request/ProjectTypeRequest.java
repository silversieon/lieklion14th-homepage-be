/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.project.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
@Schema(title = "ProjectTypeRequest: 프로젝트타입 요청 DTO")
public class ProjectTypeRequest {

  @NotBlank(message = "projectTypeName은 필수입니다.")
  @Size(max = 30, message = "projectTypeName은 30자 이내여야 합니다.")
  @Schema(description = "프로젝트 타입 이름", example = "중앙해커톤")
  private String projectTypeName;
}
