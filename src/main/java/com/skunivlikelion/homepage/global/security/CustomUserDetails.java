/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.global.security;

import java.util.Collection;
import java.util.List;

import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.skunivlikelion.homepage.domain.user.entity.User;

import lombok.Getter;

@Getter
public class CustomUserDetails implements UserDetails {

  private final User user;
  private final Long userId;

  public CustomUserDetails(User user) {
    this.user = user;
    this.userId = user.getId();
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of(new SimpleGrantedAuthority("ROLE_" + user.getUserRole().name()));
  }

  @Override
  public @Nullable String getPassword() {
    return user.getPassword();
  }

  @Override
  public String getUsername() {
    return user.getEmail();
  }
}
