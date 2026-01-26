/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.global.security.jwt;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.crypto.SecretKey;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.skunivlikelion.homepage.domain.auth.dto.response.TokenResponse;
import com.skunivlikelion.homepage.domain.auth.mapper.AuthMapper;
import com.skunivlikelion.homepage.global.config.property.JwtProperties;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtProvider {

  private final JwtProperties jwtProperties;
  private final RedisTemplate<String, String> redisTemplate;

  private static final String AUTHORIZATION_HEADER = "Authorization";
  private static final String BEARER_PREFIX = "Bearer ";
  private final AuthMapper authMapper;

  private SecretKey getSigningKey() {
    return Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
  }

  public TokenResponse generateTokenResponse(Authentication authentication) {
    String email = authentication.getName();
    String accessToken = generateAccessToken(email);
    String refreshToken = generateRefreshToken(email);
    return authMapper.toTokenResponse(accessToken, refreshToken);
  }

  public String generateAccessToken(String authenticatedEmail) {
    Instant now = Instant.now();

    long validitySeconds = jwtProperties.getAccessTokenValidityInSeconds();
    Date issuedAt = Date.from(now);
    Date expiration = Date.from(now.plusSeconds(validitySeconds));

    return Jwts.builder()
        .subject(authenticatedEmail)
        .claim("type", TokenType.ACCESS_TOKEN.name())
        .issuedAt(issuedAt)
        .expiration(expiration)
        .signWith(getSigningKey())
        .compact();
  }

  public String generateRefreshToken(String authenticatedEmail) {
    Instant now = Instant.now();

    long validitySeconds = jwtProperties.getRefreshTokenValidityInSeconds();
    Date issuedAt = Date.from(now);
    Date expiration = Date.from(now.plusSeconds(validitySeconds));
    String jti = UUID.randomUUID().toString();
    String token =
        Jwts.builder()
            .subject(authenticatedEmail)
            .claim("type", TokenType.REFRESH_TOKEN.name())
            .issuedAt(issuedAt)
            .expiration(expiration)
            .id(jti)
            .signWith(getSigningKey())
            .compact();
    saveRefreshToken(token, jti);
    return token;
  }

  public String getEmailFromToken(String token) {
    return Jwts.parser()
        .verifyWith(getSigningKey())
        .build()
        .parseSignedClaims(token)
        .getPayload()
        .getSubject();
  }

  public String getTokenTypeFromToken(String token) {
    return Jwts.parser()
        .verifyWith(getSigningKey())
        .build()
        .parseSignedClaims(token)
        .getPayload()
        .get("type", String.class);
  }

  public String getJtiFromToken(String token) {
    return Jwts.parser()
        .verifyWith(getSigningKey())
        .build()
        .parseSignedClaims(token)
        .getPayload()
        .getId();
  }

  public boolean validateToken(String token) {
    try {
      if (isBlacklisted(token)) {
        log.info("[Jwt] 해당 토큰은 블랙리스트에 등록되어 있습니다.");
        return false;
      }
      Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token);
      return true;
    } catch (SecurityException | MalformedJwtException e) {
      log.info("[Jwt] 잘못된 JWT 서명입니다.");
    } catch (ExpiredJwtException e) {
      log.info("[Jwt] 만료된 JWT 토큰입니다.");
      throw e;
    } catch (UnsupportedJwtException e) {
      log.info("[Jwt] 지원되지 않는 JWT 토큰입니다.");
    } catch (IllegalArgumentException | JwtException e) {
      log.info("[Jwt] JWT 토큰이 잘못되었습니다.");
      throw e;
    }
    return false;
  }

  public boolean validateRefreshToken(String refreshToken) {
    if (!validateToken(refreshToken)) return false;
    String storedRefreshToken =
        redisTemplate
            .opsForValue()
            .get(jwtProperties.getRefreshTokenPrefix() + getJtiFromToken(refreshToken));
    return refreshToken.equals(storedRefreshToken);
  }

  public void addToBlackList(String refreshToken) {
    if (!validateTokenType(refreshToken, TokenType.REFRESH_TOKEN)) {
      log.warn("[Jwt] 올바르지 않은 Token 타입");
      return;
    }

    Claims claims =
        Jwts.parser()
            .verifyWith(getSigningKey())
            .build()
            .parseSignedClaims(refreshToken)
            .getPayload();

    String jti = claims.getId();
    String redisKey =
        jwtProperties.getBlackListPrefix() + jwtProperties.getRefreshTokenPrefix() + jti;

    long ttl = claims.getExpiration().toInstant().getEpochSecond() - Instant.now().getEpochSecond();
    if (ttl <= 0) return;

    redisTemplate.opsForValue().set(redisKey, "blacklisted", ttl, TimeUnit.SECONDS);

    redisTemplate.delete(jwtProperties.getRefreshTokenPrefix() + jti);
  }

  public String extractAccessToken(HttpServletRequest request) {
    String bearerToken = request.getHeader(AUTHORIZATION_HEADER);

    if (bearerToken != null && bearerToken.startsWith(BEARER_PREFIX)) {
      return bearerToken.substring(BEARER_PREFIX.length());
    } else if (request.getCookies() != null) {
      for (Cookie cookie : request.getCookies()) {
        if (TokenType.ACCESS_TOKEN.name().equals(cookie.getName())) {
          return cookie.getValue();
        }
      }
    }
    return null;
  }

  public String extractRefreshToken(HttpServletRequest request) {
    if (request.getCookies() != null) {
      for (Cookie cookie : request.getCookies()) {
        if (TokenType.REFRESH_TOKEN.name().equals(cookie.getName())) {
          return cookie.getValue();
        }
      }
    }
    return null;
  }

  public boolean validateTokenType(String token, TokenType tokenType) {
    return getTokenTypeFromToken(token).equals(tokenType.name());
  }

  private void saveRefreshToken(String token, String jti) {
    String redisKey = jwtProperties.getRefreshTokenPrefix() + jti;
    redisTemplate
        .opsForValue()
        .set(redisKey, token, jwtProperties.getRefreshTokenValidityInSeconds(), TimeUnit.SECONDS);
  }

  private boolean isBlacklisted(String refreshToken) {
    String redisKey =
        jwtProperties.getBlackListPrefix()
            + jwtProperties.getRefreshTokenPrefix()
            + getJtiFromToken(refreshToken);
    return redisTemplate.hasKey(redisKey);
  }
}
