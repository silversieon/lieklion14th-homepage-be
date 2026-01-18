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
@Schema(title = "EmailVerificationConfirmReqeust: 인증 코드 검증 요청 DTO")
public class EmailVerificationConfirmReqeust {

  @Pattern(regexp = "^[A-Za-z0-9._%+-]+@skuniv\\.ac\\.kr$")
  @Schema(description = "인증 코드를 받을 이메일 주소", example = "likelion@skuniv.ac.kr")
  private String email;

  @Pattern(regexp = "^[0-9]{6}$")
  @Schema(description = "인증 코드", example = "123456")
  private String code;
}
