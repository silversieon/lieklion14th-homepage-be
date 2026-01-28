/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.application.result.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(title = "AdminDocumentResultUpdateResponse: 관리자 서류 합격 여부 변경 응답 DTO")
public class AdminDocumentResultUpdateResponse {

  private final Long applicationRecordId;
  private final boolean isDocumentPassed;
}
