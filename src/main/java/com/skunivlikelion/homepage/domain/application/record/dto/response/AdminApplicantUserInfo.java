/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.application.record.dto.response;

import com.skunivlikelion.homepage.domain.common.enums.Track;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
@Schema(title = "AdminApplicantUserInfo: 관리자 지원서 조회 사용자 정보 DTO")
public class AdminApplicantUserInfo {

  @Schema(description = "이름", example = "김나경")
  private String name;

  @Schema(description = "전화번호", example = "010-9757-5939")
  private String phoneNumber;

  @Schema(description = "학과", example = "공공인재학부")
  private String department;

  @Schema(description = "학번", example = "2022213003")
  private String studentNumber;

  @Schema(description = "이메일", example = "1030sk@skuniv.ac.kr")
  private String email;

  @Schema(description = "지원 트랙", example = "BACKEND")
  private Track supportPart;
}
