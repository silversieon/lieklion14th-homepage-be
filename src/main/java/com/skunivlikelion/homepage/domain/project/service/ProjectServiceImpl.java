/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.project.service;

import java.util.*;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.skunivlikelion.homepage.domain.project.dto.internal.ProjectImagesChangedEvent;
import com.skunivlikelion.homepage.domain.project.dto.internal.ProjectImagesDeletedEvent;
import com.skunivlikelion.homepage.domain.project.dto.internal.ProjectImagesUploadEvent;
import com.skunivlikelion.homepage.domain.project.dto.internal.UploadImagePayload;
import com.skunivlikelion.homepage.domain.project.dto.request.*;
import com.skunivlikelion.homepage.domain.project.dto.response.*;
import com.skunivlikelion.homepage.domain.project.entity.Project;
import com.skunivlikelion.homepage.domain.project.entity.ProjectMember;
import com.skunivlikelion.homepage.domain.project.entity.ProjectType;
import com.skunivlikelion.homepage.domain.project.exception.ProjectErrorCode;
import com.skunivlikelion.homepage.domain.project.mapper.ProjectMapper;
import com.skunivlikelion.homepage.domain.project.repository.ProjectImageRepository;
import com.skunivlikelion.homepage.domain.project.repository.ProjectRepository;
import com.skunivlikelion.homepage.domain.project.repository.ProjectTypeRepository;
import com.skunivlikelion.homepage.domain.semester.entity.Semester;
import com.skunivlikelion.homepage.domain.semester.exception.SemesterErrorCode;
import com.skunivlikelion.homepage.domain.semester.repository.SemesterRepository;
import com.skunivlikelion.homepage.global.annotation.TimeTrace;
import com.skunivlikelion.homepage.global.exception.CustomException;
import com.skunivlikelion.homepage.global.page.mapper.InfiniteMapper;
import com.skunivlikelion.homepage.global.page.mapper.PageMapper;
import com.skunivlikelion.homepage.global.page.response.InfiniteResponse;

import io.jsonwebtoken.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ProjectServiceImpl implements ProjectService {
  private final ProjectRepository projectRepository;
  private final ProjectImageRepository projectImageRepository;
  private final ProjectImageService projectImageService;
  private final SemesterRepository semesterRepository;
  private final ProjectTypeRepository projectTypeRepository;
  private final InfiniteMapper infiniteMapper;
  private final PageMapper pageMapper;
  private final ProjectMapper projectMapper;
  private final ProjectMemberService projectMemberService;
  private final ApplicationEventPublisher eventPublisher;

  @Override
  @TimeTrace(
      methodName = "프로젝트 생성",
      env = {"local", "dev"})
  public ProjectResponse createProject(
      ProjectCreateRequest request, List<MultipartFile> projectImages) {

    ProjectType projectType = getProjectType(request.getProjectTypeId());
    Semester semester = getSemester(request.getSemesterId());

    Project project = projectMapper.projectCreateRequestToEntity(request, projectType, semester);
    Project savedProject = projectRepository.save(project);

    projectMemberService.createProjectMembers(savedProject, request.getProjectMembers());

    List<UploadImagePayload> payloads = multipartToUploadImagePayload(projectImages);
    eventPublisher.publishEvent(new ProjectImagesUploadEvent(savedProject.getId(), payloads));

    log.info(
        "[Project] 프로젝트 생성 완료 - projectId={}, 멤버 수: {}",
        savedProject.getId(),
        savedProject.getProjectMembers().size());
    return projectMapper.toProjectResponse(savedProject);
  }

  @Override
  @TimeTrace(
      methodName = "프로젝트 수정",
      env = {"local", "dev"})
  public ProjectUpdateResponse updateProject(
      Long projectId, ProjectUpdateRequest request, List<MultipartFile> newImages) {

    Project project = getProject(projectId);
    Semester semester = getSemester(request.getSemesterId());
    ProjectType projectType = getProjectType(request.getProjectTypeId());

    projectMemberService.updateProjectMembers(
        project, request.getRemainingProjectMemberIds(), request.getNewProjectMembers());

    List<String> deletedImageUrls =
        projectImageService.preDeleteProjectImages(project, request.getRemainingProjectImageIds());
    project.update(request, semester, projectType);

    List<UploadImagePayload> payloads = multipartToUploadImagePayload(newImages);
    eventPublisher.publishEvent(
        new ProjectImagesChangedEvent(project.getId(), deletedImageUrls, payloads));

    log.info(
        "[Project] 프로젝트 수정 완료 - projectId={}, deletedImages: {}, newImagesCount: {}",
        projectId,
        deletedImageUrls.size(),
        Objects.requireNonNull(payloads).size());
    return projectMapper.toProjectUpdateResponse(project);
  }

  @Override
  @TimeTrace(
      methodName = "프로젝트 페이지 조회",
      env = {"local", "dev"})
  @Transactional(readOnly = true)
  public ProjectPageWrapperResponse<ProjectPageResponse>
      getProjectByPageAndSemesterAndTypeAndSearch(
          Long projectType, Long semester, String search, Integer pageNum, Integer pageSize) {
    Page<Project> projects =
        projectRepository.findProjectsByFilters(
            projectType, semester, search, PageRequest.of(pageNum, pageSize));

    List<Long> allProjectIdsByFilters =
        projectRepository.findProjectIdsByFilters(projectType, semester, search);

    Page<ProjectPageResponse> projectPage = projectMapper.toProjectPageResponse(projects);

    return pageMapper.toProjectPageWrapperResponse(projectPage, allProjectIdsByFilters);
  }

  @Override
  @Transactional(readOnly = true)
  public ProjectDetailResponse getProjectByProjectId(Long id) {

    Project project = getProject(id);

    List<ProjectMember> projectMembers = project.getProjectMembers();
    projectMembers.sort(Comparator.comparingInt(pm -> pm.getTrack().getPriority()));

    return projectMapper.toProjectDetailResponse(project, projectMembers);
  }

  @Override
  @Transactional(readOnly = true)
  public InfiniteResponse<ProjectAwardResponse> getAwardProjectsByPage(
      Long lastCursorId, Integer size) {

    size = (size == null || size <= 0) ? 3 : size;
    Pageable pageable = PageRequest.of(0, size + 1, Sort.by(Direction.DESC, "id"));
    List<Project> projects = projectRepository.findAwardProjects(pageable, lastCursorId);

    boolean hasNext = projects.size() > size;
    if (hasNext) {
      projects = projects.subList(0, size);
    }
    Long lastCursor = projects.isEmpty() ? null : projects.getLast().getId();
    List<ProjectAwardResponse> list = projectMapper.toProjectAwardResponseList(projects);

    return infiniteMapper.toProjectAwardInfiniteResponse(list, lastCursor, hasNext, size);
  }

  @Override
  public void deleteProject(Long projectId) {
    Project project = getProject(projectId);

    List<String> projectImages =
        projectImageRepository.findProjectImageUrlByProject_Id(project.getId());

    projectRepository.delete(project);

    eventPublisher.publishEvent(new ProjectImagesDeletedEvent(projectImages));
    log.info("[Project] 프로젝트 삭제 완료 - projectId={}", projectId);
  }

  private List<UploadImagePayload> multipartToUploadImagePayload(
      List<MultipartFile> multipartFiles) {
    return multipartFiles.stream()
        .map(
            f -> {
              try {
                return new UploadImagePayload(
                    f.getOriginalFilename(), f.getContentType(), f.getBytes());
              } catch (IOException | java.io.IOException e) {
                throw new CustomException(ProjectErrorCode.PROJECT_IMAGE_UPLOAD_FAIL);
              }
            })
        .toList();
  }

  private Project getProject(Long id) {
    return projectRepository
        .findById(id)
        .orElseThrow(
            () -> {
              log.warn("[Project] 프로젝트 없음 - projectId={}", id);
              return new CustomException(ProjectErrorCode.NOT_FOUND_PROJECT);
            });
  }

  private Semester getSemester(Long semesterId) {
    return semesterRepository
        .findById(semesterId)
        .orElseThrow(
            () -> {
              log.warn("[Project] 기수 없음 - semesterId={}", semesterId);
              return new CustomException(SemesterErrorCode.NOT_FOUND_SEMESTER);
            });
  }

  private ProjectType getProjectType(Long projectTypeId) {
    return projectTypeRepository
        .findById(projectTypeId)
        .orElseThrow(
            () -> {
              log.warn("[Project] 프로젝트 타입 없음 - projectTypeId={}", projectTypeId);
              return new CustomException(ProjectErrorCode.NOT_FOUND_PROJECT_TYPE);
            });
  }
}
