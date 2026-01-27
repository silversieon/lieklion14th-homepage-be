/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.application.form.service;

import java.util.List;

import com.skunivlikelion.homepage.domain.application.form.dto.request.ApplicationFormUpsertRequest;
import com.skunivlikelion.homepage.domain.application.form.dto.response.ApplicationFormResponse;
import com.skunivlikelion.homepage.domain.application.form.dto.response.ApplicationFormSummaryResponse;

public interface ApplicationFormService {

  /**
   * [ 모집 공고 등록 메서드 ] 특정 기수에 대한 모집 공고 생성하여 등록
   *
   * @param semester 등록할 기수 값
   * @param request 모집 공고 정보를 담은 요청 DTO
   * @return 등록된 모집 공고 정보를 담은 응답 DTO
   */
  ApplicationFormResponse createApplicationForm(
      Long semester, ApplicationFormUpsertRequest request);

  /**
   * [ 모집 공고 수정 메서드 ] 특정 기수의 모집 공고 정보 수정
   *
   * @param semester 수정할 기수 값
   * @param request 수정할 모집 공고 정보를 담은 요청 DTO
   * @return 수정된 모집 공고 정보를 담은 응답 DTO
   */
  ApplicationFormResponse updateApplicationForm(
      Long semester, ApplicationFormUpsertRequest request);

  /**
   * [ 모집 공고 삭제 메서드 ] 특정 기수의 모집 공고 삭제
   *
   * @param semester 삭제할 기수 값
   */
  void deleteApplicationForm(Long semester);

  /**
   * [ 모집 공고 기수별 조회 메서드 ] 특정 기수의 모집 공고 정보 조회
   *
   * @param semester 조회할 기수 값
   * @return 해당 기수의 모집 공고 정보를 담은 응답 DTO
   */
  ApplicationFormResponse getApplicationFormBySemester(Long semester);

  /**
   * [ 모집 공고 전체 조회 메서드 ] 등록된 모집 공고 내림차순 전체 목록 조회
   *
   * @return 모집 공고 응답 DTO 리스트
   */
  List<ApplicationFormResponse> getAllApplicationForms();

  /**
   * [ 질문 등록용 모집 공고 제목 목록 조회 메서드 ] - 질문이 아직 등록되지 않은 공고 & 마감되지 않은 공고(closeAt > now)만 조회 - 질문 등록 화면의
   * 공고 선택 토글에 사용
   *
   * @return 모집 공고 요약(title, closeAt) 리스트
   */
  List<ApplicationFormSummaryResponse> getApplicationFormSummariesForQuestionRegistration();

  /**
   * [ 현재 진행중인 모집 공고 기수 조회 메서드 ] 현재 시점 기준으로 진행중인 모집 공고의 기수를 반환
   *
   * <p>진행중 기준: now >= openAt AND now <= finalResultAt
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
}
