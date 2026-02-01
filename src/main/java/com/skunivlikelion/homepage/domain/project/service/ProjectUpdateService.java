/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.project.service;

import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.skunivlikelion.homepage.domain.common.enums.Track;
import com.skunivlikelion.homepage.domain.project.dto.response.ProjectImageResponse;
import com.skunivlikelion.homepage.domain.project.entity.Project;
import com.skunivlikelion.homepage.domain.project.entity.ProjectMember;

public interface ProjectUpdateService {

  /**
   * [ 이미지 수정 메서드 ]
   *
   * @param project 이미지를 변경할 프로젝트
   * @param remainingImageIds 남길 이미지 식별자
   * @param newImages 새 이미지 리스트
   * @return ProjectImageResponse 리스트
   */
  List<ProjectImageResponse> updateProjectImages(
      Project project, List<Long> remainingImageIds, List<MultipartFile> newImages);

  List<ProjectMember> updateProjectMembers(
      Project project, List<Long> remainingMemberIds, Map<Track, List<String>> newMembers);
}
