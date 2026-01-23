/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(title = "MyInformationResponse: 지원서 조회, 작성 페이지 내 상세 정보 조회 응답 DTO")
public class MyInformationResponse {

  @Schema(description = "사용자 이름", example = "신채린")
  private String name;

  @Schema(description = "사용자 이메일", example = "newchaerin12@gmail.com")
  private String email;

  @Schema(description = "사용자 학과", example = "소프트웨어학과")
  private String department;

  @Schema(description = "사용자 학번", example = "2023661662")
  private String studentNumber;

  @Schema(description = "사용자 전화번호", example = "010-8872-3793")
  private String phoneNumber;
}
