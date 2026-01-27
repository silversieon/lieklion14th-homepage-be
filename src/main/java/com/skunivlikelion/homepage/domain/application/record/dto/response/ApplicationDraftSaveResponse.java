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
@Schema(title = "ApplicationDraftSaveResponse: 지원서 임시 저장 응답 DTO")
public class ApplicationDraftSaveResponse {

  @Schema(description = "지원서 메타 정보")
  private ApplicationRecordMeta meta;
}
