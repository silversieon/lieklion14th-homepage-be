/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.application.record.validator;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.skunivlikelion.homepage.domain.application.question.cache.CachedQuestion;
import com.skunivlikelion.homepage.domain.application.question.cache.QuestionsBundle;
import com.skunivlikelion.homepage.domain.application.record.dto.request.ApplicationAnswerSaveItem;
import com.skunivlikelion.homepage.domain.application.record.dto.request.ApplicationDraftSaveRequest;
import com.skunivlikelion.homepage.domain.application.record.exception.ApplicationRecordErrorCode;
import com.skunivlikelion.homepage.domain.common.enums.Track;
import com.skunivlikelion.homepage.global.exception.CustomException;

@Component
public class SubmitSnapshotValidator {

  /**
   * submit 요청이 전체 스냅샷인지 검증
   *
   * <p>조건: 모든 questionId가 존재, 중복 questionId 금지, 현재 모집 공고의 질문 집합과 정확히 일치
   */
  public void validate(
      List<CachedQuestion> commonQuestions,
      List<CachedQuestion> trackQuestions,
      ApplicationDraftSaveRequest request) {

    validateSnapshotGroup(commonQuestions, request.getCommonAnswers());

    validateSnapshotGroup(trackQuestions, request.getTrackAnswers());
  }

  public void validate(
      QuestionsBundle bundle, Track requestedTrack, ApplicationDraftSaveRequest request) {

    validateSnapshotGroup(bundle.getQuestions(Track.COMMON), request.getCommonAnswers());
    validateSnapshotGroup(bundle.getQuestions(requestedTrack), request.getTrackAnswers());
  }

  private void validateSnapshotGroup(
      List<CachedQuestion> expectedQuestions, List<ApplicationAnswerSaveItem> requestAnswers) {

    if (requestAnswers == null || requestAnswers.isEmpty()) {
      throw new CustomException(ApplicationRecordErrorCode.INVALID_SUBMIT_SNAPSHOT);
    }

    Set<Long> expectedIds =
        expectedQuestions.stream().map(CachedQuestion::questionId).collect(Collectors.toSet());

    Set<Long> requestIds = extractUniqueQuestionIds(requestAnswers);

    if (expectedIds.size() != requestIds.size()) {
      throw new CustomException(ApplicationRecordErrorCode.INVALID_SUBMIT_SNAPSHOT);
    }

    if (!expectedIds.equals(requestIds)) {
      throw new CustomException(ApplicationRecordErrorCode.INVALID_SUBMIT_QUESTION_SET);
    }
  }

  private Set<Long> extractUniqueQuestionIds(List<ApplicationAnswerSaveItem> items) {
    Set<Long> ids = new HashSet<>();

    for (ApplicationAnswerSaveItem item : items) {
      if (item == null || item.getQuestionId() == null || item.getQuestionId() <= 0) {
        throw new CustomException(ApplicationRecordErrorCode.INVALID_SUBMIT_SNAPSHOT);
      }

      if (!ids.add(item.getQuestionId())) {
        throw new CustomException(ApplicationRecordErrorCode.INVALID_SUBMIT_SNAPSHOT);
      }
    }

    return ids;
  }
}
