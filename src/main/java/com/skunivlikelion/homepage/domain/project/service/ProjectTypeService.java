/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.project.service;

import java.util.List;

import com.skunivlikelion.homepage.domain.project.dto.request.ProjectTypeRequest;
import com.skunivlikelion.homepage.domain.project.dto.response.ProjectTypeResponse;

public interface ProjectTypeService {

  /**
   * [프로젝트 타입 생성 메서드] 성공 여부에 따라 ProjectTypeResponse 객체에 성공 여부를 담아 반환
   *
   * @param request 프로젝트 타입 생성 요청을 위한 프로젝트 타입 정보를 담은 요청 객체
   * @return 프로젝트 타입 정보를 담은 ProjectTypeResponse
   */
  ProjectTypeResponse createProjectType(ProjectTypeRequest request);

  /**
   * [프로젝트 타입 삭제 메서드] 전달받은 프로젝트 타입 값에 해당하는 프로젝트 타입 삭제
   *
   * @param projectTypeId 삭제할 프로젝트 타입 값
   */
  void deleteProjectType(Long projectTypeId);

  /**
   * [ 전체 프로젝트 타입 조회 메서드 ] 모든 프로젝트 타입을 조회하여 오름차순 목록으로 반환
   *
   * @return 등록된 모든 프로젝트 타입 정보를 담은 DTO 리스트
   */
  List<ProjectTypeResponse> getAllProjectTypes();

  /**
   * [프로젝트 타입 존재 여부 확인 메서드] 해당 프로젝트 타입 존재 여부를 반환
   *
   * @param projectTypeId 확인할 프로젝트 타입 값
   */
  void checkProjectType(Long projectTypeId);
}
