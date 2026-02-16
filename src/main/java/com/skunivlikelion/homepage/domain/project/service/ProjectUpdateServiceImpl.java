/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.project.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.skunivlikelion.homepage.domain.common.enums.Track;
import com.skunivlikelion.homepage.domain.project.dto.request.UploadImagePayload;
import com.skunivlikelion.homepage.domain.project.entity.Project;
import com.skunivlikelion.homepage.domain.project.entity.ProjectImage;
import com.skunivlikelion.homepage.domain.project.entity.ProjectMember;
import com.skunivlikelion.homepage.domain.project.exception.ProjectErrorCode;
import com.skunivlikelion.homepage.domain.project.repository.ProjectImageRepository;
import com.skunivlikelion.homepage.domain.project.repository.ProjectMemberRepository;
import com.skunivlikelion.homepage.global.exception.CustomException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectUpdateServiceImpl implements ProjectUpdateService {

  private final ProjectImageRepository projectImageRepository;
  private final ProjectImageService projectImageService;
  private final ProjectMemberRepository projectMemberRepository;

  @Override
  public void updateProjectImages(
      Project project, List<Long> remainingImageIds, List<UploadImagePayload> payloads) {
    List<ProjectImage> existingImages =
        projectImageRepository.findImagesByProjectId(project.getId());

    int deletedCount = 0;

    try {
      for (ProjectImage image : existingImages) {
        if (!remainingImageIds.contains(image.getId())) {
          projectImageRepository.deleteById(image.getId());
          deletedCount++;
        }
      }
    } catch (Exception e) {
      log.error(
          "[Project] 이미지 삭제 실패 - project={}, remainingImageUrls={}",
          project.getId(),
          remainingImageIds,
          e);
      throw new CustomException(ProjectErrorCode.PROJECT_IMAGE_DELETE_FAIL);
    }

    int newImagesCount = 0;
    try {
      if (payloads != null && !payloads.isEmpty()) {
        projectImageService.uploadProjectImages(payloads, project.getId());
        newImagesCount = payloads.size();
      }
    } catch (Exception e) {
      log.error("[Project] 이미지 업로드 실패 - project={}", project.getId(), e);
      throw new CustomException(ProjectErrorCode.PROJECT_IMAGE_UPLOAD_FAIL);
    }

    log.info(
        "[Project] 이미지 수정 완료 - project: {}, deletedImages: {}, newImagesCount: {}",
        project.getId(),
        deletedCount,
        newImagesCount);
  }

  @Override
  public List<ProjectMember> updateProjectMembers(
      Project project, List<Long> remainingMemberIds, Map<Track, List<String>> newMembers) {
    project.getProjectMembers().removeIf(pm -> !remainingMemberIds.contains(pm.getId()));

    Set<Track> tracks = newMembers.keySet();
    for (Track track : tracks) {
      List<String> projectMemberNames = newMembers.get(track);
      for (String name : projectMemberNames) {
        ProjectMember projectMember =
            ProjectMember.builder().track(track).projectMemberName(name).project(project).build();
        ProjectMember savedProjectMember = projectMemberRepository.save(projectMember);
        project.addProjectMember(savedProjectMember);
      }
    }
    return project.getProjectMembers();
  }
}
