/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.global.cache.exception;

import org.springframework.http.HttpStatus;

import com.skunivlikelion.homepage.global.exception.model.BaseErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CacheErrorCode implements BaseErrorCode {
  INVALID_CACHE_NAME("CACHE4001", "존재하지 않는 캐시명입니다.", HttpStatus.BAD_REQUEST);

  private final String code;
  private final String message;
  private final HttpStatus status;
}
