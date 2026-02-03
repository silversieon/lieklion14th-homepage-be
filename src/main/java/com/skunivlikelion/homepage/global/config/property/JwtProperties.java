/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.global.config.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@ConfigurationProperties("jwt")
public class JwtProperties {

  private String secret;
  private long accessTokenValidityInSeconds;
  private long refreshTokenValidityInSeconds;
  private boolean secure;
  private String sameSite;
  private String refreshTokenPrefix;
  private String blackListPrefix;
}
