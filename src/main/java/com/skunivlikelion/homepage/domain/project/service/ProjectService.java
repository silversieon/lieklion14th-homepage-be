/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.project.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import com.skunivlikelion.homepage.domain.project.dto.request.ProjectCreateRequest;
import com.skunivlikelion.homepage.domain.project.dto.response.ProjectDetailResponse;
import com.skunivlikelion.homepage.domain.project.dto.response.ProjectPostResponse;
import com.skunivlikelion.homepage.domain.project.dto.response.ProjectResponse;
import com.skunivlikelion.homepage.domain.project.dto.response.ProjectUpdateResponse;

public interface ProjectService {
  /**
   * [프로젝트 생성 메서드] 성공 여부에 따라 ProjectPostResponse 객체에 성공 여부를 담아 반환
   *
   * @param request 프로젝트 생성 요청을 위한 프로젝트 정보를 담은 요청 객체
   * @param projectImages 프로젝트 이미지들 모음을 담은 객체
   * @return 등록된 프로젝트 정보를 담은 ProjectPostResponse 객체
   */
  ProjectPostResponse createProject(
      ProjectCreateRequest request, List<MultipartFile> projectImages);

  /**
   * [프로젝트 수정 메소드] 성공 여부에 따라 ProjectUpdateResponse 객체에 성공 여부를 담아 반환
   *
   * @param id 수정할 프로젝트 값
   * @param request 프로젝트 수정 요청을 위한 프로젝트 정보를 담은 요청 객체
   * @param remainingImageUrls 수정 시 유지할 기존 이미지 URL 목록
   * @param newImages 새로운 이미지들의 모음
   * @return 수정된 프로젝트 정보를 담은 ProjectUpdateResponse 객체
   */
  ProjectUpdateResponse updateProject(
      Long id,
      ProjectCreateRequest request,
      List<String> remainingImageUrls,
      List<MultipartFile> newImages);

  /**
   * [ 프로젝트 삭제 메서드 ] 프로젝트 삭제
   *
   * @param id 삭제할 프로젝트 값
   */
  void deleteProject(Long id);

  /**
   * [프로젝트 목록 조회(전체/기수/타입/검색) 메서드] 프로젝트 리스트 조회
   *
   * @param projectTypeId 조회할 프로젝트 타입 값
   * @param semester 조회할 기수 값
   * @param search 조회할 검색어
   * @param page 조회할 페이지
   * @return 조회 응답 DTO
   */
  Page<ProjectResponse> getProjectByPageAndSemesterAndTypeAndSearch(
      Long projectTypeId, Long semester, String search, Integer page);

  /**
   * [프로젝트 단일 조회 메서드] 특정 프로젝트 정보 상세 조회
   *
   * @param id 상세 조회할 프로젝트 식별자
   * @return 프로젝트 정보를 담은 DTO
   */
  ProjectDetailResponse getProjectByProjectId(Long id);

  Page<ProjectResponse> getAwardProjectsByPage(Integer page, Integer size);
}
