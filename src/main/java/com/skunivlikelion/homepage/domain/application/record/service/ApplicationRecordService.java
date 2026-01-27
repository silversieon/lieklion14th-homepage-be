/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.application.record.service;

import com.skunivlikelion.homepage.domain.application.record.dto.request.ApplicationDraftSaveRequest;
import com.skunivlikelion.homepage.domain.application.record.dto.response.AdminApplicantListResponse;
import com.skunivlikelion.homepage.domain.application.record.dto.response.AdminApplicationDetailResponse;
import com.skunivlikelion.homepage.domain.application.record.dto.response.ApplicationAnswersGetResponse;
import com.skunivlikelion.homepage.domain.application.record.dto.response.ApplicationDraftSaveResponse;
import com.skunivlikelion.homepage.domain.application.record.dto.response.ApplicationSubmitDateResponse;
import com.skunivlikelion.homepage.domain.application.record.dto.response.ApplicationSubmitResponse;
import com.skunivlikelion.homepage.domain.common.enums.Track;

public interface ApplicationRecordService {

  /**
   * [ 지원서 최초 임시저장(first draft) ] - record 생성 + track 저장 - 공통 질문 + 선택 트랙 질문에 대한 Answer 엔티티를 전부
   * 생성(content="")
   *
   * @param semester 지원할 기수 값
   * @param request step, track 정보를 포함한 최초 임시저장 요청 DTO
   * @return 임시저장된 지원서 요약 정보
   */
  ApplicationDraftSaveResponse saveFirstDraft(Long semester, ApplicationDraftSaveRequest request);

  /**
   * [ 지원서 임시저장(draft) ] - recordId 기반으로 저장 - 트랙 변경 시: 이전 트랙 답변 삭제 + 새 트랙 답변 엔티티 생성(content="") -
   * request로 들어온 답변을 그대로 content에 덮어쓰기
   *
   * @param applicationRecordId 지원서 식별자
   * @param request step, track, (공통/트랙별) 답변을 포함한 임시저장 요청 DTO
   * @return 임시저장된 지원서 요약 정보
   */
  ApplicationDraftSaveResponse saveDraft(
      Long applicationRecordId, ApplicationDraftSaveRequest request);

  /**
   * [ 지원서 제출(submit) 메서드 ] 특정 기수의 draft 지원서를 제출 상태로 변경
   *
   * @param semester 제출할 기수 값
   * @return 제출된 지원서 정보를 담은 응답 DTO
   */
  ApplicationSubmitResponse submit(Long semester, ApplicationDraftSaveRequest request);

  /**
   * [ 사용자 본인의 임시저장/제출 지원서 조회 ] 특정 기수에 대해 submitted 우선, 없으면 draft 조회
   *
   * @param semester 조회할 기수 값
   * @return 선택된 트랙 + 공통/트랙 질문 오름차순 답변 목록
   */
  ApplicationAnswersGetResponse getMyApplicationAnswers(Long semester);

  /**
   * [ 사용자 본인의 지원서 제출 일자 조회 ] 특정 기수에 대해 사용자가 제출한 지원서의 제출 여부 및 제출 일시를 조회
   *
   * @param semester 조회할 기수 값
   * @return 제출 여부 및 제출 일시를 담은 응답 DTO
   */
  ApplicationSubmitDateResponse getMySubmitDate(Long semester);

  /**
   * [ 제출된 지원서 지원자 목록 조회 ]
   *
   * @param semester (optional) 기수
   * @param track (optional) 트랙
   * @param search (optional) 검색어(이름/학과/학번)
   * @return 제출된 지원서 목록
   */
  AdminApplicantListResponse getApplicants(Long semester, Track track, String search);

  /**
   * [ 특정 지원자 지원서 조회 ]
   *
   * @param applicationRecordId 지원서 식별자
   * @return 사용자 정보 + 공통/트랙 질문/답변
   */
  AdminApplicationDetailResponse getApplicationDetail(Long applicationRecordId);
}
