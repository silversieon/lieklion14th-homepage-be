/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.application.record.service;

import java.util.List;

import com.skunivlikelion.homepage.domain.application.record.dto.request.ApplicationDraftSaveRequest;
import com.skunivlikelion.homepage.domain.application.record.dto.response.AdminApplicantListItem;
import com.skunivlikelion.homepage.domain.application.record.dto.response.ApplicantUserInfo;
import com.skunivlikelion.homepage.domain.application.record.dto.response.ApplicationAnswerItem;
import com.skunivlikelion.homepage.domain.application.record.dto.response.ApplicationRecordMeta;
import com.skunivlikelion.homepage.domain.application.record.dto.response.ApplicationRecordResponse;
import com.skunivlikelion.homepage.domain.common.enums.Track;
import com.skunivlikelion.homepage.global.page.response.InfiniteResponse;

/**
 * 멋쟁이사자처럼 홈페이지 모집 공고 일정 관련 Service interface 입니다.
 *
 * @see com.skunivlikelion.homepage.domain.application.form.entity.ApplicationForm
 * @see com.skunivlikelion.homepage.domain.application.form.controller.ApplicationFormController
 * @since 2026.01.27
 * @author Kim Na Kyung
 */
public interface ApplicationRecordService {

  /**
   * [ 지원서 최초 임시저장(first draft) ] record 생성 + track 저장
   *
   * @param request track, 답변을 포함한 최초 임시저장 요청 DTO
   * @return 임시저장된 지원서 요약 정보
   */
  ApplicationRecordMeta saveFirstDraft(ApplicationDraftSaveRequest request);

  /**
   * [ 지원서 임시저장(draft) ] recordId 기반으로 저장: request로 들어온 답변을 그대로 content에 덮어쓰기
   *
   * @param request track, 답변을 포함한 임시저장 요청 DTO
   * @return 임시저장된 지원서 요약 정보
   */
  ApplicationRecordMeta saveDraft(ApplicationDraftSaveRequest request);

  /**
   * [ 지원서 제출(submit) 메서드 ] draft가 없으면 생성 후 제출, 있으면 draft 업데이트 후 제출 처리
   *
   * @param request track, 답변을 포함한 제출 요청 DTO
   * @return 제출된 지원서 요약 정보
   */
  ApplicationRecordMeta submit(ApplicationDraftSaveRequest request);

  /**
   * [ 사용자 본인 인적사항 조회(draft personal info) ]
   *
   * @return 사용자 인적사항 + (draft track or null)
   */
  ApplicantUserInfo getMyDraftPersonalInfo();

  /**
   * [ 사용자 본인 임시 저장 지원서 트랙별 조회 ]현재 진행중인 모집 공고 기준 draft record가 없으면 빈 리스트 반환
   *
   * @param track 조회할 트랙
   * @return 질문별 답변 목록
   */
  List<ApplicationAnswerItem> getMyDraftAnswersByTrack(Track track);

  /**
   * [ 사용자 본인의 제출 지원서 조회 ] 현재 진행중인 모집 공고를 기준으로 사용자가 제출한 지원서를 조회
   *
   * @return 선택된 트랙 + 공통/트랙 질문 오름차순 답변 목록
   */
  ApplicationRecordResponse getMySubmittedApplicationAnswers();

  /**
   * [ 제출된 지원서 지원자 목록 무한스크롤 조회 ]
   *
   * @param semester (optional) 기수(semester 값)
   * @param track (optional) 지원 트랙
   * @param search (optional) 검색어(이름/학과/학번)
   * @param lastCursor (optional) 마지막으로 조회된 지원서 식별자(applicationRecordId)
   * @param size (optional) 한 번에 조회할 개수
   * @return 제출된 지원서 목록
   */
  InfiniteResponse<AdminApplicantListItem> getApplicants(
      Long semester, Track track, String search, Long lastCursor, Integer size);

  /**
   * [ 특정 지원자 지원서 조회 ]
   *
   * @param applicationRecordId 지원서 식별자
   * @return 사용자 정보 + 공통/트랙 질문/답변
   */
  ApplicationRecordResponse getApplicationDetail(Long applicationRecordId);
}
