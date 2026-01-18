/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
@ConfigurationPropertiesScan(basePackages = "com.skunivlikelion.homepage.global.config.property")
public class WebsiteApplication {

  public static void main(String[] args) {
    SpringApplication.run(WebsiteApplication.class, args);
  }
}
