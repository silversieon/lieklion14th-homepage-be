package com.skunivlikelion.homepage.global.config;

import com.skunivlikelion.homepage.global.config.property.CsrfProperties;
import com.skunivlikelion.homepage.global.config.property.JwtProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRepository;

@Configuration
@RequiredArgsConstructor
public class CsrfConfig {

    private final CsrfProperties csrfProperties;
    private final JwtProperties jwtProperties;

    /** CSRF Cookie 발급 Repository Bean */
    @Bean
    public CsrfTokenRepository csrfTokenRepository() {
        CookieCsrfTokenRepository repository = CookieCsrfTokenRepository.withHttpOnlyFalse();
        repository.setCookieCustomizer(cookie -> cookie
                .domain(csrfProperties.getAllowedOrigin())
                .sameSite(jwtProperties.getSameSite())
                .secure(jwtProperties.isSecure())
                .path("/"));
        return repository;
    }
}
