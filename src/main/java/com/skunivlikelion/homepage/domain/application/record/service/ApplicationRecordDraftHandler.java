/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.application.record.service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.skunivlikelion.homepage.domain.application.question.cache.ApplicationQuestionCacheService;
import com.skunivlikelion.homepage.domain.application.question.cache.CachedQuestion;
import com.skunivlikelion.homepage.domain.application.question.cache.QuestionsBundle;
import com.skunivlikelion.homepage.domain.application.record.dto.request.ApplicationAnswerSaveItem;
import com.skunivlikelion.homepage.domain.application.record.dto.request.ApplicationDraftSaveRequest;
import com.skunivlikelion.homepage.domain.application.record.entity.ApplicationAnswer;
import com.skunivlikelion.homepage.domain.application.record.entity.ApplicationRecord;
import com.skunivlikelion.homepage.domain.application.record.exception.ApplicationRecordErrorCode;
import com.skunivlikelion.homepage.domain.application.record.repository.ApplicationAnswerRepository;
import com.skunivlikelion.homepage.domain.common.enums.Track;
import com.skunivlikelion.homepage.global.exception.CustomException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ApplicationRecordDraftHandler {

  private static final int MAX_ANSWER_LENGTH = 500;

  private final ApplicationAnswerRepository applicationAnswerRepository;

  private final ApplicationQuestionCacheService applicationQuestionCacheService;

  // 임시저장 시 사용할 메서드
  public void applyDraftSave(
      ApplicationRecord record, Long formId, ApplicationDraftSaveRequest request) {

    QuestionsBundle bundle = applicationQuestionCacheService.getQuestionsBundle(formId);
    applyDraftSave(record, formId, request, bundle);
  }

  // 제출 시 사용할 메서드
  public void applyDraftSave(
      ApplicationRecord record,
      Long formId,
      ApplicationDraftSaveRequest request,
      QuestionsBundle bundle) {

    applyTrackChangeIfNeeded(record, formId, request.getTrack());
    ensureAnswersInit(record, formId, record.getTrack());
    overwriteAnswers(record, formId, request, bundle);
  }

  public Map<Long, ApplicationAnswer> loadAnswerMap(Long recordId) {
    return applicationAnswerRepository.findAllWithQuestionByRecordId(recordId).stream()
        .collect(Collectors.toMap(a -> a.getQuestion().getId(), Function.identity(), (a, b) -> a));
  }

  private void applyTrackChangeIfNeeded(ApplicationRecord record, Long formId, Track requested) {

    if (requested == null) {
      log.warn(
          "[ApplicationRecord] 트랙 변경 실패: requestedTrack null - recordId={}, formId={}, currentTrack={}",
          record.getId(),
          formId,
          record.getTrack());
      throw new CustomException(ApplicationRecordErrorCode.INVALID_ANSWER_PAYLOAD);
    }

    if (record.getTrack() == requested) {
      return;
    }

    Track before = record.getTrack();
    int deleted =
        applicationAnswerRepository.deleteAnswersByRecordIdAndTrack(record.getId(), before);

    record.changeTrack(requested);

    log.info(
        "[ApplicationRecord] 트랙 변경 - recordId={}, formId={}, before={}, after={}, deletedAnswers={}",
        record.getId(),
        formId,
        before,
        requested,
        deleted);
  }

  private void ensureAnswersInit(ApplicationRecord record, Long formId, Track track) {

    int inserted =
        applicationAnswerRepository.insertMissingAnswersForRecord(
            record.getId(), formId, Track.COMMON.name(), track.name());

    if (inserted > 0) {
      log.info(
          "[ApplicationRecord] 답변 초기화(bulk insert) - recordId={}, formId={}, track={}, insertCount={}",
          record.getId(),
          formId,
          track,
          inserted);
    }
  }

  private void overwriteAnswers(
      ApplicationRecord record,
      Long formId,
      ApplicationDraftSaveRequest request,
      QuestionsBundle bundle) {

    overwriteAnswerItems(record, formId, request.getCommonAnswers(), Track.COMMON, bundle);
    overwriteAnswerItems(record, formId, request.getTrackAnswers(), record.getTrack(), bundle);
  }

  private void overwriteAnswerItems(
      ApplicationRecord record,
      Long formId,
      List<ApplicationAnswerSaveItem> items,
      Track expectedTrack,
      QuestionsBundle bundle) {

    if (items == null || items.isEmpty()) {
      return;
    }

    validateAnswerItems(record.getId(), expectedTrack, items);

    for (ApplicationAnswerSaveItem item : items) {

      Long qid = item.getQuestionId();
      CachedQuestion q = bundle.getById(qid);

      if (q == null) {
        log.warn(
            "[ApplicationRecord] 답변 저장 실패: 존재하지 않는 questionId - recordId={}, formId={}, expectedTrack={}, questionId={}",
            record.getId(),
            formId,
            expectedTrack,
            qid);
        throw new CustomException(ApplicationRecordErrorCode.INVALID_ANSWER_QUESTION_NOT_FOUND);
      }

      if (q.track() != expectedTrack) {
        log.warn(
            "[ApplicationRecord] 답변 저장 실패: track mismatch - recordId={}, formId={}, expectedTrack={}, questionId={}, actualTrack={}",
            record.getId(),
            formId,
            expectedTrack,
            qid,
            q.track());
        throw new CustomException(ApplicationRecordErrorCode.INVALID_ANSWER_TRACK_MISMATCH);
      }
    }

    List<Long> questionIds = items.stream().map(ApplicationAnswerSaveItem::getQuestionId).toList();

    Map<Long, ApplicationAnswer> answerMap =
        applicationAnswerRepository
            .findAllByRecordIdAndQuestionIds(record.getId(), questionIds)
            .stream()
            .collect(
                Collectors.toMap(a -> a.getQuestion().getId(), Function.identity(), (a, b) -> a));

    if (answerMap.size() != questionIds.size()) {
      log.warn(
          "[ApplicationRecord] 답변 저장 실패: answerMap mismatch - recordId={}, formId={}, expectedTrack={}, payloadSize={}, loadedAnswersSize={}",
          record.getId(),
          formId,
          expectedTrack,
          questionIds.size(),
          answerMap.size());
      throw new CustomException(ApplicationRecordErrorCode.INVALID_ANSWER_PAYLOAD);
    }

    for (ApplicationAnswerSaveItem item : items) {
      ApplicationAnswer answer = answerMap.get(item.getQuestionId());
      if (answer == null) {
        log.warn(
            "[ApplicationRecord] 답변 저장 실패: answer null - recordId={}, formId={}, expectedTrack={}, questionId={}",
            record.getId(),
            formId,
            expectedTrack,
            item.getQuestionId());
        throw new CustomException(ApplicationRecordErrorCode.INVALID_ANSWER_PAYLOAD);
      }
      answer.updateContent(item.getContent() == null ? "" : item.getContent());
    }

    log.debug(
        "[ApplicationRecord] 답변 저장 완료(Cache question validation) - recordId={}, formId={}, expectedTrack={}, itemsCount={}",
        record.getId(),
        formId,
        expectedTrack,
        items.size());
  }

  private void validateAnswerItems(
      Long recordId, Track track, List<ApplicationAnswerSaveItem> items) {

    Set<Long> seen = new HashSet<>();

    for (ApplicationAnswerSaveItem item : items) {
      Long qid = item.getQuestionId();

      if (qid == null || qid <= 0 || !seen.add(qid)) {
        log.warn(
            "[ApplicationRecord] 답변 payload invalid - recordId={}, track={}, questionId={}",
            recordId,
            track,
            qid);
        throw new CustomException(ApplicationRecordErrorCode.INVALID_ANSWER_PAYLOAD);
      }

      String content = item.getContent();
      if (content != null && content.length() > MAX_ANSWER_LENGTH) {
        log.warn(
            "[ApplicationRecord] 답변 길이 초과 - recordId={}, track={}, questionId={}, length={}",
            recordId,
            track,
            qid,
            content.length());
        throw new CustomException(ApplicationRecordErrorCode.ANSWER_TOO_LONG);
      }
    }
  }

  public record CachedQuestionsBundle(
      List<CachedQuestion> commonQuestions, List<CachedQuestion> trackQuestions) {}
}
