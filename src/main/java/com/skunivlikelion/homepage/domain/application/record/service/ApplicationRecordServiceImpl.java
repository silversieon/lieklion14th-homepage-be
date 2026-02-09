/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.application.record.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.skunivlikelion.homepage.domain.application.form.entity.ApplicationForm;
import com.skunivlikelion.homepage.domain.application.form.repository.ApplicationFormRepository;
import com.skunivlikelion.homepage.domain.application.form.service.ApplicationFormService;
import com.skunivlikelion.homepage.domain.application.record.dto.request.ApplicationDraftSaveRequest;
import com.skunivlikelion.homepage.domain.application.record.dto.response.AdminApplicantListItem;
import com.skunivlikelion.homepage.domain.application.record.dto.response.ApplicantUserInfo;
import com.skunivlikelion.homepage.domain.application.record.dto.response.ApplicationAnswerItem;
import com.skunivlikelion.homepage.domain.application.record.dto.response.ApplicationRecordMeta;
import com.skunivlikelion.homepage.domain.application.record.dto.response.ApplicationRecordResponse;
import com.skunivlikelion.homepage.domain.application.record.entity.ApplicationAnswer;
import com.skunivlikelion.homepage.domain.application.record.entity.ApplicationRecord;
import com.skunivlikelion.homepage.domain.application.record.exception.ApplicationRecordErrorCode;
import com.skunivlikelion.homepage.domain.application.record.mapper.ApplicationRecordMapper;
import com.skunivlikelion.homepage.domain.application.record.repository.ApplicationAnswerRepository;
import com.skunivlikelion.homepage.domain.application.record.repository.ApplicationRecordRepository;
import com.skunivlikelion.homepage.domain.application.record.service.ApplicationRecordDraftHandler.QuestionsBundle;
import com.skunivlikelion.homepage.domain.application.record.validator.SubmitSnapshotValidator;
import com.skunivlikelion.homepage.domain.common.enums.Track;
import com.skunivlikelion.homepage.domain.user.entity.User;
import com.skunivlikelion.homepage.global.exception.CustomException;
import com.skunivlikelion.homepage.global.page.exception.PageErrorStatus;
import com.skunivlikelion.homepage.global.page.mapper.InfiniteMapper;
import com.skunivlikelion.homepage.global.page.response.InfiniteResponse;
import com.skunivlikelion.homepage.global.security.CurrentUserProvider;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ApplicationRecordServiceImpl implements ApplicationRecordService {

  private final ApplicationFormRepository applicationFormRepository;
  private final ApplicationRecordRepository applicationRecordRepository;
  private final ApplicationAnswerRepository applicationAnswerRepository;

  private final CurrentUserProvider currentUserProvider;
  private final ApplicationRecordMapper applicationRecordMapper;
  private final InfiniteMapper infiniteMapper;

  private final SubmitSnapshotValidator submitSnapshotValidator;
  private final ApplicationRecordDraftHandler draftHandler;

  private final ApplicationFormService applicationFormService;

  @Override
  public ApplicationRecordMeta saveFirstDraft(ApplicationDraftSaveRequest request) {

    validateRequestTrack(request);

    User user = currentUserProvider.getCurrentUser();
    Long userId = user.getId();
    LocalDateTime now = LocalDateTime.now();

    ApplicationForm current = getSubmittableFormOrThrow(now);
    Long formId = current.getId();
    Long semester = current.getSemester().getSemester();

    if (applicationRecordRepository.findByApplicationFormIdAndUserId(formId, userId).isPresent()) {
      log.warn(
          "[ApplicationRecord] 최초 임시저장 실패: 작성중 또는 제출된 지원서 존재 - semester={}, formId={}, userId={}",
          semester,
          formId,
          userId);
      throw new CustomException(ApplicationRecordErrorCode.ALREADY_RECORD_EXISTS);
    }

    ApplicationRecord record =
        applicationRecordRepository.save(
            applicationRecordMapper.toNewDraftRecord(current, user, request.getTrack()));

    draftHandler.applyDraftSave(record, formId, request);

    log.info(
        "[ApplicationRecord] first draft 반환 - semester={}, formId={}, userId={}, recordId={}, track={}",
        semester,
        formId,
        userId,
        record.getId(),
        record.getTrack());

    return applicationRecordMapper.toMeta(record);
  }

  @Override
  public ApplicationRecordMeta saveDraft(ApplicationDraftSaveRequest request) {

    validateRequestTrack(request);

    User user = currentUserProvider.getCurrentUser();
    Long userId = user.getId();
    LocalDateTime now = LocalDateTime.now();

    ApplicationForm current = getSubmittableFormOrThrow(now);
    Long formId = current.getId();

    ApplicationRecord record =
        applicationRecordRepository
            .findByApplicationFormIdAndUserId(formId, userId)
            .orElseThrow(() -> new CustomException(ApplicationRecordErrorCode.NOT_FOUND_DRAFT));

    if (record.isSubmitted()) {
      log.warn(
          "[ApplicationRecord] draft 저장 실패: 이미 제출됨 - formId={}, userId={}, recordId={}",
          formId,
          userId,
          record.getId());
      throw new CustomException(ApplicationRecordErrorCode.ALREADY_SUBMITTED);
    }

    draftHandler.applyDraftSave(record, formId, request);

    log.info(
        "[ApplicationRecord] draft 반환 - formId={}, userId={}, recordId={}, track={}",
        formId,
        userId,
        record.getId(),
        record.getTrack());

    return applicationRecordMapper.toMeta(record);
  }

  @Override
  public ApplicationRecordMeta submit(ApplicationDraftSaveRequest request) {

    validateRequestTrack(request);

    User user = currentUserProvider.getCurrentUser();
    Long userId = user.getId();
    LocalDateTime now = LocalDateTime.now();

    ApplicationForm current = getSubmittableFormOrThrow(now);
    Long formId = current.getId();
    Long semester = current.getSemester().getSemester();

    if (applicationRecordRepository.existsSubmitted(formId, userId)) {
      log.warn(
          "[ApplicationRecord] 중복 제출 시도 - semester={}, formId={}, userId={}",
          semester,
          formId,
          userId);
      throw new CustomException(ApplicationRecordErrorCode.ALREADY_SUBMITTED);
    }

    QuestionsBundle q = draftHandler.loadQuestions(formId, request.getTrack());
    submitSnapshotValidator.validate(q.commonQuestions(), q.trackQuestions(), request);

    ApplicationRecord record = getOrCreateRecord(current, user, request.getTrack(), semester);

    draftHandler.applyDraftSave(record, formId, request);
    record.markSubmitted(now);

    log.info(
        "[ApplicationRecord] submit 반환 - semester={}, formId={}, userId={}, recordId={}, submittedAt={}",
        semester,
        formId,
        userId,
        record.getId(),
        record.getSubmittedAt());

    return applicationRecordMapper.toMeta(record);
  }

  @Override
  @Transactional(readOnly = true)
  public ApplicationRecordResponse getMySubmittedApplicationAnswers() {

    User user = currentUserProvider.getCurrentUser();
    Long userId = user.getId();

    ApplicationForm current = applicationFormService.getCurrentApplicationForm();

    ApplicationRecord record =
        applicationRecordRepository
            .findSubmittedBySemesterAndUserId(current.getSemester().getSemester(), userId)
            .orElseThrow(() -> new CustomException(ApplicationRecordErrorCode.NOT_FOUND_RECORD));

    QuestionsBundle q =
        draftHandler.loadQuestions(record.getApplicationForm().getId(), record.getTrack());
    Map<Long, ApplicationAnswer> a = draftHandler.loadAnswerMap(record.getId());

    ApplicationRecordResponse response =
        applicationRecordMapper.toAnswersGetResponse(
            record, q.commonQuestions(), q.trackQuestions(), a);

    log.info(
        "[ApplicationRecord] submitted answers 반환 - currentFormId={}, semester={}, userId={}, recordId={}",
        current.getId(),
        current.getSemester().getSemester(),
        userId,
        record.getId());

    return response;
  }

  @Override
  @Transactional(readOnly = true)
  public ApplicantUserInfo getMyDraftPersonalInfo() {

    User user = currentUserProvider.getCurrentUser();
    Long userId = user.getId();

    ApplicationForm current = applicationFormService.getCurrentApplicationForm();

    Track trackOrNull =
        applicationRecordRepository
            .findByApplicationFormIdAndUserId(current.getId(), userId)
            .map(ApplicationRecord::getTrack)
            .orElse(null);

    log.info(
        "[ApplicationRecord] 내 인적사항 반환 - currentFormId={}, semester={}, userId={}, track={}",
        current.getId(),
        current.getSemester().getSemester(),
        userId,
        trackOrNull);

    return applicationRecordMapper.toApplicantUserInfo(user, trackOrNull);
  }

  @Override
  @Transactional(readOnly = true)
  public List<ApplicationAnswerItem> getMyDraftAnswersByTrack(Track track) {

    if (track == null) {
      throw new CustomException(ApplicationRecordErrorCode.INVALID_ANSWER_PAYLOAD);
    }

    User user = currentUserProvider.getCurrentUser();
    Long userId = user.getId();
    LocalDateTime now = LocalDateTime.now();

    ApplicationForm current = getSubmittableFormOrThrow(now);

    ApplicationRecord draft =
        applicationRecordRepository
            .findByApplicationFormIdAndUserId(current.getId(), userId)
            .orElseThrow(() -> new CustomException(ApplicationRecordErrorCode.NOT_FOUND_DRAFT));

    if (draft.isSubmitted()) {
      throw new CustomException(ApplicationRecordErrorCode.ALREADY_SUBMITTED);
    }

    List<ApplicationAnswer> answers =
        applicationAnswerRepository.findAllWithQuestionByRecordIdAndTrack(draft.getId(), track);

    log.info(
        "[ApplicationRecord] draft answers 반환 - currentFormId={}, semester={}, userId={}, recordId={}, requestedTrack={}, size={}",
        current.getId(),
        current.getSemester().getSemester(),
        userId,
        draft.getId(),
        track,
        answers.size());

    return applicationRecordMapper.toAnswerItems(answers);
  }

  @Override
  @Transactional(readOnly = true)
  public InfiniteResponse<AdminApplicantListItem> getApplicants(
      Long semester, Track track, String search, Long lastCursor, Integer size) {

    int resolvedSize = (size == null) ? 10 : size;
    if (resolvedSize <= 0 || resolvedSize > 100) {
      throw new CustomException(PageErrorStatus.PAGE_SIZE_ERROR);
    }

    String normalized = (search == null || search.isBlank()) ? null : search.trim().toLowerCase();
    Pageable pageable = PageRequest.of(0, resolvedSize + 1);

    List<AdminApplicantListItem> items =
        applicationRecordRepository.findAdminApplicantListItemsInfinite(
            semester, track, normalized, lastCursor, pageable);

    boolean hasNext = items.size() > resolvedSize;
    if (hasNext) {
      items.remove(resolvedSize);
    }

    Long newLastCursor = items.isEmpty() ? null : items.getLast().applicationRecordId();
    return infiniteMapper.toInfiniteResponse(items, newLastCursor, hasNext, resolvedSize);
  }

  @Override
  @Transactional(readOnly = true)
  public ApplicationRecordResponse getApplicationDetail(Long applicationRecordId) {

    ApplicationRecord record =
        applicationRecordRepository
            .findSubmittedWithUserAndForm(applicationRecordId)
            .orElseThrow(() -> new CustomException(ApplicationRecordErrorCode.NOT_FOUND_RECORD));

    QuestionsBundle q =
        draftHandler.loadQuestions(record.getApplicationForm().getId(), record.getTrack());
    Map<Long, ApplicationAnswer> a = draftHandler.loadAnswerMap(record.getId());

    return applicationRecordMapper.toAnswersGetResponse(
        record, q.commonQuestions(), q.trackQuestions(), a);
  }

  // ========================================================================
  // Helper
  // ========================================================================
  private void validateRequestTrack(ApplicationDraftSaveRequest request) {
    if (request == null || request.getTrack() == null) {
      throw new CustomException(ApplicationRecordErrorCode.INVALID_ANSWER_PAYLOAD);
    }

    if (request.getTrack() == Track.COMMON) {
      throw new CustomException(ApplicationRecordErrorCode.INVALID_REQUEST_TRACK);
    }
  }

  // 최초 임시저장, 임시저장, 임지저장된 답변 조회, 제출에서만 사용
  private ApplicationForm getSubmittableFormOrThrow(LocalDateTime now) {
    return applicationFormRepository
        .findSubmittableApplicationForm(now)
        .orElseThrow(
            () -> {
              log.info("[ApplicationRecord] 현재 서류 지원기간 모집 공고 없음 - now={}", now);
              return new CustomException(
                  ApplicationRecordErrorCode.NOT_FOUND_SUBMITTABLE_APPLICATION_FORM);
            });
  }

  private ApplicationRecord getOrCreateRecord(
      ApplicationForm form, User user, Track requestedTrack, Long semester) {

    Long formId = form.getId();
    Long userId = user.getId();

    return applicationRecordRepository
        .findByApplicationFormIdAndUserId(formId, userId)
        .map(
            existing -> {
              log.info(
                  "[ApplicationRecord] record 재사용 - semester={}, formId={}, userId={}, recordId={}, track={}",
                  semester,
                  formId,
                  userId,
                  existing.getId(),
                  existing.getTrack());
              return existing;
            })
        .orElseGet(
            () -> {
              ApplicationRecord created =
                  applicationRecordRepository.save(
                      applicationRecordMapper.toNewDraftRecord(form, user, requestedTrack));
              log.info(
                  "[ApplicationRecord] record 생성 - semester={}, formId={}, userId={}, recordId={}, track={}",
                  semester,
                  formId,
                  userId,
                  created.getId(),
                  created.getTrack());
              return created;
            });
  }
}
