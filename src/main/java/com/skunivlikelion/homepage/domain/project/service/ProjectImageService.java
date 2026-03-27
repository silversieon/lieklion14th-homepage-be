/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.project.service;

import java.util.List;

import com.skunivlikelion.homepage.domain.project.dto.internal.UploadImagePayload;
import com.skunivlikelion.homepage.domain.project.entity.Project;

public interface ProjectImageService {
  /**
   * [ 프로젝트 이미지 업로드 메서드 ]
   *
   * @param projectId 이미지를 가질 프로젝트
   * @param payloads 업로드할 프로젝트 이미지 페이로드 리스트
   * @return 프로젝트 이미지 리스트
   */
  void uploadProjectImages(Long projectId, List<UploadImagePayload> payloads);

  /**
   * [ 프로젝트 이미지를 S3에서 삭제하는 메서드 ]
   *
   * @param imageUrl 삭제할 이미지 URL
   */
  void deleteProjectImageByUrl(String imageUrl);

  /**
   * [ 프로젝트 이미지를 DB에서 삭제하는 메서드 (트랜잭션 필요) ]
   *
   * @param project 이미지를 고아객체로 만들 프로젝트
   * @param remainingImageIds 남길 이미지 식별자 리스트
   * @return DB에서 삭제된 이미지들의 Url 리스트 (S3에서 삭제 필요로 인한 반환)
   */
  List<String> preDeleteProjectImages(Project project, List<Long> remainingImageIds);

  /**
   * [ 프로젝트 이미지 리스트를 S3에서 삭제하는 메서드 ]
   *
   * @param deletedImageUrls 삭제할 이미지 Url 리스트
   */
  void deleteProjectImagesByUrl(List<String> deletedImageUrls);
}
