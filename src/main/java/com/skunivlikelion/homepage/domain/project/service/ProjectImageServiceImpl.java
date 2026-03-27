/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.project.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.skunivlikelion.homepage.domain.project.dto.internal.UploadImagePayload;
import com.skunivlikelion.homepage.domain.project.entity.Project;
import com.skunivlikelion.homepage.domain.project.entity.ProjectImage;
import com.skunivlikelion.homepage.domain.project.exception.ProjectErrorCode;
import com.skunivlikelion.homepage.domain.project.repository.ProjectImageRepository;
import com.skunivlikelion.homepage.domain.project.repository.ProjectRepository;
import com.skunivlikelion.homepage.global.exception.CustomException;
import com.skunivlikelion.homepage.global.s3.enums.PathName;
import com.skunivlikelion.homepage.global.s3.service.S3Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ProjectImageServiceImpl implements ProjectImageService {

  private final ProjectImageRepository projectImageRepository;
  private final S3Service s3Service;
  private final Executor projectParallel;
  private final ProjectRepository projectRepository;

  public ProjectImageServiceImpl(
      ProjectImageRepository projectImageRepository,
      S3Service s3Service,
      @Qualifier("projectParallel") Executor projectParallel,
      ProjectRepository projectRepository) {
    this.projectImageRepository = projectImageRepository;
    this.s3Service = s3Service;
    this.projectParallel = projectParallel;
    this.projectRepository = projectRepository;
  }

  @Override
  @Transactional
  public void uploadProjectImages(Long projectId, List<UploadImagePayload> payloads) {
    Project project = getProjectById(projectId);

    try {
      List<CompletableFuture<String>> futures =
          payloads.stream()
              .map(
                  payload ->
                      CompletableFuture.supplyAsync(
                          () -> s3Service.uploadByte(PathName.PROJECT, payload.bytes()),
                          projectParallel))
              .toList();

      List<String> urls = futures.stream().map(CompletableFuture::join).toList();
      List<ProjectImage> projectImages =
          urls.stream()
              .map(url -> ProjectImage.builder().project(project).imageUrl(url).build())
              .toList();
      projectImageRepository.saveAll(projectImages);
      log.info(
          "[Project] 프로젝트 이미지 전체 업로드 성공 - projectId = {}, 이미지 수= {}",
          project.getId(),
          payloads.size());
    } catch (Exception e) {
      throw new CustomException(ProjectErrorCode.PROJECT_IMAGE_UPLOAD_FAIL);
    }
  }

  @Override
  public void deleteProjectImagesByUrl(List<String> deletedImageUrls) {
    for (String deletedImageUrl : deletedImageUrls) {
      deleteProjectImageByUrl(deletedImageUrl);
    }
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

  @Override
  public List<String> preDeleteProjectImages(Project project, List<Long> remainingImageIds) {
    List<String> deletedImageUrls = new ArrayList<>();
    project
        .getProjectImages()
        .removeIf(
            pi -> {
              boolean remove = !remainingImageIds.contains(pi.getId());
              if (remove) {
                deletedImageUrls.add(pi.getImageUrl());
                pi.setProject(null);
              }
              return remove;
            });
    return deletedImageUrls;
  }

  private Project getProjectById(Long projectId) {
    return projectRepository
        .findById(projectId)
        .orElseThrow(() -> new CustomException(ProjectErrorCode.NOT_FOUND_PROJECT));
  }
}
