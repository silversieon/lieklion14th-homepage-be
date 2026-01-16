/* 
 * Copyright (c) SKU LIKELION 
 */
package com.likelionskuniv.website.global.jwt;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.crypto.SecretKey;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.likelionskuniv.website.global.config.property.JwtProperties;

import backend.boilerplate.exception.CustomException;
import backend.boilerplate.exception.GlobalErrorCode;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtProvider {

  private final JwtProperties jwtProperties;
  private final RedisTemplate<String, String> redisTemplate;

  private Key getSigningKey() {
    return Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
  }

  public String generateToken(String authenticatedEmail, TokenType tokenType) {
    Instant now = Instant.now();
    Date expireTime;
    if (tokenType == TokenType.ACCESS_TOKEN) {
      expireTime = Date.from(now.plusSeconds(jwtProperties.getAccessTokenValidityInSeconds()));
    } else if (tokenType == TokenType.REFRESH_TOKEN) {
      expireTime = Date.from(now.plusSeconds(jwtProperties.getRefreshTokenValidityInSeconds()));
    } else {
      log.error("[Jwt] 유효하지 않은 토큰 타입 - 토큰 타입: {}", tokenType);
      throw new CustomException(GlobalErrorCode.INTERNAL_SERVER_ERROR);
    }

    return Jwts.builder()
        .subject(authenticatedEmail)
        .claim("type", tokenType.toString())
        .issuedAt(Date.from(now))
        .expiration(expireTime)
        .signWith(getSigningKey())
        .compact();
  }

  public String getEmailFromToken(String token) {
    return Jwts.parser()
        .verifyWith((SecretKey) getSigningKey())
        .build()
        .parseSignedClaims(token)
        .getPayload()
        .getSubject();
  }

  public String getTokenTypeFromToken(String token) {
    return Jwts.parser()
        .verifyWith(((SecretKey) getSigningKey()))
        .build()
        .parseSignedClaims(token)
        .getPayload()
        .get("type", String.class);
  }

  public boolean validateToken(String token) {
    try {
      if (isBlacklisted(token)) {
        log.info("[Jwt] 해당 토큰은 블랙리스트에 등록되어 있습니다.");
        return false;
      }
      Jwts.parser().verifyWith((SecretKey) getSigningKey()).build().parseSignedClaims(token);
      return true;
    } catch (SecurityException | MalformedJwtException e) {
      log.info("[Jwt] 잘못된 JWT 서명입니다.");
    } catch (ExpiredJwtException e) {
      log.info("[Jwt] 만료된 JWT 토큰입니다.");
    } catch (UnsupportedJwtException e) {
      log.info("[Jwt] 지원되지 않는 JWT 토큰입니다.");
    } catch (IllegalArgumentException e) {
      log.info("[Jwt] JWT 토큰이 잘못되었습니다.");
    }
    return false;
  }

  public void saveRefreshToken(String token, String email) {
    String redisKey = jwtProperties.getRefreshTokenPrefix() + email;
    redisTemplate
        .opsForValue()
        .set(redisKey, token, jwtProperties.getRefreshTokenValidityInSeconds(), TimeUnit.SECONDS);
  }

  public void addToBlackList(String token, long expiration) {
    String redisKey = jwtProperties.getBlackListPrefix() + token;
    redisTemplate.opsForValue().set(redisKey, "blacklisted", expiration, TimeUnit.SECONDS);
  }

  private boolean isBlacklisted(String token) {
    String redisKey = jwtProperties.getBlackListPrefix() + token;
    return redisTemplate.hasKey(redisKey);
  }
}
