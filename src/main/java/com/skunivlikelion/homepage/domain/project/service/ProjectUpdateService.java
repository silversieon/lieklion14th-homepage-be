/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.project.service;

import java.util.List;
import java.util.Map;

import com.skunivlikelion.homepage.domain.common.enums.Track;
import com.skunivlikelion.homepage.domain.project.dto.request.UploadImagePayload;
import com.skunivlikelion.homepage.domain.project.entity.Project;
import com.skunivlikelion.homepage.domain.project.entity.ProjectMember;

public interface ProjectUpdateService {

  /**
   * [ 이미지 수정 메서드 ]
   *
   * @param project 이미지를 변경할 프로젝트
   * @param remainingImageIds 남길 이미지 식별자
   * @param payloads 새 이미지 페이로드 리스트
   */
  void updateProjectImages(
      Project project, List<Long> remainingImageIds, List<UploadImagePayload> payloads);

  List<ProjectMember> updateProjectMembers(
      Project project, List<Long> remainingMemberIds, Map<Track, List<String>> newMembers);
}
