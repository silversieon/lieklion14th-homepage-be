/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.project.mapper;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import com.skunivlikelion.homepage.domain.project.dto.request.ProjectCreateRequest;
import com.skunivlikelion.homepage.domain.project.dto.response.*;
import com.skunivlikelion.homepage.domain.project.entity.Project;
import com.skunivlikelion.homepage.domain.project.entity.ProjectMember;
import com.skunivlikelion.homepage.domain.project.entity.ProjectType;
import com.skunivlikelion.homepage.domain.semester.entity.Semester;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProjectMapper {
  private final ProjectImageMapper projectImageMapper;
  private final ProjectMemberMapper projectMemberMapper;

  public Project projectCreateRequestToEntity(
      ProjectCreateRequest request, ProjectType projectType, Semester semester) {
    return Project.builder()
        .title(request.getTitle())
        .content(request.getContent())
        .award(request.getAward())
        .projectType(projectType)
        .semester(semester)
        .build();
  }

  public ProjectResponse toProjectResponse(Project project) {
    return ProjectResponse.builder()
        .projectId(project.getId())
        .title(project.getTitle())
        .award(project.isAward())
        .semester(project.getSemester().getSemester())
        .projectTypeName(project.getProjectType().getProjectTypeName())
        .content(project.getContent())
        .projectMembers(
            projectMemberMapper.toProjectMemberResponseList(project.getProjectMembers()))
        .thumbnailUrl(null)
        .build();
  }

  public ProjectUpdateResponse toProjectUpdateResponse(Project project) {
    return ProjectUpdateResponse.builder()
        .id(project.getId())
        .title(project.getTitle())
        .semester(project.getSemester().getSemester())
        .award(project.isAward())
        .projectTypeName(project.getProjectType().getProjectTypeName())
        .content(project.getContent())
        .projectMembers(
            projectMemberMapper.toProjectMemberResponseList(project.getProjectMembers()))
        .thumbnailUrl(null)
        .projectImageResponses(null)
        .build();
  }

  public Page<ProjectPageResponse> toProjectPageResponse(Page<Project> projects) {
    return projects.map(
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
  }

  public ProjectDetailResponse toProjectDetailResponse(
      Project project, List<ProjectMember> sortedProjectMembers) {
    return ProjectDetailResponse.builder()
        .id(project.getId())
        .title(project.getTitle())
        .semester(project.getSemester().getSemester())
        .award(project.isAward())
        .projectTypeName(project.getProjectType().getProjectTypeName())
        .content(project.getContent())
        .projectMembers(projectMemberMapper.toProjectMemberResponseList(sortedProjectMembers))
        .projectImageResponses(
            projectImageMapper.toProjectImageResponseList(project.getProjectImages()))
        .build();
  }

  public List<ProjectAwardResponse> toProjectAwardResponseList(List<Project> projects) {
    return projects.stream().map(this::toProjectAwardResponse).toList();
  }

  public ProjectAwardResponse toProjectAwardResponse(Project project) {
    return ProjectAwardResponse.builder()
        .projectId(project.getId())
        .thumbnailUrl(project.getProjectImages().getFirst().getImageUrl())
        .build();
  }
}
