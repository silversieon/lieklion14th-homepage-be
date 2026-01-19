/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.application.form.service;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.EntityManager;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.skunivlikelion.homepage.domain.application.form.dto.request.ApplicationFormUpsertRequest;
import com.skunivlikelion.homepage.domain.application.form.dto.response.ApplicationFormResponse;
import com.skunivlikelion.homepage.domain.application.form.entity.ApplicationForm;
import com.skunivlikelion.homepage.domain.application.form.exception.ApplicationFormErrorCode;
import com.skunivlikelion.homepage.domain.application.form.mapper.ApplicationFormMapper;
import com.skunivlikelion.homepage.domain.application.form.repository.ApplicationFormRepository;
import com.skunivlikelion.homepage.domain.semester.entity.Semester;
import com.skunivlikelion.homepage.domain.semester.repository.SemesterRepository;

import backend.boilerplate.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ApplicationFormServiceImpl implements ApplicationFormService {

  private final ApplicationFormRepository applicationFormRepository;
  private final SemesterRepository semesterRepository;
  private final EntityManager entityManager;
  private final ApplicationFormMapper applicationFormMapper;

  @Override
  public ApplicationFormResponse createApplicationForm(
      Long semester, ApplicationFormUpsertRequest request) {
    validateSemesterExists(semester);
    validateDateRange(request);

    if (applicationFormRepository.existsBySemester_Semester(semester)) {
      log.info("[ApplicationForm] 모집 공고 등록 실패 - 이미 존재하는 모집 공고 - semester={}", semester);
      throw new CustomException(ApplicationFormErrorCode.ALREADY_EXIST_APPLICATION_FORM);
    }

    Semester semesterRef = entityManager.getReference(Semester.class, semester);
    ApplicationForm form = applicationFormMapper.toEntity(semesterRef, request);

    ApplicationForm saved = applicationFormRepository.save(form);
    log.info("[ApplicationForm] 모집 공고 등록 완료 - semester={}, id={}", semester, saved.getId());

    return applicationFormMapper.toResponse(saved);
  }

  @Override
  public ApplicationFormResponse updateApplicationForm(
      Long semester, ApplicationFormUpsertRequest request) {
    validateSemesterExists(semester);
    validateDateRange(request);

    ApplicationForm found =
        applicationFormRepository
            .findBySemester_Semester(semester)
            .orElseThrow(
                () -> {
                  log.warn("[ApplicationForm] 모집 공고 수정 실패: 모집 공고 없음 - semester={}", semester);
                  return new CustomException(ApplicationFormErrorCode.NOT_FOUND_APPLICATION_FORM);
                });

    found.update(
        request.getOpenAt(),
        request.getCloseAt(),
        request.getApplicationResultAt(),
        request.getFinalResultAt());

    log.info("[ApplicationForm] 모집 공고 수정 완료 - semester={}, id={}", semester, found.getId());

    return applicationFormMapper.toResponse(found);
  }

  @Override
  public void deleteApplicationForm(Long semester) {
    validateSemesterExists(semester);

    ApplicationForm found =
        applicationFormRepository
            .findBySemester_Semester(semester)
            .orElseThrow(
                () -> {
                  log.warn("[ApplicationForm] 모집 공고 삭제 실패: 모집 공고 없음 - semester={}", semester);
                  return new CustomException(ApplicationFormErrorCode.NOT_FOUND_APPLICATION_FORM);
                });

    applicationFormRepository.delete(found);
    log.info("[ApplicationForm] 모집 공고 삭제 완료 - semester={}, id={}", semester, found.getId());
  }

  @Override
  @Transactional(readOnly = true)
  public ApplicationFormResponse getApplicationFormBySemester(Long semester) {
    validateSemesterExists(semester);

    ApplicationForm found =
        applicationFormRepository
            .findBySemester_Semester(semester)
            .orElseThrow(
                () -> {
                  log.warn("[ApplicationForm] 모집 공고 기수별 조회 실패: 모집 공고 없음 - semester={}", semester);
                  return new CustomException(ApplicationFormErrorCode.NOT_FOUND_APPLICATION_FORM);
                });

    log.info("[ApplicationForm] 모집 공고 기수별 조회 완료 - semester={}, id={}", semester, found.getId());
    return applicationFormMapper.toResponse(found);
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

  private void validateSemesterExists(Long semester) {
    if (semester == null) {
      log.warn("[ApplicationForm] 기수 검증 실패 - semester=null");
      throw new CustomException(ApplicationFormErrorCode.NOT_FOUND_SEMESTER);
    }

    if (!semesterRepository.existsById(semester)) {
      log.warn("[ApplicationForm] 기수 검증 실패 - 존재하지 않는 기수 - semester={}", semester);
      throw new CustomException(ApplicationFormErrorCode.NOT_FOUND_SEMESTER);
    }
  }

  private void validateDateRange(ApplicationFormUpsertRequest request) {
    LocalDateTime openAt = request.getOpenAt();
    LocalDateTime closeAt = request.getCloseAt();
    LocalDateTime applicationResultAt = request.getApplicationResultAt();
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

    if (finalResultAt.isBefore(applicationResultAt)) {
      log.info(
          "[ApplicationForm] 날짜 검증 실패 - finalResultAt < applicationResultAt - finalResultAt={}, applicationResultAt={}",
          finalResultAt,
          applicationResultAt);
      throw new CustomException(ApplicationFormErrorCode.INVALID_DATE_RANGE);
    }
  }
}
