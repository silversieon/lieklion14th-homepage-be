/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.user.dto.response;

import com.skunivlikelion.homepage.domain.common.enums.Track;
import com.skunivlikelion.homepage.domain.user.enums.Position;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(title = "ClubMemberInformationResponse: 게스트 관리 페이지 구성원 조회 응답 DTO")
public class ClubMemberInformationResponse {

  @Schema(description = "사용자 식별자", example = "1")
  private Long userId;

  @Schema(description = "구성원 식별자", example = "2")
  private Long clubMemberId;

  @Schema(description = "기수", example = "14")
  private Long semester;

  @Schema(description = "역할", example = "LEAD")
  private Position position;

  @Schema(description = "사용자 이름", example = "윤희준")
  private String name;

  @Schema(description = "트랙", example = "BACKEND")
  private Track track;

  @Schema(description = "사용자 학과", example = "소프트웨어학과")
  private String department;

  @Schema(description = "사용자 학번", example = "2020123456")
  private String studentNumber;
}
