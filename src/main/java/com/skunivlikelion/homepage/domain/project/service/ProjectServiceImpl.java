/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.project.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.skunivlikelion.homepage.domain.common.enums.Track;
import com.skunivlikelion.homepage.domain.project.dto.request.ProjectCreateRequest;
import com.skunivlikelion.homepage.domain.project.dto.response.ProjectDetailResponse;
import com.skunivlikelion.homepage.domain.project.dto.response.ProjectPostResponse;
import com.skunivlikelion.homepage.domain.project.dto.response.ProjectResponse;
import com.skunivlikelion.homepage.domain.project.dto.response.ProjectUpdateResponse;
import com.skunivlikelion.homepage.domain.project.entity.Project;
import com.skunivlikelion.homepage.domain.project.entity.ProjectImage;
import com.skunivlikelion.homepage.domain.project.entity.ProjectMember;
import com.skunivlikelion.homepage.domain.project.entity.ProjectType;
import com.skunivlikelion.homepage.domain.project.exception.ProjectErrorCode;
import com.skunivlikelion.homepage.domain.project.repository.ProjectImageRepository;
import com.skunivlikelion.homepage.domain.project.repository.ProjectRepository;
import com.skunivlikelion.homepage.domain.project.repository.ProjectTypeRepository;
import com.skunivlikelion.homepage.domain.semester.entity.Semester;
import com.skunivlikelion.homepage.domain.semester.exception.SemesterErrorCode;
import com.skunivlikelion.homepage.domain.semester.repository.SemesterRepository;
import com.skunivlikelion.homepage.domain.semester.service.SemesterService;

import backend.boilerplate.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {
  private final ProjectRepository projectRepository;
  private final ProjectImageRepository projectImageRepository;
  private final ProjectImageService projectImageService;
  private final ProjectImageUpdateService projectImageUpdateService;
  private final SemesterRepository semesterRepository;
  private final SemesterService semesterService;
  private final ProjectTypeRepository projectTypeRepository;

  @Override
  public ProjectPostResponse createProject(
      ProjectCreateRequest request, List<MultipartFile> projectImages) {
    validateCreateRequest(request, projectImages);
    validateMembers(request.getMembers());

    Semester semester = getSemester(request.getSemesterId());
    ProjectType projectType = getProjectType(request.getProjectTypeId());
    validateDuplicateForCreate(request.getTitle(), request.getSemesterId(), projectType);

    Project project = Project.builder().build();
    project.update(request, semester, projectType);

    Project savedProject = projectRepository.save(project);

    List<ProjectImage> savedImages =
        projectImageService.uploadProjectImages(projectImages, savedProject);

    if (savedImages == null || savedImages.isEmpty()) {
      log.warn("[Project] 프로젝트 생성 실패 - 이미지 저장 실패 projectId={}", savedProject.getId());
      throw new CustomException(ProjectErrorCode.PROJECT_IMAGE_UPLOAD_FAIL);
    }

    log.info("[Project] 프로젝트 생성 완료 - projectId={}", savedProject.getId());

    return ProjectPostResponse.builder()
        .id(String.valueOf(savedProject.getId()))
        .imageCount(savedImages.size())
        .build();
  }

  @Override
  public ProjectUpdateResponse updateProject(
      Long id,
      ProjectCreateRequest request,
      List<String> remainingImageUrls,
      List<MultipartFile> newImages) {

    Project project = getProject(id);

    validateUpdateRequest(request);
    validateMembers(request.getMembers());

    Semester semester = getSemester(request.getSemesterId());
    ProjectType projectType = getProjectType(request.getProjectTypeId());
    validateDuplicateForUpdate(request.getTitle(), request.getSemesterId(), projectType, id);

    ProjectImageUpdateService.ImageUpdateResult imageResult =
        projectImageUpdateService.updateProjectImages(project, remainingImageUrls, newImages);

    project.update(request, semester, projectType);

    log.info("[Project] 프로젝트 수정 완료 - projectId={}", id);

    return ProjectUpdateResponse.builder()
        .id(project.getId())
        .title(project.getTitle())
        .semester(project.getSemester())
        .award(project.isAward())
        .projectType(project.getProjectType())
        .content(project.getContent())
        .members(toMembersMap(project))
        .thumbnailUrl(imageResult.getThumbnailUrl())
        .newImagesCount(imageResult.getNewImagesCount())
        .deletedImagesCount(imageResult.getDeletedCount())
        .build();
  }

  @Override
  @Transactional(readOnly = true)
  public Page<ProjectResponse> getProjectByPageAndSemesterAndTypeAndSearch(
      Long projectType, Long semester, String search, Integer page) {
    Page<Project> projects =
        projectRepository.findProjectsByFilters(
            projectType, semester, search, PageRequest.of(page, 6));

    if (semester != null) {
      semesterService.getSemester(semester);
    }
    return projects.map(
        project -> {
          List<ProjectImage> images = projectImageRepository.findImagesByProjectId(project.getId());
          String thumbnailUrl =
              images.stream().findFirst().map(ProjectImage::getImageUrl).orElse(null);

          return toProjectResponse(project, thumbnailUrl);
        });
  }

  @Override
  @Transactional(readOnly = true)
  public ProjectDetailResponse getProjectByProjectId(Long id) {

    Project project = getProject(id);

    List<String> imageUrls =
        projectImageRepository.findImagesByProjectId(project.getId()).stream()
            .map(ProjectImage::getImageUrl)
            .toList();

    return ProjectDetailResponse.builder()
        .id(project.getId())
        .title(project.getTitle())
        .semester(project.getSemester())
        .award(project.isAward())
        .projectType(project.getProjectType())
        .content(project.getContent())
        .members(toMembersMap(project))
        .imageUrls(imageUrls)
        .build();
  }

  @Override
  public void deleteProject(Long id) {

    Project project = getProject(id);

    projectImageRepository
        .findImagesByProjectId(project.getId())
        .forEach(image -> projectImageService.deleteProjectImageByUrl(image.getImageUrl()));

    projectRepository.delete(project);

    log.info("[Project] 프로젝트 삭제 완료 - projectId={}", id);
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

  private void validateCreateRequest(ProjectCreateRequest request, List<MultipartFile> images) {
    if (request == null
        || request.getTitle() == null
        || request.getTitle().isBlank()
        || request.getContent() == null
        || request.getContent().isBlank()
        || request.getSemesterId() == null
        || request.getProjectTypeId() == null
        || images == null
        || images.isEmpty()) {

      log.info("[Project] 프로젝트 생성 요청값 검증 실패");
      throw new CustomException(ProjectErrorCode.INVALID_PROJECT_REQUEST);
    }
  }

  private void validateUpdateRequest(ProjectCreateRequest request) {
    if (request == null
        || request.getTitle() == null
        || request.getTitle().isBlank()
        || request.getContent() == null
        || request.getContent().isBlank()
        || request.getSemesterId() == null
        || request.getProjectTypeId() == null) {

      log.info("[Project] 프로젝트 수정 요청값 검증 실패");
      throw new CustomException(ProjectErrorCode.INVALID_PROJECT_REQUEST);
    }
  }

  private void validateMembers(Map<Track, List<String>> members) {
    if (members == null
        || members.values().stream()
            .flatMap(List::stream)
            .noneMatch(name -> name != null && !name.isBlank())) {

      log.info("[Project] 멤버 검증 실패");
      throw new CustomException(ProjectErrorCode.INVALID_PROJECT_REQUEST);
    }
  }

  private void validateDuplicateForCreate(String title, Long semesterId, ProjectType projectType) {

    if (projectRepository.existsByTitleAndSemester_SemesterAndProjectType(
        title, semesterId, projectType)) {

      log.info("[Project] 중복 프로젝트 생성 시도 - title={}", title);
      throw new CustomException(ProjectErrorCode.ALREADY_EXIST_PROJECT);
    }
  }

  private void validateDuplicateForUpdate(
      String title, Long semesterId, ProjectType projectType, Long id) {

    if (projectRepository.existsByTitleAndSemester_SemesterAndProjectTypeAndIdNot(
        title, semesterId, projectType, id)) {

      log.info("[Project] 중복 프로젝트 수정 시도 - projectId={}", id);
      throw new CustomException(ProjectErrorCode.ALREADY_EXIST_PROJECT);
    }
  }

  private ProjectResponse toProjectResponse(Project project, String thumbnailUrl) {
    return ProjectResponse.builder()
        .id(project.getId())
        .title(project.getTitle())
        .semester(project.getSemester())
        .award(project.isAward())
        .projectType(project.getProjectType())
        .content(project.getContent())
        .members(toMembersMap(project))
        .thumbnailUrl(thumbnailUrl)
        .build();
  }

  private Map<Track, List<String>> toMembersMap(Project project) {
    return project.getMembers().stream()
        .filter(m -> m.getTrack() != null && m.getName() != null && !m.getName().isBlank())
        .collect(
            Collectors.groupingBy(
                ProjectMember::getTrack,
                LinkedHashMap::new,
                Collectors.mapping(ProjectMember::getName, Collectors.toList())));
  }

  @Override
  @Transactional(readOnly = true)
  public Page<ProjectResponse> getAwardProjectsByPage(Integer page, Integer size) {

    int pageNumber = (page == null || page < 0) ? 0 : page;
    int pageSize = (size == null || size <= 0) ? 6 : size;

    Page<Project> projects =
        projectRepository.findAwardProjects(PageRequest.of(pageNumber, pageSize));

    Page<ProjectResponse> result =
        projects.map(
            project -> {
              List<ProjectImage> images =
                  projectImageRepository.findImagesByProjectId(project.getId());
              String thumbnailUrl =
                  images.stream().findFirst().map(ProjectImage::getImageUrl).orElse(null);

              return toProjectResponse(project, thumbnailUrl);
            });

    log.info(
        "[Project] 수상작 무한스크롤 조회 완료 - page={}, size={}, totalElements={}",
        pageNumber,
        pageSize,
        projects.getTotalElements());

    return result;
  }
}
