/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.project.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.skunivlikelion.homepage.domain.project.entity.Project;
import com.skunivlikelion.homepage.domain.project.entity.ProjectImage;
import com.skunivlikelion.homepage.domain.project.exception.ProjectErrorCode;
import com.skunivlikelion.homepage.domain.project.repository.ProjectImageRepository;
import com.skunivlikelion.homepage.global.s3.enums.PathName;
import com.skunivlikelion.homepage.global.s3.service.S3Service;

import backend.boilerplate.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectImageServiceImpl implements ProjectImageService {

  private final ProjectImageRepository projectImageRepository;
  private final S3Service s3Service;

  @Override
  public List<ProjectImage> uploadProjectImages(
      List<MultipartFile> projectImages, Project project) {

    for (MultipartFile file : projectImages) {
      String imageUrl = s3Service.uploadFile(PathName.PROJECT, file);
      ProjectImage image = ProjectImage.builder().project(project).imageUrl(imageUrl).build();
      project.addProjectImage(projectImageRepository.save(image));
    }
    return project.getProjectImages();
  }

  @Override
  public void deleteProjectImageByUrl(String imageUrl) {
    try {
      String keyName = s3Service.extractKeyNameFromUrl(imageUrl);
      s3Service.deleteFile(keyName);
    } catch (Exception e) {
      log.error("[Project] 이미지 삭제 실패 - project={}", imageUrl, e);
      throw new CustomException(ProjectErrorCode.PROJECT_IMAGE_DELETE_FAIL);
    }
  }
}
