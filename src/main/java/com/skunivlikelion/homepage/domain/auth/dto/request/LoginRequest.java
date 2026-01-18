/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
@Schema(title = "LoginRequest: 로그인 요청 DTO")
public class LoginRequest {

  @Pattern(regexp = "^[A-Za-z0-9._%+-]+@skuniv\\.ac\\.kr$")
  @Schema(description = "사용자 이메일", example = "likelion@skuniv.ac.kr")
  private String email;

  @NotBlank
  @Schema(description = "사용자 비밀번호", example = "lion1234!")
  private String password;
}
