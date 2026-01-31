/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.project.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProjectPostResponse {
  @Schema(description = "생성된 프로젝트 식별자", example = "1")
  private String id;

  @Schema(description = "프로젝트의 이미지 개수", example = "5")
  private Integer imageCount;
}
