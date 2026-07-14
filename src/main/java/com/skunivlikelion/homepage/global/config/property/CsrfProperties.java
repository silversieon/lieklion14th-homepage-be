package com.skunivlikelion.homepage.global.config.property;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@AllArgsConstructor
@ConfigurationProperties("csrf")
public class CsrfProperties {

    private String allowedOrigin;
}
