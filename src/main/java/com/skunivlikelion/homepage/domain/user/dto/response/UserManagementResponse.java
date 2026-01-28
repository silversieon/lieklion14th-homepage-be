/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.user.dto.response;

import com.skunivlikelion.homepage.global.page.response.InfiniteResponse;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(title = "UserManagementResponse: 사용자 관리 페이지 사용자 목록 조회 응답 DTO")
public class UserManagementResponse {

  @Schema(description = "게스트 여부", example = "false")
  private boolean guest;

  @Schema(description = "사용자 정보 목록")
  private InfiniteResponse<UserInformationResponse> userInformationList;
}
