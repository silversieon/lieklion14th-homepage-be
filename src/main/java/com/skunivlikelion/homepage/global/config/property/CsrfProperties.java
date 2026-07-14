/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.global.config.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@ConfigurationProperties("csrf")
public class CsrfProperties {

  private String allowedOrigin;
}
