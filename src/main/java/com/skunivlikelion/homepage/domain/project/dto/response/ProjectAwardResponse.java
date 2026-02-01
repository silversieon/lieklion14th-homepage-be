/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.project.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(title = "ProjectAwardResponse: 프로젝트 수상작 응답 DTO")
public class ProjectAwardResponse {

  @Schema(description = "프로젝트 식별자", example = "1")
  private Long projectId;

  @Schema(description = "프로젝트 썸네일 이미지", example = "https://sdjfnsdkjnwe.png")
  private String thumbnailUrl;
}
