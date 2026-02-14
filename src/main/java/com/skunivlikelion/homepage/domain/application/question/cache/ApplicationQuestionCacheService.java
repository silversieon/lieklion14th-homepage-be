/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.application.question.cache;

import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.skunivlikelion.homepage.domain.application.question.entity.ApplicationQuestion;
import com.skunivlikelion.homepage.domain.application.question.repository.ApplicationQuestionRepository;
import com.skunivlikelion.homepage.global.config.CacheConfig;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApplicationQuestionCacheService {

  private final ApplicationQuestionRepository questionRepository;

  // formId 기준 전체 질문(트랙 포함) 캐싱
  @Cacheable(cacheNames = CacheConfig.QUESTIONS_CACHE, key = "#formId", sync = true)
  @Transactional(readOnly = true)
  public QuestionsBundle getQuestionsBundle(Long formId) {
    log.info("[ApplicationQuestionCache] MISS - DB 질문지를 로드 formId={}", formId);

    List<ApplicationQuestion> entities =
        questionRepository.findAllByApplicationForm_IdOrderByTrackAscOrderNumberAsc(formId);

    List<CachedQuestion> snapshots =
        entities.stream()
            .map(
                q ->
                    new CachedQuestion(q.getId(), q.getTrack(), q.getOrderNumber(), q.getContent()))
            .toList();

    return QuestionsBundle.of(snapshots);
  }

  // 질문 생성/수정/삭제 이후 강제 무효화
  @CacheEvict(cacheNames = CacheConfig.QUESTIONS_CACHE, key = "#formId")
  public void evictQuestions(Long formId) {
    log.info("[ApplicationQuestionCache] EVICT - 질문지 캐시 제거 formId={}", formId);
  }
}
