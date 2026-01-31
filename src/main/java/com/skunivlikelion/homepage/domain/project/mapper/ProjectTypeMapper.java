/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.project.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.skunivlikelion.homepage.domain.project.dto.request.ProjectTypeRequest;
import com.skunivlikelion.homepage.domain.project.dto.response.ProjectTypeResponse;
import com.skunivlikelion.homepage.domain.project.entity.ProjectType;

@Component
public class ProjectTypeMapper {

  public ProjectType toEntity(ProjectTypeRequest request) {
    return ProjectType.builder().projectTypeName(request.getProjectTypeName()).build();
  }

  public ProjectTypeResponse toResponse(ProjectType projectType) {
    return ProjectTypeResponse.builder()
        .projectTypeId(projectType.getId())
        .projectTypeName(projectType.getProjectTypeName())
        .build();
  }

  public List<ProjectTypeResponse> toResponseList(List<ProjectType> projectTypes) {
    return projectTypes.stream().map(this::toResponse).toList();
  }
}
