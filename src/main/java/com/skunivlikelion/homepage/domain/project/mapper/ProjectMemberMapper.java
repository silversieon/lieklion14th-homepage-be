/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.project.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.skunivlikelion.homepage.domain.project.dto.response.ProjectMemberResponse;
import com.skunivlikelion.homepage.domain.project.entity.ProjectMember;

@Component
public class ProjectMemberMapper {

  public List<ProjectMemberResponse> toProjectMemberResponseList(
      List<ProjectMember> projectMembers) {
    return projectMembers.stream().map(this::toProjectMemberResponse).toList();
  }

  public ProjectMemberResponse toProjectMemberResponse(ProjectMember projectMember) {
    return ProjectMemberResponse.builder()
        .projectMemberId(projectMember.getId())
        .projectMemberName(projectMember.getProjectMemberName())
        .track(projectMember.getTrack())
        .build();
  }
}
