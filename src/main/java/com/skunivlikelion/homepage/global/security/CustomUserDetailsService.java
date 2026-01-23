/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.global.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.skunivlikelion.homepage.domain.user.entity.User;
import com.skunivlikelion.homepage.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

  private final UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    User user =
        userRepository
            .findByEmail(email)
            .orElseThrow(
                () -> {
                  log.info("[Auth] Security: 해당 이메일을 가진 사용자가 없습니다. - 이메일: {}", email);
                  return new UsernameNotFoundException(email);
                });
    return new CustomUserDetails(user);
  }
}
