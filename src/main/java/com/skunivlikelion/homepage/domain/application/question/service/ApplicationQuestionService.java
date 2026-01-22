/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.application.question.service;

import com.skunivlikelion.homepage.domain.application.question.dto.request.ApplicationQuestionUpsertRequest;
import com.skunivlikelion.homepage.domain.application.question.dto.response.ApplicationQuestionGetResponse;
import com.skunivlikelion.homepage.domain.application.question.dto.response.ApplicationQuestionUpsertResponse;
import com.skunivlikelion.homepage.domain.application.question.dto.response.ApplicationSummaryListResponse;
import com.skunivlikelion.homepage.domain.common.enums.Track;

public interface ApplicationQuestionService {

  /**
   * [ 지원서 질문 등록 메서드 ] 특정 기수(모집 공고) 트랙별 질문들 등록 - ApplicationForm openAt 이전만 가능
   *
   * @param semester 등록할 기수 값
   * @param request 트랙별 질문 묶음 요청 DTO
   * @return 등록된 지원서 질문 정보를 담은 응답 DTO
   */
  ApplicationQuestionUpsertResponse createQuestions(
      Long semester, ApplicationQuestionUpsertRequest request);

  /**
   * [ 지원서 질문 수정 메서드 ] 특정 기수(모집 공고)에 등록된 지원서 질문 덮어쓰기형 수정 - ApplicationForm openAt 이전만 가능
   *
   * @param semester 수정할 기수 값
   * @param request 트랙별 질문 묶음 요청 DTO
   * @return 수정된 지원서 질문 정보를 담은 응답 DTO
   */
  ApplicationQuestionUpsertResponse updateQuestions(
      Long semester, ApplicationQuestionUpsertRequest request);

  /**
   * [ 지원서 질문 삭제 메서드 ] 특정 기수(모집 공고)에 등록된 지원서 질문 전체 삭제 - ApplicationForm openAt 이전만 가능
   *
   * @param semester 삭제할 기수 값
   */
  void deleteQuestions(Long semester);

  /**
   * [ 지원서 기수·트랙별 질문 조회 메서드 ] 특정 기수·트랙에 해당하는 지원서 질문 목록 오름차순 조회
   *
   * @param semester 조회할 기수 값
   * @param track 조회할 트랙
   * @return 기수·트랙별 지원서 질문 응답 DTO
   */
  ApplicationQuestionGetResponse getQuestionsBySemesterAndTrack(Long semester, Track track);

  /**
   * [ 지원서 목록 조회 메서드 ] 마감일(closeAt)을 기준으로 진행중 / 진행완료 목록으로 분리하여 기수 내림차순 조회
   *
   * @return 진행중 / 진행완료로 분리된 지원서 목록 응답 DTO
   */
  ApplicationSummaryListResponse getApplicationSummaries();
}
