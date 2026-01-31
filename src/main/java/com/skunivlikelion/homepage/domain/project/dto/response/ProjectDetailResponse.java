/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.project.dto.response;

import java.util.List;
import java.util.Map;

import com.skunivlikelion.homepage.domain.common.enums.Track;
import com.skunivlikelion.homepage.domain.project.entity.ProjectType;
import com.skunivlikelion.homepage.domain.semester.entity.Semester;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProjectDetailResponse {
  @Schema(description = "프로젝트 식별자", example = "1")
  private Long id;

  @Schema(description = "프로젝트 제목", example = "2025 서경대학교 대동제 축제 안내 페이지")
  private String title;

  @Schema(description = "수상여부", example = "수상 O")
  private boolean award;

  @Schema(description = "기수식별자", example = "14기")
  private Semester semester;

  @Schema(description = "프로젝트타입", example = "중앙해커톤")
  private ProjectType projectType;

  @Schema(description = "프로젝트 설명", example = "2025년도 서경대학교 대동제 축제 안내 페이지입니다.")
  private String content;

  @Schema(
      description = "트랙별 참여자 이름 목록 (key=트랙, value=이름 리스트)",
      example =
          "{\"PM\":[\"홍길동\"],\"DESIGN\":[\"김디자이너\"],\"FRONTEND\":[\"이프론트\",\"박프론트\"],\"BACKEND\":[\"최백\"]}")
  private Map<Track, List<String>> members;

  @Schema(description = "이미지 URL 리스트")
  private List<String> imageUrls;
}
