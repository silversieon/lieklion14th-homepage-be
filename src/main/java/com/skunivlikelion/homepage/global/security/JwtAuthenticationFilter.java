/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.global.security;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import com.skunivlikelion.homepage.domain.auth.exception.AuthErrorCode;
import com.skunivlikelion.homepage.global.security.jwt.JwtProvider;
import com.skunivlikelion.homepage.global.security.jwt.TokenType;

import backend.boilerplate.response.BaseResponse;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtProvider jwtProvider;
  private final UserDetailsService userDetailsService;
  private static final AntPathMatcher pathMatcher = new AntPathMatcher();
  private final ObjectMapper objectMapper;

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    String uri = request.getRequestURI();
    return pathMatcher.match("/api/**/auth/refresh", uri)
        || pathMatcher.match("/api/**/auth/login", uri)
        || pathMatcher.match("/api/**/auth/register", uri);
  }

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
      filterChain.doFilter(request, response);
    } catch (ExpiredJwtException e) {
      SecurityContextHolder.clearContext();
      log.info("[Auth] Security: 만료된 JWT 액세스 토큰 입력 확인, 리프레시 필요");
      writeAuthErrorResponse(response, AuthErrorCode.EXPIRED_ACCESS_TOKEN);
    } catch (JwtException | IllegalArgumentException | UsernameNotFoundException e) {
      SecurityContextHolder.clearContext();
      log.warn("[Auth] Security: 유효하지 않은 JWT 토큰 입력 확인, 주의 필요 - {}", e.getClass().getSimpleName());
      writeAuthErrorResponse(response, AuthErrorCode.UNAUTHORIZED_TOKEN);
    }
  }

  private void writeAuthErrorResponse(HttpServletResponse response, AuthErrorCode errorCode)
      throws IOException {
    if (response.isCommitted()) return;

    response.setStatus(errorCode.getStatus().value());
    response.setCharacterEncoding("UTF-8");
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);

    BaseResponse<Object> baseResponse =
        BaseResponse.error(errorCode.getStatus().value(), errorCode.getMessage());
    response.getWriter().write(objectMapper.writeValueAsString(baseResponse));
    response.getWriter().flush();
  }
}
