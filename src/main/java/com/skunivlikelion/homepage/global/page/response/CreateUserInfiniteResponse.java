/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.global.page.response;

import java.util.List;

import com.skunivlikelion.homepage.domain.user.entity.User;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CreateUserInfiniteResponse {

  List<User> users;

  boolean hasNext;

  Long lastCursor;
}
