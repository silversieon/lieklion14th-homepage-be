/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.global.security;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.skunivlikelion.homepage.domain.auth.exception.AuthErrorCode;
import com.skunivlikelion.homepage.global.security.jwt.JwtProvider;
import com.skunivlikelion.homepage.global.security.jwt.TokenType;

import backend.boilerplate.exception.CustomException;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtProvider jwtProvider;
  private final UserDetailsService userDetailsService;

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    if ("/error".equals(request.getRequestURI())) {
      filterChain.doFilter(request, response);
      return;
    }

    try {
      String token = jwtProvider.extractAccessToken(request);

      if (token != null
          && jwtProvider.validateToken(token)
          && jwtProvider.validateTokenType(token, TokenType.ACCESS_TOKEN)) {
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
          String email = jwtProvider.getEmailFromToken(token);
          UserDetails userDetails = userDetailsService.loadUserByUsername(email);

          UsernamePasswordAuthenticationToken authentication =
              new UsernamePasswordAuthenticationToken(
                  userDetails, null, userDetails.getAuthorities());
          SecurityContextHolder.getContext().setAuthentication(authentication);
        }
      }
    } catch (JwtException | IllegalArgumentException e) {
      SecurityContextHolder.clearContext();
      log.warn("[Auth] Security: 유효하지 않은 JWT 입력 확인");
      throw new CustomException(AuthErrorCode.UNAUTHORIZED_JWT);
    }
    filterChain.doFilter(request, response);
  }
}
