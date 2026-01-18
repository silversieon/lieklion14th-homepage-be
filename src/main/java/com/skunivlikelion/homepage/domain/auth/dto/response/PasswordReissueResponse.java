/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.auth.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(title = "PasswordResetResponse: 비밀번호 찾기 성공 응답 DTO")
public class PasswordReissueResponse {

  @Schema(description = "비밀번호 찾기를 요청한 사용자 이메일", example = "likelion@skuniv.ac.kr")
  private String email;

  @Schema(description = "임시 발급된 비밀번호")
  private String temporaryPassword;
}
