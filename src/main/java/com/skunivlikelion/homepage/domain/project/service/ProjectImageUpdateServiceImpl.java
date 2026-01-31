/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.project.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.skunivlikelion.homepage.domain.project.entity.Project;
import com.skunivlikelion.homepage.domain.project.entity.ProjectImage;
import com.skunivlikelion.homepage.domain.project.exception.ProjectErrorCode;
import com.skunivlikelion.homepage.domain.project.repository.ProjectImageRepository;

import backend.boilerplate.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectImageUpdateServiceImpl implements ProjectImageUpdateService {

  private final ProjectImageRepository projectImageRepository;
  private final ProjectImageService projectImageService;

  @Override
  @Transactional
  public ImageUpdateResult updateProjectImages(
      Project project, List<String> remainingImageUrls, List<MultipartFile> newImages) {
    if (remainingImageUrls == null) {
      remainingImageUrls = List.of();
    }
    List<ProjectImage> existingImages =
        projectImageRepository.findImagesByProjectId(project.getId());

    int deletedCount = 0;

    try {
      for (ProjectImage image : existingImages) {
        if (!remainingImageUrls.contains(image.getImageUrl())) {
          projectImageService.deleteProjectImageByUrl(image.getImageUrl());
          projectImageRepository.delete(image);
          deletedCount++;
        }
      }
    } catch (Exception e) {
      log.error(
          "[Project] 이미지 삭제 실패 - project={}, remainingImageUrls={}",
          project.getId(),
          remainingImageUrls,
          e);
      throw new CustomException(ProjectErrorCode.PROJECT_IMAGE_DELETE_FAIL);
    }

    int newImagesCount = 0;
    try {
      if (newImages != null && !newImages.isEmpty()) {
        projectImageService.uploadProjectImages(newImages, project);
        newImagesCount = newImages.size();
      }
    } catch (Exception e) {
      log.error("[Project] 이미지 업로드 실패 - project={}", project.getId(), e);
      throw new CustomException(ProjectErrorCode.PROJECT_IMAGE_UPLOAD_FAIL);
    }
    List<ProjectImage> finalImages = projectImageRepository.findImagesByProjectId(project.getId());

    String thumbnailUrl = finalImages.isEmpty() ? null : finalImages.get(0).getImageUrl();

    return ImageUpdateResult.builder()
        .thumbnailUrl(thumbnailUrl)
        .deletedCount(deletedCount)
        .newImagesCount(newImagesCount)
        .build();
  }
}
