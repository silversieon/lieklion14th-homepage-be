/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.project.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.skunivlikelion.homepage.domain.project.dto.request.ProjectCreateRequest;
import com.skunivlikelion.homepage.domain.project.dto.request.ProjectUpdateRequest;
import com.skunivlikelion.homepage.domain.project.dto.response.*;
import com.skunivlikelion.homepage.global.page.response.InfiniteResponse;

/**
 * 멋쟁이사자처럼 홈페이지 프로젝트 관련 Service interface 입니다.
 *
 * @since 2026.02.01
 * @see com.skunivlikelion.homepage.domain.project.entity.Project
 * @see com.skunivlikelion.homepage.domain.project.entity.ProjectImage
 * @see com.skunivlikelion.homepage.domain.project.entity.ProjectMember
 * @see com.skunivlikelion.homepage.domain.project.controller.ProjectController
 * @author Lim Da Hyun, Keum Si Eon
 * @version latest: 1
 */
public interface ProjectService {
  /**
   * [프로젝트 생성 메서드] 성공 여부에 따라 ProjectPostResponse 객체에 성공 여부를 담아 반환
   *
   * @param request 프로젝트 생성 요청을 위한 프로젝트 정보를 담은 요청 객체
   * @param projectImages 프로젝트 이미지들 모음을 담은 객체
   * @return 등록된 프로젝트 정보를 담은 ProjectPostResponse 객체
   */
  ProjectResponse createProject(ProjectCreateRequest request, List<MultipartFile> projectImages);

  /**
   * [프로젝트 수정 메소드] 성공 여부에 따라 ProjectUpdateResponse 객체에 성공 여부를 담아 반환
   *
   * @param id 수정할 프로젝트 값
   * @param request 프로젝트 수정 요청을 위한 프로젝트 정보를 담은 요청 객체
   * @param newImages 새로운 이미지들의 모음
   * @return 수정된 프로젝트 정보를 담은 ProjectUpdateResponse 객체
   */
  ProjectUpdateResponse updateProject(
      Long id, ProjectUpdateRequest request, List<MultipartFile> newImages);

  /**
   * [ 프로젝트 삭제 메서드 ] 프로젝트 삭제
   *
   * @param projectId 삭제할 프로젝트 값
   */
  void deleteProject(Long projectId);

  /**
   * [프로젝트 목록 조회(전체/기수/타입/검색) 메서드] 프로젝트 리스트 조회
   *
   * @param projectTypeId 조회할 프로젝트 타입 값
   * @param semester 조회할 기수 값
   * @param search 조회할 검색어
   * @param pageNum 조회할 페이지 번호
   * @param pageSize 조회할 페이지 크기
   * @return 조회 응답 DTO
   */
  ProjectPageWrapperResponse<ProjectPageResponse> getProjectByPageAndSemesterAndTypeAndSearch(
      Long projectTypeId, Long semester, String search, Integer pageNum, Integer pageSize);

  /**
   * [프로젝트 단일 조회 메서드] 특정 프로젝트 정보 상세 조회
   *
   * @param projectId 상세 조회할 프로젝트 식별자
   * @return 프로젝트 정보를 담은 DTO
   */
  ProjectDetailResponse getProjectByProjectId(Long projectId);

  /**
   * [ 프로젝트 수상작 무한 스크롤 조회 메서드 ]
   *
   * @param lastCursorId 마지막 커서 위치 프로젝트 식별자
   * @param size 한 번에 받아올 프로젝트 크기
   * @return ProjectAwardResponse 응답 객체
   */
  InfiniteResponse<ProjectAwardResponse> getAwardProjectsByPage(Long lastCursorId, Integer size);
}
