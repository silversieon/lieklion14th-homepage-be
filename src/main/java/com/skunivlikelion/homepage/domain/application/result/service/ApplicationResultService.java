/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.application.result.service;

import com.skunivlikelion.homepage.domain.application.result.dto.response.AdminApplicationResultConfirmResponse;
import com.skunivlikelion.homepage.domain.application.result.dto.response.AdminDocumentResultUpdateResponse;
import com.skunivlikelion.homepage.domain.application.result.dto.response.MyInterviewResultResponse;

/**
 * 멋쟁이사자처럼 홈페이지 지원 결과 관련 Service interface 입니다.
 *
 * @author Kim Na Kyung
 * @see com.skunivlikelion.homepage.domain.application.result.controller.ApplicationResultController
 * @since 2026.01.29
 */
public interface ApplicationResultService {

  /**
   * [ 관리자 | 토큰 O | 특정 지원서 서류 합격 여부 수정 ]
   *
   * @param applicationRecordId 지원서 식별자
   * @param isDocumentPassed 서류 합격 여부
   * @return 수정 결과
   */
  AdminDocumentResultUpdateResponse updateDocumentResult(
      Long applicationRecordId, boolean isDocumentPassed);

  /**
   * [ 관리자 | 토큰 O | 특정 지원서 최종 합격 여부 확정 ]
   *
   * @param applicationRecordId 지원서 식별자
   * @param passed 최종 합격 여부
   * @return 면접 합격 여부를 담은 응답 객체
   */
  AdminApplicationResultConfirmResponse confirmDocumentResult(
      Long applicationRecordId, boolean passed);

  /**
   * [ 현재 사용자의 면접 합격 결과 조회 메서드 ] 현재 진행 중인 모집 공고이거나 최종 결과 발표일로부터 7일 이내인 경우에 한해 면접 합격 결과를 조회
   *
   * @return 면접 합격 결과 정보를 담은 객체
   */
  MyInterviewResultResponse getCurrentUserInterviewResult();
}
