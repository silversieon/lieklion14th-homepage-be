/* 
 * Copyright (c) SKU LIKELION 
 */
package com.likelionskuniv.website.domain.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
@Schema(title = "EmailVerificationConfirmReqeust: 인증 코드 검증 요청 DTO")
public class EmailVerificationConfirmReqeust {

  @Schema(description = "인증 코드를 받을 이메일 주소", example = "likelion@skuniv.ac.kr")
  private String email;

  @Schema(description = "인증 코드", example = "123456")
  private String code;
}
