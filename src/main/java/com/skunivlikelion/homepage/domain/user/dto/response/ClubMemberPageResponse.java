/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.user.dto.response;

import java.util.List;

import com.skunivlikelion.homepage.domain.common.enums.Track;
import com.skunivlikelion.homepage.domain.user.enums.Position;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(title = "ClubMemberPageResponse: 구성원 페이지 조회 응답 DTO")
public class ClubMemberPageResponse {

  @Schema(description = "역할군")
  private Position position;

  @Schema(description = "해당 트랙별 구성원")
  private List<ClubMembersOfTracks> clubMembersOfTracks;

  @Getter
  @Builder
  public static class ClubMembersOfTracks {

    @Schema(description = "트랙")
    private Track track;

    @Schema(description = "해당 역할군, 트랙에 소속된 구성원 리스트")
    private List<ClubMemberSummary> clubMemberSummaryList;
  }

  @Getter
  @Builder
  public static class ClubMemberSummary {

    @Schema(description = "사용자 이름", example = "김나경")
    private String name;

    @Schema(description = "학과", example = "공공인재학과")
    private String department;

    @Schema(description = "학번 (앞부분)", example = "22")
    private String shortStudentNumber;

    @Schema(description = "프로필 이미지URL")
    private String profileImageUrl;
  }
}
