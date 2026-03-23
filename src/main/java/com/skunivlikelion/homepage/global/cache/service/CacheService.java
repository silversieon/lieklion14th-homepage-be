/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.global.cache.service;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import com.skunivlikelion.homepage.global.cache.enums.CacheName;
import com.skunivlikelion.homepage.global.cache.exception.CacheErrorCode;
import com.skunivlikelion.homepage.global.exception.CustomException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CacheService {

  private final CacheManager cacheManager;

  public void evict(CacheName cacheName, Object key) {
    Cache cache = getCacheOrThrow(cacheName);

    log.warn("[CACHE EVICT] cache={}, key={}", cacheName, key);
    cache.evict(key);
  }

  public void evictAll(CacheName cacheName) {
    Cache cache = getCacheOrThrow(cacheName);

    log.warn("[CACHE EVICT ALL] cache={}", cacheName);
    cache.clear();
  }

  private Cache getCacheOrThrow(CacheName cacheName) {
    Cache cache = cacheManager.getCache(cacheName.value());

    if (cache == null) {
      throw new CustomException(CacheErrorCode.INVALID_CACHE_NAME);
    }

    return cache;
  }
}
