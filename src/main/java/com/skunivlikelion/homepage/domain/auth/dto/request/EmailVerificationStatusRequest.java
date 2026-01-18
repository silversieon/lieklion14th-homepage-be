/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.auth.dto.request;

import jakarta.validation.constraints.Pattern;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
@Schema(title = "EmailVerificationStatusRequest: 이메일 검증 상태 확인 요청 DTO")
public class EmailVerificationStatusRequest {

  @Pattern(regexp = "^[A-Za-z0-9._%+-]+@skuniv\\.ac\\.kr$")
  @Schema(description = "이메일 검증 상태를 확인할 이메일 주소", example = "likelion@skuniv.ac.kr")
  private String email;
}
