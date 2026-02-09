/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.project.service;

import java.util.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.skunivlikelion.homepage.domain.common.enums.Track;
import com.skunivlikelion.homepage.domain.project.dto.request.ProjectCreateRequest;
import com.skunivlikelion.homepage.domain.project.dto.request.ProjectUpdateRequest;
import com.skunivlikelion.homepage.domain.project.dto.response.*;
import com.skunivlikelion.homepage.domain.project.entity.Project;
import com.skunivlikelion.homepage.domain.project.entity.ProjectImage;
import com.skunivlikelion.homepage.domain.project.entity.ProjectMember;
import com.skunivlikelion.homepage.domain.project.entity.ProjectType;
import com.skunivlikelion.homepage.domain.project.exception.ProjectErrorCode;
import com.skunivlikelion.homepage.domain.project.repository.ProjectImageRepository;
import com.skunivlikelion.homepage.domain.project.repository.ProjectMemberRepository;
import com.skunivlikelion.homepage.domain.project.repository.ProjectRepository;
import com.skunivlikelion.homepage.domain.project.repository.ProjectTypeRepository;
import com.skunivlikelion.homepage.domain.semester.entity.Semester;
import com.skunivlikelion.homepage.domain.semester.exception.SemesterErrorCode;
import com.skunivlikelion.homepage.domain.semester.repository.SemesterRepository;
import com.skunivlikelion.homepage.domain.semester.service.SemesterService;
import com.skunivlikelion.homepage.global.exception.CustomException;
import com.skunivlikelion.homepage.global.page.mapper.InfiniteMapper;
import com.skunivlikelion.homepage.global.page.mapper.PageMapper;
import com.skunivlikelion.homepage.global.page.response.InfiniteResponse;

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
  private final ProjectUpdateService projectUpdateService;
  private final SemesterRepository semesterRepository;
  private final SemesterService semesterService;
  private final ProjectTypeRepository projectTypeRepository;
  private final InfiniteMapper infiniteMapper;
  private final ProjectMemberRepository projectMemberRepository;
  private final PageMapper pageMapper;

  @Override
  public ProjectResponse createProject(
      ProjectCreateRequest request, List<MultipartFile> projectImages) {
    validateCreateRequest(request, projectImages);
    validateMembers(request.getProjectMembers());

    Semester semester = getSemester(request.getSemesterId());
    ProjectType projectType = getProjectType(request.getProjectTypeId());

    Project project =
        Project.builder()
            .title(request.getTitle())
            .content(request.getContent())
            .award(request.isAward())
            .projectType(projectType)
            .semester(semester)
            .build();

    Project savedProject = projectRepository.save(project);

    Map<Track, List<String>> projectMemberMap = request.getProjectMembers();
    Set<Track> tracks = request.getProjectMembers().keySet();
    for (Track track : tracks) {
      List<String> projectMemberNames = projectMemberMap.get(track);
      for (String name : projectMemberNames) {
        ProjectMember projectMember =
            ProjectMember.builder()
                .track(track)
                .projectMemberName(name)
                .project(savedProject)
                .build();
        ProjectMember savedProjectMember = projectMemberRepository.save(projectMember);
        savedProject.addProjectMember(savedProjectMember);
      }
    }

    List<ProjectImage> savedImages =
        projectImageService.uploadProjectImages(projectImages, savedProject);

    if (savedImages == null || savedImages.isEmpty()) {
      log.warn("[Project] 프로젝트 생성 실패 - 이미지 저장 실패 projectId={}", savedProject.getId());
      throw new CustomException(ProjectErrorCode.PROJECT_IMAGE_UPLOAD_FAIL);
    }

    log.info(
        "[Project] 프로젝트 생성 완료 - projectId={}, 이미지 수: {}, 멤버 수: {}",
        savedProject.getId(),
        savedImages.size(),
        savedProject.getProjectMembers().size());

    return ProjectResponse.builder()
        .projectId(savedProject.getId())
        .title(savedProject.getTitle())
        .award(savedProject.isAward())
        .semester(savedProject.getSemester().getSemester())
        .projectTypeName(savedProject.getProjectType().getProjectTypeName())
        .content(savedProject.getContent())
        .projectMembers(
            savedProject.getProjectMembers().stream()
                .map(
                    projectMember ->
                        ProjectMemberResponse.builder()
                            .projectMemberId(projectMember.getId())
                            .projectMemberName(projectMember.getProjectMemberName())
                            .track(projectMember.getTrack())
                            .build())
                .toList())
        .thumbnailUrl(savedProject.getProjectImages().getFirst().getImageUrl())
        .build();
  }

  @Override
  public ProjectUpdateResponse updateProject(
      Long id, ProjectUpdateRequest request, List<MultipartFile> newImages) {

    Project project = getProject(id);

    validateUpdateRequest(request);

    Semester semester = getSemester(request.getSemesterId());
    ProjectType projectType = getProjectType(request.getProjectTypeId());

    List<ProjectImageResponse> projectImageResponses =
        projectUpdateService.updateProjectImages(
            project, request.getRemainingProjectImageIds(), newImages);

    List<ProjectMember> projectMembers =
        projectUpdateService.updateProjectMembers(
            project, request.getRemainingProjectMemberIds(), request.getNewProjectMembers());

    project.update(request, semester, projectType);

    log.info("[Project] 프로젝트 수정 완료 - projectId={}", id);

    return ProjectUpdateResponse.builder()
        .id(project.getId())
        .title(project.getTitle())
        .semester(project.getSemester().getSemester())
        .award(project.isAward())
        .projectTypeName(project.getProjectType().getProjectTypeName())
        .content(project.getContent())
        .projectMembers(
            projectMembers.stream()
                .map(
                    projectMember ->
                        ProjectMemberResponse.builder()
                            .projectMemberId(projectMember.getId())
                            .projectMemberName(projectMember.getProjectMemberName())
                            .track(projectMember.getTrack())
                            .build())
                .toList())
        .thumbnailUrl(projectImageResponses.getFirst().getImageUrl())
        .projectImageResponses(projectImageResponses)
        .build();
  }

  @Override
  @Transactional(readOnly = true)
  public ProjectPageWrapperResponse<ProjectPageResponse>
      getProjectByPageAndSemesterAndTypeAndSearch(
          Long projectType, Long semester, String search, Integer pageNum, Integer pageSize) {
    Page<Project> projects =
        projectRepository.findProjectsByFilters(
            projectType, semester, search, PageRequest.of(pageNum, pageSize));

    List<Long> allProjectIdsByFilters =
        projectRepository.findProjectIdsByFilters(projectType, semester, search);

    Page<ProjectPageResponse> projectPage =
        projects.map(
            project ->
                ProjectPageResponse.builder()
                    .projectId(project.getId())
                    .title(project.getTitle())
                    .award(project.isAward())
                    .semester(project.getSemester().getSemester())
                    .projectTypeName(project.getProjectType().getProjectTypeName())
                    .content(project.getContent())
                    .thumbnailUrl(project.getProjectImages().getFirst().getImageUrl())
                    .build());

    return pageMapper.toProjectPageWrapperResponse(projectPage, allProjectIdsByFilters);
  }

  @Override
  @Transactional(readOnly = true)
  public ProjectDetailResponse getProjectByProjectId(Long id) {

    Project project = getProject(id);

    List<ProjectImageResponse> projectImageResponses =
        projectImageRepository.findImagesByProjectId(id).stream()
            .map(
                projectImage ->
                    ProjectImageResponse.builder()
                        .projectImageId(projectImage.getId())
                        .imageUrl(projectImage.getImageUrl())
                        .build())
            .toList();

    List<Track> tracksByPriority = Track.getTracksByPriority();
    List<ProjectMember> projectMembers = project.getProjectMembers();

    Map<Track, Integer> trackOrderMap = new HashMap<>();
    for (int i = 0; i < tracksByPriority.size(); i++) {
      trackOrderMap.put(tracksByPriority.get(i), i);
    }
    projectMembers.sort(Comparator.comparingInt(pm -> trackOrderMap.get(pm.getTrack())));

    return ProjectDetailResponse.builder()
        .id(project.getId())
        .title(project.getTitle())
        .semester(project.getSemester().getSemester())
        .award(project.isAward())
        .projectTypeName(project.getProjectType().getProjectTypeName())
        .content(project.getContent())
        .projectMembers(
            project.getProjectMembers().stream()
                .map(
                    projectMember ->
                        ProjectMemberResponse.builder()
                            .projectMemberId(projectMember.getId())
                            .projectMemberName(projectMember.getProjectMemberName())
                            .track(projectMember.getTrack())
                            .build())
                .toList())
        .projectImageResponses(projectImageResponses)
        .build();
  }

  @Override
  public void deleteProject(Long projectId) {

    Project project = getProject(projectId);

    List<ProjectImage> projectImages =
        projectImageRepository.findImagesByProjectId(project.getId());

    projectImages.forEach(
        image -> projectImageService.deleteProjectImageByUrl(image.getImageUrl()));

    projectImageRepository.deleteAll(projectImages);
    projectMemberRepository.deleteAll(project.getProjectMembers());
    projectRepository.delete(project);

    log.info("[Project] 프로젝트 삭제 완료 - projectId={}", projectId);
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

  private void validateUpdateRequest(ProjectUpdateRequest request) {
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
    List<ProjectAwardResponse> list =
        projects.stream()
            .map(
                project ->
                    ProjectAwardResponse.builder()
                        .projectId(project.getId())
                        .thumbnailUrl(project.getProjectImages().getFirst().getImageUrl())
                        .build())
            .toList();

    return infiniteMapper.toProjectAwardInfiniteResponse(list, lastCursor, hasNext, size);
  }
}
