/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.user.dto.response;

import com.skunivlikelion.homepage.domain.user.enums.UserRole;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(title = "UserRoleResponse: 사용자 권한 조회 응답 DTO")
public class UserRoleResponse {

  @Schema(description = "사용자 역할")
  private UserRole userRole;
}
