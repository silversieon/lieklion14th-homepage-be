/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.project.service;

import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.skunivlikelion.homepage.domain.project.dto.request.ProjectTypeRequest;
import com.skunivlikelion.homepage.domain.project.dto.response.ProjectTypeResponse;
import com.skunivlikelion.homepage.domain.project.entity.ProjectType;
import com.skunivlikelion.homepage.domain.project.exception.ProjectErrorCode;
import com.skunivlikelion.homepage.domain.project.mapper.ProjectTypeMapper;
import com.skunivlikelion.homepage.domain.project.repository.ProjectTypeRepository;

import backend.boilerplate.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ProjectTypeServiceImpl implements ProjectTypeService {

  private final ProjectTypeRepository projectTypeRepository;
  private final ProjectTypeMapper projectTypeMapper;

  @Override
  public ProjectTypeResponse createProjectType(ProjectTypeRequest request) {
    String projectTypeName = request.getProjectTypeName();

    if (projectTypeRepository.existsByProjectTypeName(projectTypeName)) {
      log.warn("[ProjectType] 프로젝트 타입 생성 실패. 이미 존재하는 프로젝트타입 - projectType={}", projectTypeName);
      throw new CustomException(ProjectErrorCode.ALREADY_EXIST_PROJECT_TYPE);
    }
    try {
      ProjectType projectType = projectTypeMapper.toEntity(request);
      ProjectType saved = projectTypeRepository.save(projectType);
      return projectTypeMapper.toResponse(saved);
    } catch (DataAccessException e) {
      log.error("[ProjectType] 프로젝트 타입 생성 실패(DB) - projectType={}", projectTypeName, e);
      throw new CustomException(ProjectErrorCode.INVALID_PROJECT_TYPE_REQUEST);
    }
  }

  @Override
  public void deleteProjectType(Long projectTypeId) {

    if (!projectTypeRepository.existsById(projectTypeId)) {
      log.warn("[ProjectType] 프로젝트 타입 삭제 실패. 존재하지 않은 프로젝트 타입 - projectType={}", projectTypeId);
      throw new CustomException(ProjectErrorCode.NOT_FOUND_PROJECT_TYPE);
    }
    try {
      int deleted = projectTypeRepository.deleteByProjectTypeIdNative(projectTypeId);
      log.info("[ProjectType] 프로젝트 타입 삭제 완료 - projectType={}, deleted={}", projectTypeId, deleted);
    } catch (DataIntegrityViolationException e) {
      log.info("[ProjectType] 프로젝트 타입 삭제 실패: 프로젝트에서 참조 중(FK) - projectType={}", projectTypeId);
      throw new CustomException(ProjectErrorCode.CANNOT_DELETE_PROJECT_TYPE_WITH_PROJECTS);
    }
  }

  @Override
  @Transactional(readOnly = true)
  public List<ProjectTypeResponse> getAllProjectTypes() {
    List<ProjectType> projectTypes = projectTypeRepository.findAllByOrderByIdAsc();
    List<ProjectTypeResponse> result = projectTypeMapper.toResponseList(projectTypes);

    log.info("[ProjectType] 전체 프로젝트 타입 조회 완료 - count={}", result.size());
    return result;
  }

  @Override
  @Transactional(readOnly = true)
  public void checkProjectType(Long projectTypeId) {
    if (!projectTypeRepository.existsById(projectTypeId)) {
      log.warn("[ProjectType] 해당 프로젝트 타입이 존재하지 않음 - projectType={}", projectTypeId);
      throw new CustomException(ProjectErrorCode.NOT_FOUND_PROJECT_TYPE);
    }
  }
}
