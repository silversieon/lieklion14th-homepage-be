/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.global.page.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.skunivlikelion.homepage.domain.project.dto.response.ProjectAwardResponse;
import com.skunivlikelion.homepage.domain.user.dto.response.UserInformationResponse;
import com.skunivlikelion.homepage.domain.user.entity.User;
import com.skunivlikelion.homepage.global.page.response.CreateUserInfiniteResponse;
import com.skunivlikelion.homepage.global.page.response.InfiniteResponse;

@Component
public class InfiniteMapper {

  public <T> InfiniteResponse<T> toInfiniteResponse(
      List<T> content, Long lastCursor, boolean hasNext, int size) {
    return InfiniteResponse.<T>builder()
        .content(content)
        .lastCursor(lastCursor)
        .hasNext(hasNext)
        .size(size)
        .build();
  }

  public CreateUserInfiniteResponse toCreateUserInfiniteResponse(List<User> users, Integer size) {
    boolean hasNext = users.size() > size;
    if (hasNext) {
      users = users.subList(0, size);
    }
    Long lastCursor = users.isEmpty() ? null : users.getLast().getId();
    return CreateUserInfiniteResponse.builder()
        .users(users)
        .hasNext(hasNext)
        .lastCursor(lastCursor)
        .build();
  }

  public InfiniteResponse<UserInformationResponse> toUserInformationInfiniteResponse(
      List<UserInformationResponse> content, Long lastCursor, boolean hasNext, int size) {
    return toInfiniteResponse(content, lastCursor, hasNext, size);
  }

  public InfiniteResponse<ProjectAwardResponse> toProjectAwardInfiniteResponse(
      List<ProjectAwardResponse> content, Long lastCursor, boolean hasNext, int size) {
    return toInfiniteResponse(content, lastCursor, hasNext, size);
  }
}
