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
@Schema(title = "UpdateUserInformationRequest: 사용자 정보 변경 요청 DTO")
public class UpdateUserInformationRequest {

  @Schema(description = "이름(본명)", example = "정영진")
  private String name;

  @Schema(description = "학과/학부", example = "소프트웨어학과")
  private String department;

  @Schema(description = "학번", example = "2023123456")
  private String studentNumber;

  @Schema(description = "전화번호", example = "010-1234-5678")
  private String phoneNumber;
}
