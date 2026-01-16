/* 
 * Copyright (c) SKU LIKELION 
 */
package com.likelionskuniv.website.domain.auth.dto.request;

import jakarta.validation.constraints.Pattern;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(title = "EmailVerificationSendRequest: 인증 코드 전송 요청 DTO")
public class EmailVerificationSendRequest {

  @Pattern(regexp = "^[A-Za-z0-9._%+-]+@skuniv\\.ac\\.kr$")
  @Schema(description = "인증 코드를 받을 이메일 주소", example = "likelion@skuniv.ac.kr")
  private String email;
}
