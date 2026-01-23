/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
@Schema(title = "UpdatePasswordRequest: 비밀번호 변경 요청 DTO")
public class UpdatePasswordRequest {

  @Schema(description = "사용자의 현재 비밀번호", example = "likelion1234!")
  private String currentPassword;

  @Schema(description = "사용자의 새 비밀번호", example = "likelion5678!")
  private String newPassword;

  @Schema(description = "사용자의 새 비밀번호 확인", example = "likelion5678!")
  private String newPasswordConfirmation;
}
