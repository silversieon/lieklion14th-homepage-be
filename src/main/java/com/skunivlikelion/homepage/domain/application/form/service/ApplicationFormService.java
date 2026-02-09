/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.application.form.service;

import java.util.List;

import com.skunivlikelion.homepage.domain.application.form.dto.request.ApplicationFormUpsertRequest;
import com.skunivlikelion.homepage.domain.application.form.dto.response.ApplicationFormResponse;
import com.skunivlikelion.homepage.domain.application.form.dto.response.ApplicationFormSummaryResponse;
import com.skunivlikelion.homepage.domain.application.form.entity.ApplicationForm;

/**
 * 멋쟁이사자처럼 홈페이지 모집 공고 일정 관련 Service interface 입니다.
 *
 * @author Kim Na Kyung
 * @see com.skunivlikelion.homepage.domain.application.form.entity.ApplicationForm
 * @see com.skunivlikelion.homepage.domain.application.form.controller.ApplicationFormController
 * @since 2026.01.20
 */
public interface ApplicationFormService {

  /**
   * [ 모집 공고 등록 메서드 ]
   *
   * @param request 모집 공고 정보를 담은 요청 DTO
   * @return 등록된 모집 공고 정보를 담은 응답 DTO
   */
  ApplicationFormResponse createApplicationForm(ApplicationFormUpsertRequest request);

  /**
   * [ 모집 공고 수정 메서드 ]
   *
   * @param applicationFormId 수정할 모집 공고의 식별자
   * @param request 수정할 모집 공고 정보를 담은 요청 DTO
   * @return 수정된 모집 공고 정보를 담은 응답 DTO
   */
  ApplicationFormResponse updateApplicationForm(
      Long applicationFormId, ApplicationFormUpsertRequest request);

  /**
   * [ 모집 공고 삭제 메서드 ]
   *
   * @param applicationFormId 삭제할 모집 공고의 식별자
   */
  void deleteApplicationForm(Long applicationFormId);

  /**
   * [ 모집 공고 전체 조회 메서드 ]
   *
   * @return 모집 공고 응답 DTO 리스트
   */
  List<ApplicationFormResponse> getAllApplicationForms();

  /**
   * [ 질문 등록용 모집 공고 제목 목록 조회 메서드 ] 질문이 아직 등록되지 않았고(closeAt > now), 질문 등록이 가능한 모집 공고만 조회 (질문 등록 화면에서
   * 공고 선택 토글에 사용)
   *
   * @return 모집 공고 요약(title, closeAt) 리스트
   */
  List<ApplicationFormSummaryResponse> getApplicationFormSummariesForQuestionRegistration();

  /**
   * [ 현재 진행중인 모집 공고 조회 메서드 (Response용) ]
   *
   * <p>진행중 기준: now >= openAt AND now <= finalResultAt
   *
   * @return 현재 진행중인 모집 공고 응답
   */
  ApplicationFormResponse getCurrentApplicationFormResponse();

  /**
   * [ 현재 진행중인 모집 공고 기수 조회 메서드 ] 현재 시점 기준으로 진행중인 모집 공고의 기수를 반환
   *
   * @return 현재 진행중인 모집 공고의 기수 값
   */
  Long getCurrentApplicationSemester();

  /**
   * [ 현재 진행중인 모집 공고 ID 조회 메서드 ] 현재 시점 기준으로 진행중인 모집 공고의 ApplicationForm 식별자를 반환
   *
   * @return 현재 진행중인 모집 공고의 ApplicationForm ID
   */
  Long getCurrentApplicationFormId();

  /**
   * [ 현재 진행중인 모집 공고 조회 메서드 ]
   *
   * <p>진행중 기준: now >= openAt AND now <= finalResultAt
   *
   * @return 현재 진행중인 모집 공고 엔티티
   */
  ApplicationForm getCurrentApplicationForm();
}
