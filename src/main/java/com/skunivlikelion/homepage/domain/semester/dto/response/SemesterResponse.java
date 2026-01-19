/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.semester.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
@Schema(title = "SemesterResponse: 기수 조회 응답 DTO")
public class SemesterResponse {

  @Schema(description = "기수", example = "14")
  private Long semester;
}
