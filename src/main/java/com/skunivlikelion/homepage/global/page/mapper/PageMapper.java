/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.global.page.mapper;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import com.skunivlikelion.homepage.domain.project.dto.response.ProjectPageWrapperResponse;
import com.skunivlikelion.homepage.global.page.response.PageResponse;

@Component
public class PageMapper {

  private <T> PageResponse<T> toPageResponse(Page<T> page) {
    return PageResponse.<T>builder()
        .content(page.getContent())
        .totalElements(page.getTotalElements())
        .totalPages(page.getTotalPages())
        .pageNum(page.getNumber() + 1)
        .pageSize(page.getSize())
        .last(page.isLast())
        .first(page.isFirst())
        .build();
  }

  public <T> ProjectPageWrapperResponse<T> toProjectPageWrapperResponse(
      Page<T> projectPage, List<Long> allProjectIdsByFilters) {
    return ProjectPageWrapperResponse.<T>builder()
        .projectPageResponse(toPageResponse(projectPage))
        .allProjectIdsByFilters(allProjectIdsByFilters)
        .build();
  }
}
