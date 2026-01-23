/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(title = "CreateUserResponse: 임의의 사용자 생성 응답 DTO")
public class CreateUserResponse {

  @Schema(description = "사용자 식별자", example = "12")
  private Long userId;

  @Schema(description = "사용자 이메일", example = "test3@skuniv.ac.kr")
  private String email;

  @Schema(description = "이름(본명)", example = "정목진")
  private String name;

  @Schema(description = "학과/학부", example = "소프트웨어학과")
  private String department;

  @Schema(description = "학번", example = "2022123456")
  private String studentNumber;

  @Schema(description = "전화번호", example = "010-1234-5678")
  private String phoneNumber;
}
