/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.project.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(title = "ProjectImageResponse: 프로젝트 이미지 응답 DTO")
public class ProjectImageResponse {

  @Schema(description = "프로젝트 이미지 식별자", example = "1")
  private Long projectImageId;

  @Schema(description = "프로젝트 이미지 URL", example = "https://fdskkgjsdfn.png")
  private String imageUrl;
}
