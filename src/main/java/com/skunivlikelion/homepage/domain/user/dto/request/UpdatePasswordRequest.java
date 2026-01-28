/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
@Schema(title = "UpdatePasswordRequest: 비밀번호 변경 요청 DTO")
public class UpdatePasswordRequest {

  @NotBlank
  @Schema(description = "사용자의 현재 비밀번호", example = "likelion1234!")
  private String currentPassword;

  @NotBlank
  @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*])[A-Za-z\\d!@#$%^&*]{8,20}$")
  @Schema(description = "사용자의 새 비밀번호", example = "likelion5678!")
  private String newPassword;

  @NotBlank
  @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*])[A-Za-z\\d!@#$%^&*]{8,20}$")
  @Schema(description = "사용자의 새 비밀번호 확인", example = "likelion5678!")
  private String newPasswordConfirmation;
}
