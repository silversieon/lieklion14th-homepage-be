/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.global.page.mapper;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import com.skunivlikelion.homepage.global.page.response.PageResponse;

@Component
public class PageMapper {

  public <T> PageResponse<T> toPageResponse(Page<T> page) {
    return PageResponse.<T>builder()
        .content(page.getContent())
        .totalElements(page.getTotalElements())
        .totalPages(page.getTotalPages())
        .pageNum(page.getNumber())
        .pageSize(page.getSize())
        .last(page.isLast())
        .first(page.isFirst())
        .build();
  }
}
