/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.global.cache.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.skunivlikelion.homepage.global.cache.enums.CacheName;
import com.skunivlikelion.homepage.global.cache.service.CacheService;
import com.skunivlikelion.homepage.global.common.BaseResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "Cache", description = "캐시 관리 API")
public class CacheController {

  private final CacheService cacheService;

  @Operation(
      summary = "[ 관리자 | 토큰 O | 캐시 무효화 ]",
      description =
          """
              **Path Parameter**
              cacheName: 캐시 이름

              **Query Parameter (Optional)**
              - QUESTIONS 캐시
              → key = formId (application form ID)

              **동작**
              - key 존재 → 특정 캐시 데이터 삭제
              - key 없음 → 해당 캐시 전체 삭제
              """)
  @DeleteMapping("/v1/admin/cache/{cacheName}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<BaseResponse<Void>> evict(
      @PathVariable CacheName cacheName, @RequestParam(required = false) Long key) {

    if (key == null) {
      cacheService.evictAll(cacheName);
    } else {
      cacheService.evict(cacheName, key);
    }
    return ResponseEntity.status(200).body(BaseResponse.success(200, "캐시 무효화에 성공했습니다.", null));
  }
}
