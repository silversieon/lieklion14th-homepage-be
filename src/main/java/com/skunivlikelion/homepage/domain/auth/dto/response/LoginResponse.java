/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.auth.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(title = "LoginResponse: 로그인 성공 응답 DTO")
public class LoginResponse {

  @Schema(description = "JWT 액세스 토큰")
  private String accessToken;
}
