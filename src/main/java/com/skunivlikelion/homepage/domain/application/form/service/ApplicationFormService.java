/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.application.form.service;

import java.util.List;

import com.skunivlikelion.homepage.domain.application.form.dto.request.ApplicationFormUpsertRequest;
import com.skunivlikelion.homepage.domain.application.form.dto.response.ApplicationFormResponse;

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
}
