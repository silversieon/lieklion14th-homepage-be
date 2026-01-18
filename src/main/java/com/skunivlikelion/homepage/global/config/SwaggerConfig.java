/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.global.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.skunivlikelion.homepage.global.config.property.ServerProperties;
import com.skunivlikelion.homepage.global.config.property.SwaggerProperties;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class SwaggerConfig {

  private final ServerProperties serverProperties;
  private final SwaggerProperties swaggerProperties;

  @Bean
  public OpenAPI customOpenAPI() {
    Server server =
        new Server()
            .url(swaggerProperties.getUrl() + serverProperties.getContextPath())
            .description(swaggerProperties.getName() + " Server");

    return new OpenAPI()
        .addServersItem(server)
        .info(
            new Info()
                .title("SKU LikeLion Homepage API 명세서")
                .version("1.0")
                .description(
                    """
                        # 멋쟁이사자처럼 서경대학교 웹페이지 프로젝트 API 문서

                        ## 주의사항
                        - 파일 업로드 크기 제한: 5MB (1개 파일 크기)

                        """));
  }

  @Bean
  public GroupedOpenApi apiGroup() {
    return GroupedOpenApi.builder().group("api").pathsToMatch("/**").build();
  }
}
