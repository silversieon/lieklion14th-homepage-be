/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.application.result.dto.request;

import jakarta.validation.constraints.NotNull;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(title = "AdminDocumentResultUpdateRequest: 관리자 서류 합격 여부 변경 요청 DTO")
public class AdminDocumentResultUpdateRequest {

  @NotNull private final Boolean isDocumentPassed;
}
