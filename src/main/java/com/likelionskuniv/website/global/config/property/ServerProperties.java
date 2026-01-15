/* 
 * Copyright (c) SKU LIKELION 
 */
package com.likelionskuniv.website.global.config.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@ConfigurationProperties("server.servlet")
public class ServerProperties {

  private String contextPath;
}
