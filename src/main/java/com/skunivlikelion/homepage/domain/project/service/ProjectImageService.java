/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.project.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.skunivlikelion.homepage.domain.project.entity.Project;
import com.skunivlikelion.homepage.domain.project.entity.ProjectImage;

public interface ProjectImageService {
  /**
   * @param projectImages
   * @param project
   * @return
   */
  List<ProjectImage> uploadProjectImages(List<MultipartFile> projectImages, Project project);

  void deleteProjectImageByUrl(String imageUrl);
}
