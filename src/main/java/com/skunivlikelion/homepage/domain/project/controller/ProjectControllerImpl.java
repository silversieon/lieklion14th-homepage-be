/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.project.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.skunivlikelion.homepage.domain.project.dto.request.ProjectCreateRequest;
import com.skunivlikelion.homepage.domain.project.dto.request.ProjectTypeRequest;
import com.skunivlikelion.homepage.domain.project.dto.request.ProjectUpdateRequest;
import com.skunivlikelion.homepage.domain.project.dto.response.*;
import com.skunivlikelion.homepage.domain.project.service.ProjectService;
import com.skunivlikelion.homepage.domain.project.service.ProjectTypeService;
import com.skunivlikelion.homepage.global.page.mapper.PageMapper;
import com.skunivlikelion.homepage.global.page.response.InfiniteResponse;
import com.skunivlikelion.homepage.global.page.response.PageResponse;

import backend.boilerplate.response.BaseResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ProjectControllerImpl implements ProjectController {

  private final ProjectService projectService;
  private final ProjectTypeService projectTypeService;
  private final PageMapper pageMapper;

  @Override
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<BaseResponse<ProjectTypeResponse>> createProjectType(
      @Valid @RequestBody ProjectTypeRequest request) {
    ProjectTypeResponse response = projectTypeService.createProjectType(request);
    return ResponseEntity.status(201)
        .body(BaseResponse.success(201, "프로젝트 타입 생성에 성공했습니다.", response));
  }

  @Override
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<BaseResponse<Void>> deleteProjectType(
      @PathVariable("project-type-id") Long projectTypeId) {
    projectTypeService.deleteProjectType(projectTypeId);
    return ResponseEntity.status(200).body(BaseResponse.success(200, "프로젝트 타입 삭제에 성공했습니다.", null));
  }

  @Override
  public ResponseEntity<BaseResponse<List<ProjectTypeResponse>>> getAllProjectTypes() {
    List<ProjectTypeResponse> response = projectTypeService.getAllProjectTypes();
    return ResponseEntity.status(200)
        .body(BaseResponse.success(200, "프로젝트 타입 목록 조회에 성공했습니다.", response));
  }

  @Override
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<BaseResponse<ProjectResponse>> createProject(
      @Valid @RequestPart("request") ProjectCreateRequest request,
      @RequestPart(value = "projectImages", required = false) MultipartFile[] projectImages) {
    List<MultipartFile> images = (projectImages == null) ? List.of() : List.of(projectImages);
    ProjectResponse response = projectService.createProject(request, images);
    return ResponseEntity.status(201).body(BaseResponse.success(201, "프로젝트 생성을 성공했습니다.", response));
  }

  @Override
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<BaseResponse<ProjectUpdateResponse>> updateProject(
      @PathVariable(value = "project-id") Long projectId,
      @Valid @RequestPart("request") ProjectUpdateRequest request,
      @RequestPart(value = "projectImages", required = false) MultipartFile[] newImages) {

    List<MultipartFile> newImageList = (newImages == null) ? List.of() : List.of(newImages);

    ProjectUpdateResponse response = projectService.updateProject(projectId, request, newImageList);

    return ResponseEntity.status(200).body(BaseResponse.success(200, "프로젝트 수정에 성공했습니다.", response));
  }

  @Override
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<BaseResponse<Void>> deleteProject(
      @PathVariable(value = "project-id") Long projectId) {
    projectService.deleteProject(projectId);
    return ResponseEntity.status(200).body(BaseResponse.success(200, "프로젝트 삭제에 성공했습니다.", null));
  }

  @Override
  public ResponseEntity<BaseResponse<PageResponse<ProjectPageResponse>>>
      getProjectByPageAndSemesterAndTypeAndSearch(
          Long projectTypeId, Long semester, String search, Integer page) {
    Page<ProjectPageResponse> projectPage =
        projectService.getProjectByPageAndSemesterAndTypeAndSearch(
            projectTypeId, semester, search, page);
    PageResponse<ProjectPageResponse> response = pageMapper.toPageResponse(projectPage);
    return ResponseEntity.ok(BaseResponse.success(200, "프로젝트 목록 조회에 성공했습니다.", response));
  }

  @Override
  public ResponseEntity<BaseResponse<ProjectDetailResponse>> getProjectByProjectId(Long projectId) {
    ProjectDetailResponse response = projectService.getProjectByProjectId(projectId);
    return ResponseEntity.ok(BaseResponse.success(200, "프로젝트 단일 조회에 성공했습니다.", response));
  }

  @Override
  public ResponseEntity<BaseResponse<InfiniteResponse<ProjectAwardResponse>>> getAwardProjects(
      @RequestParam(value = "last-cursor-id", required = false) Long lastCursorId,
      @RequestParam Integer size) {

    InfiniteResponse<ProjectAwardResponse> result =
        projectService.getAwardProjectsByPage(lastCursorId, size);

    return ResponseEntity.ok(BaseResponse.success(200, "수상작 무한스크롤 조회 성공", result));
  }
}
