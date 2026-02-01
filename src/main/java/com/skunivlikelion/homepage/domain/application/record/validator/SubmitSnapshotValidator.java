/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.application.record.validator;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.skunivlikelion.homepage.domain.application.question.entity.ApplicationQuestion;
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
      List<ApplicationQuestion> commonQuestions,
      List<ApplicationQuestion> trackQuestions,
      ApplicationDraftSaveRequest request) {

    validateSnapshotGroup(commonQuestions, request.getCommonAnswers(), Track.COMMON);

    validateSnapshotGroup(trackQuestions, request.getTrackAnswers(), request.getTrack());
  }

  private void validateSnapshotGroup(
      List<ApplicationQuestion> expectedQuestions,
      List<ApplicationAnswerSaveItem> requestAnswers,
      Track track) {

    if (requestAnswers == null || requestAnswers.isEmpty()) {
      throw new CustomException(ApplicationRecordErrorCode.INVALID_SUBMIT_SNAPSHOT);
    }

    Set<Long> expectedIds =
        expectedQuestions.stream().map(ApplicationQuestion::getId).collect(Collectors.toSet());

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
