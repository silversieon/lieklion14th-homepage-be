/* 
 * Copyright (c) SKU LIKELION 
 */
package com.likelionskuniv.website.domain.auth.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TokenResponse {

  @Schema(description = "JWT 액세스 토큰")
  private String accessToken;

  @Schema(description = "JWT 리프레시 토큰")
  private String refreshToken;
}
