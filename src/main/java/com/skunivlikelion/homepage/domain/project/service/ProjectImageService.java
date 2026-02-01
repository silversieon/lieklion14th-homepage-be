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
   * [ 프로젝트 이미지 업로드 메서드 ]
   *
   * @param projectImages 업로드할 프로젝트 이미지
   * @param project 이미지를 가질 프로젝트 객체
   * @return 프로젝트 이미지 리스트
   */
  List<ProjectImage> uploadProjectImages(List<MultipartFile> projectImages, Project project);

  /**
   * [ 프로젝트 이미지 삭제 메서드 ]
   *
   * @param imageUrl 삭제할 이미지 URL
   */
  void deleteProjectImageByUrl(String imageUrl);
}
