/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.auth.mapper;

import org.springframework.stereotype.Component;

import com.skunivlikelion.homepage.domain.auth.dto.response.LoginResponse;
import com.skunivlikelion.homepage.domain.auth.dto.response.PasswordReissueResponse;
import com.skunivlikelion.homepage.domain.auth.dto.response.RefreshResponse;
import com.skunivlikelion.homepage.domain.auth.dto.response.TokenResponse;

@Component
public class AuthMapper {

  public LoginResponse toLoginResponse(String accessToken) {
    return LoginResponse.builder().accessToken(accessToken).build();
  }

  public TokenResponse toTokenResponse(String accessToken, String refreshToken) {
    return TokenResponse.builder().accessToken(accessToken).refreshToken(refreshToken).build();
  }

  public PasswordReissueResponse toPasswordResetResponse(String email, String temporaryPassword) {
    return PasswordReissueResponse.builder()
        .email(email)
        .temporaryPassword(temporaryPassword)
        .build();
  }

  public RefreshResponse toRefreshResponse(String accessToken) {
    return RefreshResponse.builder().accessToken(accessToken).build();
  }
}
