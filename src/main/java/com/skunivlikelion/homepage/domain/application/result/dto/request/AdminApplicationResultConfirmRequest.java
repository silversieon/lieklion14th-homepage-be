/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.application.result.dto.request;

import jakarta.validation.constraints.NotNull;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
@Schema(title = "AdminInterviewResultConfirmRequest: 관리자 면접 결과 확정 요청 DTO")
public class AdminApplicationResultConfirmRequest {

  @NotNull private final Boolean passed;
}
