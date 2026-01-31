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
@Schema(title = "ClubMemberCursorResponse DTO", description = "구성원 응답에 대한 리스트를 무한 스크롤로 반환")
public class ClubMemberCursorResponse<T> {

  @Schema(description = "데이터")
  private T content;

  @Schema(description = "다음 역할의 커서 값")
  private Position nextPositionCursor;

  @Schema(description = "다음 트랙의 커서 값")
  private Track nextTrackCursor;

  @Schema(description = "더 가져올 데이터가 있는지 여부")
  private Boolean hasNext;
}
