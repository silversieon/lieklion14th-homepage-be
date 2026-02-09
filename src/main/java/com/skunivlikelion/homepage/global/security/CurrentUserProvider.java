/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.global.security;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.skunivlikelion.homepage.domain.auth.exception.AuthErrorCode;
import com.skunivlikelion.homepage.domain.user.entity.User;
import com.skunivlikelion.homepage.domain.user.repository.UserRepository;
import com.skunivlikelion.homepage.global.exception.CustomException;
import com.skunivlikelion.homepage.global.filter.JwtAuthenticationFilter;

import lombok.RequiredArgsConstructor;

/**
 * 현재 응답을 요청한 사용자의 정보를 반환하는 Provider 입니다.
 *
 * @since 2026.01.22
 * @see CustomUserDetails
 * @see CustomUserDetailsService
 * @see JwtAuthenticationFilter
 * @author Keum Si Eon
 */
@Component
@RequiredArgsConstructor
public class CurrentUserProvider {

  private final UserRepository userRepository;

  /**
   * 현재 로그인된 사용자의 식별자를 반환합니다.
   *
   * @return userId: 로그인된 사용자 식별자
   */
  public Long getCurrentUserId() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    isAuthenticated(auth);

    return getCurrentCustomUserDetails(getCurrentUserDetails(auth.getPrincipal()))
        .getUser()
        .getId();
  }

  /**
   * 현재 로그인된 사용자 객체를 반환합니다.
   *
   * @return User: 로그인된 사용자 객체
   */
  public User getCurrentUser() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    isAuthenticated(auth);

    return getCurrentCustomUserDetails(getCurrentUserDetails(auth.getPrincipal())).getUser();
  }

  private void isAuthenticated(Authentication auth) {
    if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
      throw new CustomException(AuthErrorCode.UNAUTHORIZED);
    }
  }

  private UserDetails getCurrentUserDetails(Object principal) {
    if (principal instanceof UserDetails ud) return ud;
    else throw new CustomException(AuthErrorCode.UNAUTHORIZED);
  }

  private CustomUserDetails getCurrentCustomUserDetails(UserDetails userDetails) {
    if (userDetails instanceof CustomUserDetails customUserDetails) return customUserDetails;
    else throw new CustomException(AuthErrorCode.UNAUTHORIZED);
  }
}
