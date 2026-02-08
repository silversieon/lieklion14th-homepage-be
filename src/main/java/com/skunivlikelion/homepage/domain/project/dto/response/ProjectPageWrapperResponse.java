/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.project.dto.response;

import java.util.List;

import com.skunivlikelion.homepage.global.page.response.PageResponse;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(title = "ProjectPageWrapperResponse: 프로젝트 모아보기 페이지의 전체 응답 DTO")
public class ProjectPageWrapperResponse<T> {

  PageResponse<T> projectPageResponse;

  List<Long> allProjectIdsByFilters;
}
