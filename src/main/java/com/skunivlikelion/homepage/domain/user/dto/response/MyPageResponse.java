/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(title = "MyPageResponse: 마이페이지 내 정보 조회 응답 DTO")
public class MyPageResponse {

  @Schema(description = "사용자 이름", example = "임다현")
  private String name;

  @Schema(description = "사용자 이메일", example = "dahyun12@gmail.com")
  private String email;

  @Schema(description = "사용자 프로필 이미지 URL", example = "https://amazon.image.jpg")
  private String profileImageUrl;

  @Schema(description = "지원서 작성 또는 확인 가능 여부", example = "true")
  private boolean documentActive;

  @Schema(description = "지원서 제출 여부", example = "true")
  private boolean documentSubmitted;

  @Schema(description = "면접 일정 변경 가능 여부", example = "false")
  private boolean interviewScheduleChangeable;

  @Schema(description = "최종 결과 확인 가능 여부", example = "true")
  private boolean finalResultConfirmation;
}
