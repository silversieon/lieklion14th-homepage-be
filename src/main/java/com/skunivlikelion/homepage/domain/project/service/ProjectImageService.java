/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.project.service;

import java.util.List;

import com.skunivlikelion.homepage.domain.project.dto.request.UploadImagePayload;

public interface ProjectImageService {
  /**
   * [ 프로젝트 이미지 업로드 메서드 ]
   *
   * @param payloads 업로드할 프로젝트 이미지 페이로드
   * @param projectId 이미지를 가질 프로젝트 식별자
   * @return 프로젝트 이미지 리스트
   */
  void uploadProjectImages(List<UploadImagePayload> payloads, Long projectId);

  /**
   * [ 프로젝트 이미지 삭제 메서드 ]
   *
   * @param imageUrl 삭제할 이미지 URL
   */
  void deleteProjectImageByUrl(String imageUrl);
}
