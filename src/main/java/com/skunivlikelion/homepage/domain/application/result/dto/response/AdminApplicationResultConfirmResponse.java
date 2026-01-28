/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.application.result.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(title = "AdminApplicationResultConfirmResponse: 관리자 최종 합격 여부 확정 성공 응답 DTO")
public class AdminApplicationResultConfirmResponse {

  private final Long applicationRecordId;
  private final boolean passed;
}
