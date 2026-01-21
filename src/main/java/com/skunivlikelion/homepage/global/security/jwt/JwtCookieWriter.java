/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.global.security.jwt;

import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import com.skunivlikelion.homepage.global.config.property.JwtProperties;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtCookieWriter {

  private final JwtProperties jwtProperties;

  public ResponseCookie addAccessTokenToCookie(String accessToken) {
    return ResponseCookie.from(TokenType.ACCESS_TOKEN.name(), accessToken)
        .httpOnly(true)
        .secure(true)
        .path("/api")
        .maxAge(jwtProperties.getAccessTokenValidityInSeconds())
        .build();
  }

  public ResponseCookie addRefreshTokenToCookie(String refreshToken) {
    return ResponseCookie.from(TokenType.REFRESH_TOKEN.name(), refreshToken)
        .httpOnly(true)
        .secure(true)
        .path("/api")
        .maxAge(jwtProperties.getRefreshTokenValidityInSeconds())
        .build();
  }
}
