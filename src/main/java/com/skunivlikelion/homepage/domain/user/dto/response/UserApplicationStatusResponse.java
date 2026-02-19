/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(title = "UserApplicationStatusResponse: 사용자 지원 정보 응답 DTO")
public class UserApplicationStatusResponse {

  @Schema(description = "지원서 제출 여부", example = "true")
  private boolean documentSubmitted;

  @Schema(description = "면접 일정 확정 여부", example = "false")
  private boolean interviewScheduleConfirmed;
}
