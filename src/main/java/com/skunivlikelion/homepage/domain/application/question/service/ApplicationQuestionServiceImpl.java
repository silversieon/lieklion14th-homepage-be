/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.application.question.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.skunivlikelion.homepage.domain.application.form.entity.ApplicationForm;
import com.skunivlikelion.homepage.domain.application.form.repository.ApplicationFormRepository;
import com.skunivlikelion.homepage.domain.application.form.service.ApplicationFormService;
import com.skunivlikelion.homepage.domain.application.question.dto.request.ApplicationQuestionUpsertRequest;
import com.skunivlikelion.homepage.domain.application.question.dto.request.ApplicationQuestionUpsertRequest.QuestionItemRequest;
import com.skunivlikelion.homepage.domain.application.question.dto.request.ApplicationQuestionUpsertRequest.TrackQuestionGroupRequest;
import com.skunivlikelion.homepage.domain.application.question.dto.response.ApplicationQuestionGetResponse;
import com.skunivlikelion.homepage.domain.application.question.dto.response.ApplicationQuestionUpsertResponse;
import com.skunivlikelion.homepage.domain.application.question.dto.response.ApplicationSummaryListResponse;
import com.skunivlikelion.homepage.domain.application.question.entity.ApplicationQuestion;
import com.skunivlikelion.homepage.domain.application.question.exception.ApplicationQuestionErrorCode;
import com.skunivlikelion.homepage.domain.application.question.mapper.ApplicationQuestionMapper;
import com.skunivlikelion.homepage.domain.application.question.repository.ApplicationQuestionRepository;
import com.skunivlikelion.homepage.domain.application.record.repository.ApplicationAnswerRepository;
import com.skunivlikelion.homepage.domain.common.enums.Track;
import com.skunivlikelion.homepage.global.exception.CustomException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ApplicationQuestionServiceImpl implements ApplicationQuestionService {

  private final ApplicationQuestionRepository applicationQuestionRepository;
  private final ApplicationFormRepository applicationFormRepository;
  private final ApplicationAnswerRepository applicationAnswerRepository;

  private final ApplicationQuestionMapper applicationQuestionMapper;
  private final ApplicationFormService applicationFormService;

  @Override
  public ApplicationQuestionUpsertResponse createQuestions(
      Long semester, ApplicationQuestionUpsertRequest request) {

    ApplicationForm form = getFormWithLock(semester);
    validateBeforeOpenAt(form);

    if (form.isHasQuestions()) {
      log.info(
          "[ApplicationQuestion] 질문 등록 실패: 이미 질문이 설정된 공고 - semester={}, formId={}",
          semester,
          form.getId());
      throw new CustomException(ApplicationQuestionErrorCode.ALREADY_CONFIGURED_QUESTIONS);
    }

    List<TrackQuestionGroupRequest> groups = safeGroups(request);
    if (!groups.isEmpty()) {
      validateRequestForUpsert(groups);
    }

    List<ApplicationQuestion> saved = saveQuestions(form, request);
    form.markHasQuestions();

    log.info(
        "[ApplicationQuestion] 질문 등록 완료 - semester={}, formId={}, savedCount={}",
        semester,
        form.getId(),
        saved.size());

    return applicationQuestionMapper.toUpsertResponse(form, saved);
  }

  @Override
  public ApplicationQuestionUpsertResponse updateQuestions(
      Long applicationFormId, ApplicationQuestionUpsertRequest request) {

    ApplicationForm form = getFormWithLockById(applicationFormId);
    validateBeforeOpenAt(form);

    if (applicationAnswerRepository.existsByQuestion_ApplicationForm_Id(form.getId())) {
      log.warn("[ApplicationQuestion] 답변 존재로 질문 변경 차단 - formId={}", form.getId());
      throw new CustomException(ApplicationQuestionErrorCode.QUESTIONS_IN_USE);
    }

    List<TrackQuestionGroupRequest> groups = safeGroups(request);

    if (groups.isEmpty()) {
      applicationQuestionRepository.deleteAllByApplicationFormId(form.getId());
      if (!form.isHasQuestions()) {
        form.markHasQuestions();
      }

      log.info("[ApplicationQuestion] 질문 수정(빈 상태 저장) 완료 - formId={}", form.getId());

      return applicationQuestionMapper.toUpsertResponse(form, List.of());
    }

    validateRequestForUpsert(groups);

    List<ApplicationQuestion> existing =
        applicationQuestionRepository.findAllByApplicationForm_IdOrderByTrackAscOrderNumberAsc(
            form.getId());

    Map<String, ApplicationQuestion> existingMap =
        existing.stream()
            .collect(
                Collectors.toMap(q -> key(q.getTrack(), q.getOrderNumber()), q -> q, (a, b) -> a));

    Set<String> desiredKeys = new HashSet<>();
    List<ApplicationQuestion> toInsert = new ArrayList<>();

    for (TrackQuestionGroupRequest g : groups) {
      Track track = g.getTrack();
      List<QuestionItemRequest> questions = Optional.ofNullable(g.getQuestions()).orElse(List.of());

      for (QuestionItemRequest item : questions) {
        String k = key(track, item.getOrderNumber());
        desiredKeys.add(k);

        ApplicationQuestion found = existingMap.get(k);
        if (found != null) {
          found.updateContent(item.getContent());
        } else {
          toInsert.add(
              applicationQuestionMapper.toEntity(
                  form, track, item.getOrderNumber(), item.getContent()));
        }
      }
    }

    if (!toInsert.isEmpty()) {
      applicationQuestionRepository.saveAll(toInsert);
    }

    List<Long> obsoleteIds =
        existing.stream()
            .filter(q -> !desiredKeys.contains(key(q.getTrack(), q.getOrderNumber())))
            .map(ApplicationQuestion::getId)
            .toList();

    if (!obsoleteIds.isEmpty()) {
      applicationQuestionRepository.deleteAllByIdInBatch(obsoleteIds);
    }

    if (!form.isHasQuestions()) {
      form.markHasQuestions();
    }

    List<ApplicationQuestion> refreshed =
        applicationQuestionRepository.findAllByApplicationForm_IdOrderByTrackAscOrderNumberAsc(
            form.getId());

    log.info(
        "[ApplicationQuestion] 질문 수정(upsert) 완료 - formId={}, inserted={}, deleted={}, total={}",
        form.getId(),
        toInsert.size(),
        obsoleteIds.size(),
        refreshed.size());

    return applicationQuestionMapper.toUpsertResponse(form, refreshed);
  }

  @Override
  public void deleteQuestions(Long applicationFormId) {
    ApplicationForm form = getFormWithLockById(applicationFormId);
    validateBeforeOpenAt(form);

    if (applicationAnswerRepository.existsByQuestion_ApplicationForm_Id(form.getId())) {
      throw new CustomException(ApplicationQuestionErrorCode.QUESTIONS_IN_USE);
    }

    applicationQuestionRepository.deleteAllByApplicationFormId(form.getId());

    if (form.isHasQuestions()) {
      form.unmarkHasQuestions();
    }

    log.info("[ApplicationQuestion] 질문 삭제 완료 - formId={}", form.getId());
  }

  @Override
  @Transactional(readOnly = true)
  public ApplicationQuestionGetResponse getCurrentQuestionsByTrack(Track track) {

    Long formId = applicationFormService.getCurrentApplicationFormId();
    ApplicationForm form =
        applicationFormRepository
            .findById(formId)
            .orElseThrow(
                () -> new CustomException(ApplicationQuestionErrorCode.NOT_FOUND_APPLICATION_FORM));

    if (!form.isHasQuestions()) {
      log.warn("[ApplicationQuestion] 질문 조회 실패: 지원서 미설정 공고 - formId={}, track={}", formId, track);
      throw new CustomException(ApplicationQuestionErrorCode.NOT_CONFIGURED_QUESTIONS);
    }

    List<ApplicationQuestion> questions =
        applicationQuestionRepository.findAllByApplicationForm_IdAndTrackOrderByOrderNumberAsc(
            formId, track);

    log.info(
        "[ApplicationQuestion] 진행중 공고 질문 조회 완료 - formId={}, semester={}, track={}, count={}",
        formId,
        form.getSemester().getSemester(),
        track,
        questions.size());

    return applicationQuestionMapper.toGetResponse(
        form.getSemester().getSemester(), track, questions);
  }

  @Override
  @Transactional(readOnly = true)
  public ApplicationSummaryListResponse getApplicationSummaries() {
    LocalDateTime now = LocalDateTime.now();

    List<ApplicationForm> forms =
        applicationFormRepository.findAllConfiguredWithSemesterOrderBySemesterDesc();

    ApplicationSummaryListResponse response =
        applicationQuestionMapper.toApplicationSummaryListResponse(now, forms);

    log.info(
        "[ApplicationQuestion] 지원서 목록 조회 완료 - inProgress={}, completed={}",
        response.getInProgress().size(),
        response.getCompleted().size());

    return response;
  }

  @Override
  @Transactional(readOnly = true)
  public ApplicationQuestionUpsertResponse getQuestionsBySemesterForDev(Long semester) {

    ApplicationForm form =
        applicationFormRepository
            .findBySemester_Semester(semester)
            .orElseThrow(
                () -> new CustomException(ApplicationQuestionErrorCode.NOT_FOUND_APPLICATION_FORM));

    if (!form.isHasQuestions()) {
      throw new CustomException(ApplicationQuestionErrorCode.NOT_CONFIGURED_QUESTIONS);
    }

    List<ApplicationQuestion> questions =
        applicationQuestionRepository.findAllByApplicationForm_IdOrderByTrackAscOrderNumberAsc(
            form.getId());

    return applicationQuestionMapper.toUpsertResponse(form, questions);
  }

  private ApplicationForm getFormWithLockById(Long applicationFormId) {
    return applicationFormRepository
        .findByIdForUpdate(applicationFormId)
        .orElseThrow(
            () -> {
              log.warn("[ApplicationQuestion] 모집 공고 없음(락 조회) - formId={}", applicationFormId);
              return new CustomException(ApplicationQuestionErrorCode.NOT_FOUND_APPLICATION_FORM);
            });
  }

  private String key(Track track, Integer orderNumber) {
    return track.name() + "#" + orderNumber;
  }

  private ApplicationForm getFormWithLock(Long semester) {
    return applicationFormRepository
        .findBySemesterForUpdate(semester)
        .orElseThrow(
            () -> {
              log.warn("[ApplicationQuestion] 모집 공고 없음(락 조회) - semester={}", semester);
              return new CustomException(ApplicationQuestionErrorCode.NOT_FOUND_APPLICATION_FORM);
            });
  }

  private void validateBeforeOpenAt(ApplicationForm form) {
    if (!LocalDateTime.now().isBefore(form.getOpenAt())) {
      log.warn(
          "[ApplicationQuestion] 모집 시작 이후 변경 시도 - formId={}, openAt={}",
          form.getId(),
          form.getOpenAt());
      throw new CustomException(ApplicationQuestionErrorCode.APPLICATION_ALREADY_OPENED);
    }
  }

  private List<TrackQuestionGroupRequest> safeGroups(ApplicationQuestionUpsertRequest request) {
    if (request == null || request.getGroups() == null) {
      return List.of();
    }
    return request.getGroups();
  }

  private List<ApplicationQuestion> saveQuestions(
      ApplicationForm form, ApplicationQuestionUpsertRequest request) {
    List<ApplicationQuestion> entities = applicationQuestionMapper.toEntities(form, request);
    if (entities.isEmpty()) {
      return List.of();
    }
    return applicationQuestionRepository.saveAll(entities);
  }

  private void validateRequestForUpsert(List<TrackQuestionGroupRequest> groups) {
    for (TrackQuestionGroupRequest group : groups) {
      List<Integer> orders =
          group.getQuestions().stream().map(QuestionItemRequest::getOrderNumber).toList();

      if (new HashSet<>(orders).size() != orders.size()) {
        log.warn(
            "[ApplicationQuestion] 문항번호 중복 요청 - track={}, orders={}", group.getTrack(), orders);
        throw new CustomException(ApplicationQuestionErrorCode.DUPLICATE_ORDER_NUMBER_IN_TRACK);
      }

      List<Integer> sorted = orders.stream().sorted().toList();

      if (sorted.getFirst() != 1) {
        log.warn(
            "[ApplicationQuestion] 문항번호 1부터 시작하지 않음 - track={}, orders={}",
            group.getTrack(),
            sorted);
        throw new CustomException(ApplicationQuestionErrorCode.INVALID_QUESTION_REQUEST);
      }

      for (int i = 0; i < sorted.size(); i++) {
        if (sorted.get(i) != i + 1) {
          log.warn(
              "[ApplicationQuestion] 문항번호 연속성 위반 - track={}, orders={}", group.getTrack(), sorted);
          throw new CustomException(ApplicationQuestionErrorCode.INVALID_QUESTION_REQUEST);
        }
      }
    }
  }
}
