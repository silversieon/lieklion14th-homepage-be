/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
@Schema(title = "SignUpRequest: 회원가입 요청 DTO")
public class SignUpRequest {

  @NotBlank
  @Pattern(regexp = "^[A-Za-z0-9._%+-]+@skuniv\\.ac\\.kr$")
  @Schema(description = "사용자 이메일", example = "likelion@skuniv.ac.kr")
  private String email;

  @NotBlank
  @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*])[A-Za-z\\d!@#$%^&*]{8,20}$")
  @Schema(description = "사용자 비밀번호", example = "lion1234!")
  private String password;

  @NotBlank
  @Size(min = 2, max = 17)
  @Schema(description = "이름(본명)", example = "윤희준")
  private String name;

  @NotBlank
  @Schema(description = "학과/학부", example = "소프트웨어학과")
  private String department;

  @NotBlank
  @Pattern(regexp = "^[0-9]{10}$")
  @Schema(description = "학번", example = "2023123456")
  private String studentNumber;

  @NotBlank
  @Pattern(regexp = "^01[016789]-\\d{3,4}-\\d{4}$")
  @Schema(description = "전화번호", example = "010-1234-5678")
  private String phoneNumber;
}
