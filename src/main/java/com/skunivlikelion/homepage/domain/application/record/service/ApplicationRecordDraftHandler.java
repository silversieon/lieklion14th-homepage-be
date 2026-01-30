/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.application.record.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.skunivlikelion.homepage.domain.application.question.entity.ApplicationQuestion;
import com.skunivlikelion.homepage.domain.application.question.repository.ApplicationQuestionRepository;
import com.skunivlikelion.homepage.domain.application.record.dto.request.ApplicationAnswerSaveItem;
import com.skunivlikelion.homepage.domain.application.record.dto.request.ApplicationDraftSaveRequest;
import com.skunivlikelion.homepage.domain.application.record.entity.ApplicationAnswer;
import com.skunivlikelion.homepage.domain.application.record.entity.ApplicationRecord;
import com.skunivlikelion.homepage.domain.application.record.exception.ApplicationRecordErrorCode;
import com.skunivlikelion.homepage.domain.application.record.mapper.ApplicationRecordMapper;
import com.skunivlikelion.homepage.domain.application.record.repository.ApplicationAnswerRepository;
import com.skunivlikelion.homepage.domain.common.enums.Track;

import backend.boilerplate.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ApplicationRecordDraftHandler {

  private static final int MAX_ANSWER_LENGTH = 500;

  private final ApplicationQuestionRepository applicationQuestionRepository;
  private final ApplicationAnswerRepository applicationAnswerRepository;
  private final ApplicationRecordMapper applicationRecordMapper;

  // draft 저장(트랙변경/초기화/overwrite) 전체 파이프라인
  public void applyDraftSave(
      ApplicationRecord record, Long formId, ApplicationDraftSaveRequest request) {
    applyTrackChangeIfNeeded(record, formId, request.getTrack());
    ensureAnswersInit(record, formId, record.getTrack());
    overwriteAnswers(record, formId, request);
  }

  public QuestionsBundle loadQuestions(Long formId, Track track) {
    return new QuestionsBundle(
        applicationQuestionRepository.findAllByApplicationForm_IdAndTrackOrderByOrderNumberAsc(
            formId, Track.COMMON),
        applicationQuestionRepository.findAllByApplicationForm_IdAndTrackOrderByOrderNumberAsc(
            formId, track));
  }

  public Map<Long, ApplicationAnswer> loadAnswerMap(Long recordId) {
    return applicationAnswerRepository.findAllWithQuestionByRecordId(recordId).stream()
        .collect(Collectors.toMap(a -> a.getQuestion().getId(), Function.identity()));
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

    ensureAnswersInit(record, formId, requested);
  }

  private void ensureAnswersInit(ApplicationRecord record, Long formId, Track track) {

    QuestionsBundle questions = loadQuestions(formId, track);

    Set<Long> existingIds =
        new HashSet<>(applicationAnswerRepository.findQuestionIdsByRecordId(record.getId()));

    List<ApplicationAnswer> toInsert = new ArrayList<>();
    addMissingAnswers(toInsert, record, questions.commonQuestions(), existingIds);
    addMissingAnswers(toInsert, record, questions.trackQuestions(), existingIds);

    if (!toInsert.isEmpty()) {
      applicationAnswerRepository.saveAll(toInsert);
      log.info(
          "[ApplicationRecord] 답변 초기화(insert) - recordId={}, formId={}, track={}, insertCount={}",
          record.getId(),
          formId,
          track,
          toInsert.size());
    }
  }

  private void addMissingAnswers(
      List<ApplicationAnswer> target,
      ApplicationRecord record,
      List<ApplicationQuestion> questions,
      Set<Long> existingIds) {

    for (ApplicationQuestion q : questions) {
      if (!existingIds.contains(q.getId())) {
        target.add(applicationRecordMapper.toNewEmptyAnswer(record, q));
      }
    }
  }

  private void overwriteAnswers(
      ApplicationRecord record, Long formId, ApplicationDraftSaveRequest request) {

    overwriteAnswerItems(record, formId, request.getCommonAnswers(), Track.COMMON);
    overwriteAnswerItems(record, formId, request.getTrackAnswers(), record.getTrack());
  }

  private void overwriteAnswerItems(
      ApplicationRecord record,
      Long formId,
      List<ApplicationAnswerSaveItem> items,
      Track expectedTrack) {

    if (items == null || items.isEmpty()) {
      return;
    }

    validateAnswerItems(record.getId(), expectedTrack, items);

    List<Long> questionIds = items.stream().map(ApplicationAnswerSaveItem::getQuestionId).toList();

    List<ApplicationQuestion> questions =
        applicationQuestionRepository.findAllByFormIdAndQuestionIds(formId, questionIds);

    if (questions.size() != questionIds.size()) {
      log.warn(
          "[ApplicationRecord] 답변 저장 실패: question mismatch - recordId={}, formId={}, expectedTrack={}, payloadSize={}, loadedQuestionsSize={}",
          record.getId(),
          formId,
          expectedTrack,
          questionIds.size(),
          questions.size());
      throw new CustomException(ApplicationRecordErrorCode.INVALID_ANSWER_PAYLOAD);
    }

    boolean hasMismatchedTrack = questions.stream().anyMatch(q -> q.getTrack() != expectedTrack);
    if (hasMismatchedTrack) {
      log.warn(
          "[ApplicationRecord] 답변 저장 실패: track mismatch - recordId={}, formId={}, expectedTrack={}, payloadSize={}",
          record.getId(),
          formId,
          expectedTrack,
          questionIds.size());
      throw new CustomException(ApplicationRecordErrorCode.INVALID_ANSWER_PAYLOAD);
    }

    Map<Long, ApplicationAnswer> answerMap =
        applicationAnswerRepository
            .findAllByRecordIdAndQuestionIds(record.getId(), questionIds)
            .stream()
            .collect(Collectors.toMap(a -> a.getQuestion().getId(), Function.identity()));

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
        "[ApplicationRecord] 답변 저장 완료 - recordId={}, formId={}, expectedTrack={}, itemsCount={}",
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

  public record QuestionsBundle(
      List<ApplicationQuestion> commonQuestions, List<ApplicationQuestion> trackQuestions) {}
}
