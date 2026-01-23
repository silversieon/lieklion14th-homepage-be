/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(title = "UserInformationResponse: 게스트 관리 페이지 조회 사용자 정보 응답 DTO")
public class UserInformationResponse {

  @Schema(description = "사용자 식별자", example = "1")
  private Long userId;

  @Schema(description = "사용자 이름", example = "금시언")
  private String name;

  @Schema(description = "사용자 학과", example = "소프트웨어학과")
  private String department;

  @Schema(description = "사용자 학번", example = "2021456789")
  private String studentNumber;
}
