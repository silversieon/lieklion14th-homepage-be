/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.application.result.dto.response;

import com.skunivlikelion.homepage.domain.common.enums.Track;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(title = "MyInterviewResultResponse: 사용자 면접 결과 조회 응답 DTO")
public class MyInterviewResultResponse {

  @Schema(description = "서류 합격 여부", example = "true")
  private boolean documentPassed;

  @Schema(description = "면접 합격 여부", example = "true")
  private boolean interviewPassed;

  @Schema(description = "제출 트랙", example = "PO")
  private Track track;

  @Schema(description = "기수", example = "14")
  private Long semester;
}
