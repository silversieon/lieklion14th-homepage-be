/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.project.dto.request;

import java.util.List;
import java.util.Map;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import com.skunivlikelion.homepage.domain.common.enums.Track;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
@Schema(title = "ProjectUpdateRequest: 프로젝트 수정 요청 DTO")
public class ProjectUpdateRequest {

  @NotBlank(message = "title은 필수입니다.")
  @Size(max = 100, message = "title은 100자 이내여야 합니다.")
  @Schema(description = "프로젝트 제목", example = "2025 서경대학교 대동제 축제 안내 페이지")
  private String title;

  @Schema(description = "기수식별자", example = "14")
  @NotNull(message = "semesterId는 필수입니다.") @Positive private Long semesterId;

  @Schema(description = "수상여부", example = "false")
  private boolean award;

  @Schema(description = "프로젝트 타입 ID", example = "1")
  @NotNull(message = "projectTypeId는 필수입니다.") @Positive private Long projectTypeId;

  @NotBlank(message = "content는 필수입니다.")
  @Size(max = 300, message = "content은 300자 이내여야 합니다.")
  @Schema(description = "프로젝트 설명", example = "2025년도 서경대학교 대동제 축제 안내 페이지입니다.")
  private String content;

  @Schema(description = "변경 시 유지할 프로젝트 멤버 식별자들", example = "[1, 3, 7]")
  private List<Long> remainingProjectMemberIds;

  @Schema(
      description = "새로 추가할 트랙별 참여자 이름 목록 (key=트랙, value=이름 리스트)",
      example =
          "{\"PM\":[\"홍길동\"],\"DESIGN\":[\"김디자이너\"],\"FRONTEND\":[\"이프론트\",\"박프론트\"],\"BACKEND\":[\"최백\"]}")
  private Map<Track, List<String>> newProjectMembers;

  @Schema(description = "변경 시 유지할 이미지 식별자들", example = "[1, 3, 7]")
  private List<Long> remainingProjectImageIds;
}
