/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.project.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.skunivlikelion.homepage.domain.project.dto.response.ProjectImageResponse;
import com.skunivlikelion.homepage.domain.project.entity.ProjectImage;

@Component
public class ProjectImageMapper {

  public List<ProjectImageResponse> toProjectImageResponseList(List<ProjectImage> projectImages) {
    return projectImages.stream().map(this::toProjectImageResponse).toList();
  }

  public ProjectImageResponse toProjectImageResponse(ProjectImage projectImage) {
    return ProjectImageResponse.builder()
        .projectImageId(projectImage.getId())
        .imageUrl(projectImage.getImageUrl())
        .build();
  }
}
