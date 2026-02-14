/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.application.form.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.skunivlikelion.homepage.domain.application.form.dto.request.ApplicationFormUpsertRequest;
import com.skunivlikelion.homepage.domain.application.form.dto.response.ApplicationFormResponse;
import com.skunivlikelion.homepage.domain.application.form.dto.response.ApplicationFormSummaryResponse;
import com.skunivlikelion.homepage.domain.application.form.entity.ApplicationForm;
import com.skunivlikelion.homepage.domain.application.form.exception.ApplicationFormErrorCode;
import com.skunivlikelion.homepage.domain.application.form.mapper.ApplicationFormMapper;
import com.skunivlikelion.homepage.domain.application.form.repository.ApplicationFormRepository;
import com.skunivlikelion.homepage.domain.application.question.cache.ApplicationQuestionCacheService;
import com.skunivlikelion.homepage.domain.application.record.repository.ApplicationRecordRepository;
import com.skunivlikelion.homepage.domain.semester.entity.Semester;
import com.skunivlikelion.homepage.domain.semester.repository.SemesterRepository;
import com.skunivlikelion.homepage.global.exception.CustomException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ApplicationFormServiceImpl implements ApplicationFormService {

  private final ApplicationFormRepository applicationFormRepository;
  private final SemesterRepository semesterRepository;
  private final ApplicationRecordRepository applicationRecordRepository;

  private final ApplicationFormMapper applicationFormMapper;

  private final ApplicationQuestionCacheService applicationQuestionCacheService;

  @Override
  public ApplicationFormResponse createApplicationForm(ApplicationFormUpsertRequest request) {
    Long semesterId = request.getSemester();

    validateDateRange(request);

    validateNoOverlappedFormForCreate(request);

    if (semesterId == null) {
      log.warn("[ApplicationForm] 지원 일정 등록 실패 - semester=null");
      throw new CustomException(ApplicationFormErrorCode.NOT_FOUND_SEMESTER);
    }

    Semester semester =
        semesterRepository
            .findById(semesterId)
            .orElseThrow(
                () -> {
                  log.warn("[ApplicationForm] 지원 일정 등록 실패: 존재하지 않는 기수 - semester={}", semesterId);
                  return new CustomException(ApplicationFormErrorCode.NOT_FOUND_SEMESTER);
                });

    if (applicationFormRepository.existsBySemester_Semester(semesterId)) {
      log.info("[ApplicationForm] 지원 일정 등록 실패: 이미 존재하는 모집 공고 - semester={}", semesterId);
      throw new CustomException(ApplicationFormErrorCode.ALREADY_EXIST_APPLICATION_FORM);
    }

    ApplicationForm form = applicationFormMapper.toEntity(semester, request);

    ApplicationForm saved = applicationFormRepository.save(form);
    log.info("[ApplicationForm] 지원 일정 등록 완료 - semester={}, formId={}", semesterId, saved.getId());

    return applicationFormMapper.toResponse(saved);
  }

  @Override
  public ApplicationFormResponse updateApplicationForm(
      Long applicationFormId, ApplicationFormUpsertRequest request) {

    validateDateRange(request);

    ApplicationForm found =
        applicationFormRepository
            .findById(applicationFormId)
            .orElseThrow(
                () -> {
                  log.warn(
                      "[ApplicationForm] 지원 일정 수정 실패: 모집 공고 없음 - formId={}", applicationFormId);
                  return new CustomException(ApplicationFormErrorCode.NOT_FOUND_APPLICATION_FORM);
                });

    Long targetSemesterId = request.getSemester();
    if (targetSemesterId == null) {
      log.warn("[ApplicationForm] 지원 일정 수정 실패: semester=null - formId={}", applicationFormId);
      throw new CustomException(ApplicationFormErrorCode.INVALID_SEMESTER_VALUE);
    }

    Semester targetSemester =
        semesterRepository
            .findById(targetSemesterId)
            .orElseThrow(
                () -> {
                  log.warn(
                      "[ApplicationForm] 지원 일정 수정 실패: 존재하지 않는 기수 - formId={}, semester={}",
                      applicationFormId,
                      targetSemesterId);
                  return new CustomException(ApplicationFormErrorCode.NOT_FOUND_SEMESTER);
                });

    if (applicationFormRepository.existsBySemester_SemesterAndIdNot(
        targetSemesterId, applicationFormId)) {
      log.info(
          "[ApplicationForm] 지원 일정 수정 실패: 이미 존재하는 모집 공고(기수 중복) - formId={}, semester={}",
          applicationFormId,
          targetSemesterId);
      throw new CustomException(ApplicationFormErrorCode.ALREADY_EXIST_APPLICATION_FORM);
    }

    validateNoOverlappedFormForUpdate(found.getId(), request);

    found.update(
        targetSemester,
        request.getOpenAt(),
        request.getCloseAt(),
        request.getApplicationResultAt(),
        request.getInterviewScheduleConfirmedAt(),
        request.getFinalResultAt());

    log.info(
        "[ApplicationForm] 지원 일정 수정 완료 - formId={}, semester={} -> {}",
        found.getId(),
        found.getSemester().getSemester(),
        targetSemesterId);

    return applicationFormMapper.toResponse(found);
  }

  @Override
  public void deleteApplicationForm(Long applicationFormId) {

    ApplicationForm found =
        applicationFormRepository
            .findById(applicationFormId)
            .orElseThrow(
                () -> {
                  log.warn(
                      "[ApplicationForm] 지원 일정 삭제 실패 - 모집 공고 없음 - formId={}", applicationFormId);
                  return new CustomException(ApplicationFormErrorCode.NOT_FOUND_APPLICATION_FORM);
                });

    if (found.isHasQuestions()) {
      log.warn(
          "[ApplicationForm] 지원 일정 삭제 실패: 등록된 지원서 질문 존재 - formId={}, semester={}",
          found.getId(),
          found.getSemester().getSemester());
      throw new CustomException(ApplicationFormErrorCode.CANNOT_DELETE_FORM_WITH_QUESTIONS);
    }

    if (applicationRecordRepository.existsByApplicationFormId(found.getId())) {
      log.warn(
          "[ApplicationForm] 삭제 실패 - 지원서 존재 - formId={}, semester={}",
          found.getId(),
          found.getSemester().getSemester());
      throw new CustomException(ApplicationFormErrorCode.CANNOT_DELETE_FORM_WITH_RECORDS);
    }

    applicationFormRepository.delete(found);
    applicationQuestionCacheService.evictQuestions(found.getId());

    log.info(
        "[ApplicationForm] 지원 일정 삭제 완료: formId={}, semester={}",
        found.getId(),
        found.getSemester().getSemester());
  }

  @Override
  @Transactional(readOnly = true)
  public List<ApplicationFormResponse> getAllApplicationForms() {
    List<ApplicationForm> forms =
        applicationFormRepository.findAllWithSemesterOrderBySemesterDesc();
    List<ApplicationFormResponse> result = applicationFormMapper.toResponseList(forms);

    log.info("[ApplicationForm] 모집 공고 내림차순 전체 조회 완료 - count={}", result.size());
    return result;
  }

  @Override
  @Transactional(readOnly = true)
  public List<ApplicationFormSummaryResponse> getApplicationFormSummariesForQuestionRegistration() {
    LocalDateTime now = LocalDateTime.now();

    List<ApplicationForm> forms =
        applicationFormRepository
            .findAllAvailableForQuestionRegistrationWithSemesterOrderBySemesterDesc(now);

    List<ApplicationFormSummaryResponse> result =
        applicationFormMapper.toSummaryResponseList(forms);

    log.info("[ApplicationForm] 질문 등록용 모집 공고 제목 목록 조회 완료 - count={}", result.size());
    return result;
  }

  private void validateDateRange(ApplicationFormUpsertRequest request) {
    LocalDateTime openAt = request.getOpenAt();
    LocalDateTime closeAt = request.getCloseAt();
    LocalDateTime applicationResultAt = request.getApplicationResultAt();
    LocalDateTime interviewScheduleConfirmedAt = request.getInterviewScheduleConfirmedAt();
    LocalDateTime finalResultAt = request.getFinalResultAt();

    if (!openAt.isBefore(closeAt)) {
      log.info(
          "[ApplicationForm] 날짜 검증 실패 - openAt >= closeAt - openAt={}, closeAt={}",
          openAt,
          closeAt);
      throw new CustomException(ApplicationFormErrorCode.INVALID_DATE_RANGE);
    }

    if (applicationResultAt.isBefore(closeAt)) {
      log.info(
          "[ApplicationForm] 날짜 검증 실패 - applicationResultAt < closeAt - applicationResultAt={}, closeAt={}",
          applicationResultAt,
          closeAt);
      throw new CustomException(ApplicationFormErrorCode.INVALID_DATE_RANGE);
    }

    if (interviewScheduleConfirmedAt.isBefore(applicationResultAt)) {
      log.info(
          "[ApplicationForm] 날짜 검증 실패 - interviewScheduleConfirmedAt < applicationResultAt - interviewScheduleConfirmedAt={}, applicationResultAt={}",
          interviewScheduleConfirmedAt,
          applicationResultAt);
      throw new CustomException(ApplicationFormErrorCode.INVALID_DATE_RANGE);
    }

    if (finalResultAt.isBefore(interviewScheduleConfirmedAt)) {
      log.info(
          "[ApplicationForm] 날짜 검증 실패 - finalResultAt < interviewScheduleConfirmedAt - finalResultAt={}, interviewScheduleConfirmedAt={}",
          finalResultAt,
          interviewScheduleConfirmedAt);
      throw new CustomException(ApplicationFormErrorCode.INVALID_DATE_RANGE);
    }
  }

  private void validateNoOverlappedFormForCreate(ApplicationFormUpsertRequest request) {
    if (applicationFormRepository.existsOverlappedApplicationForm(
        request.getOpenAt(), request.getFinalResultAt())) {
      log.info(
          "[ApplicationForm] 기간 겹침: create 불가 - openAt={}, finalResultAt={}",
          request.getOpenAt(),
          request.getFinalResultAt());
      throw new CustomException(ApplicationFormErrorCode.DATE_RANGE_OVERLAPPED);
    }
  }

  private void validateNoOverlappedFormForUpdate(
      Long excludeFormId, ApplicationFormUpsertRequest request) {
    if (applicationFormRepository.existsOverlappedApplicationFormExcludingId(
        excludeFormId, request.getOpenAt(), request.getFinalResultAt())) {
      log.info(
          "[ApplicationForm] 기간 겹침: update 불가 - formId={}, openAt={}, finalResultAt={}",
          excludeFormId,
          request.getOpenAt(),
          request.getFinalResultAt());
      throw new CustomException(ApplicationFormErrorCode.DATE_RANGE_OVERLAPPED);
    }
  }

  @Override
  @Transactional(readOnly = true)
  public Long getCurrentApplicationSemester() {
    ApplicationForm current = getCurrentApplicationForm();
    return current.getSemester().getSemester();
  }

  @Override
  @Transactional(readOnly = true)
  public Long getCurrentApplicationFormId() {
    ApplicationForm current = getCurrentApplicationForm();
    return current.getId();
  }

  @Override
  @Transactional(readOnly = true)
  public ApplicationForm getCurrentApplicationForm() {
    LocalDateTime now = LocalDateTime.now();
    ApplicationForm current =
        applicationFormRepository
            .findCurrentApplicationForm(now)
            .orElseThrow(
                () -> {
                  log.info("[ApplicationForm] 현재 진행중 모집 공고 없음 - now={}", now);
                  return new CustomException(
                      ApplicationFormErrorCode.NOT_FOUND_CURRENT_APPLICATION_FORM);
                });

    log.info(
        "[ApplicationForm] 현재 진행중 모집 공고 조회 성공 - formId={}, semester={}, openAt={}, finalResultAt={}",
        current.getId(),
        current.getSemester().getSemester(),
        current.getOpenAt(),
        current.getFinalResultAt());

    return current;
  }

  @Override
  @Transactional(readOnly = true)
  public ApplicationFormResponse getCurrentApplicationFormResponse() {
    ApplicationForm current = getCurrentApplicationForm();
    return applicationFormMapper.toResponse(current);
  }
}
