/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.application.record.dto.response;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
@Schema(title = "ApplicationSubmitDateResponse: 지원서 제출 일자 조회 응답 DTO")
public class ApplicationSubmitDateResponse {

  @Schema(description = "제출 여부", example = "true")
  private boolean isSubmitted;

  @Schema(description = "제출 일시 (미제출시 null)")
  private LocalDateTime submittedAt;
}
