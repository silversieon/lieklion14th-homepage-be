/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.application.record.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "ApplicationSubmitResponse: 지원서 제출 응답 DTO")
public class ApplicationSubmitResponse {

  @Schema(description = "지원서 메타 정보")
  private ApplicationRecordMeta meta;
}
