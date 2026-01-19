/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.global.config.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@ConfigurationProperties("aws")
public class AwsProperties {

  private String regionStatic;
  private String stackAuto;
  private Credentials credentials;
  private S3 s3;

  @Getter
  @AllArgsConstructor
  public static class Credentials {
    private String accessKey;
    private String secretKey;
  }

  @Getter
  @AllArgsConstructor
  public static class S3 {
    private String bucket;
    private Path path;

    @Getter
    @AllArgsConstructor
    public static class Path {
      private String profile;
      private String project;
    }
  }
}
