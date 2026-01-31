/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.project.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.skunivlikelion.homepage.domain.project.entity.Project;

import lombok.Builder;
import lombok.Getter;

public interface ProjectImageUpdateService {
  ImageUpdateResult updateProjectImages(
      Project project, List<String> remainingImageUrls, List<MultipartFile> newImages);

  @Getter
  @Builder
  public static class ImageUpdateResult {
    private final String thumbnailUrl;
    private final int deletedCount;
    private final int newImagesCount;
  }
}
