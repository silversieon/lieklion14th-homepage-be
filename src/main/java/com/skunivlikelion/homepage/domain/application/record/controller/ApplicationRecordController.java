/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.application.record.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.skunivlikelion.homepage.domain.application.record.dto.request.ApplicationDraftSaveRequest;
import com.skunivlikelion.homepage.domain.application.record.dto.response.AdminApplicantListItem;
import com.skunivlikelion.homepage.domain.application.record.dto.response.ApplicantUserInfo;
import com.skunivlikelion.homepage.domain.application.record.dto.response.ApplicationAnswerItem;
import com.skunivlikelion.homepage.domain.application.record.dto.response.ApplicationRecordMeta;
import com.skunivlikelion.homepage.domain.application.record.dto.response.ApplicationRecordResponse;
import com.skunivlikelion.homepage.domain.common.enums.Track;
import com.skunivlikelion.homepage.global.page.response.InfiniteResponse;

import backend.boilerplate.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RequestMapping("/api")
@Tag(name = "ApplicationRecord", description = "사용자 지원서 관련 API")
public interface ApplicationRecordController {

  @Operation(
      summary = "[ 사용자 | 토큰 O | 지원서 최초 임시 저장 ]",
      description =
          """
              **RequestBody**  \n
              track + answer  \n

              **Returns**  \n
              임시 저장된 지원서 요약 정보
              """)
  @PostMapping("/v1/applications/records/first-draft")
  ResponseEntity<BaseResponse<ApplicationRecordMeta>> saveFirstDraft(
      @Valid @RequestBody ApplicationDraftSaveRequest request);

  @Operation(
      summary = "[ 사용자 | 토큰 O | 지원서 임시 저장 ]",
      description =
          """
              **RequestBody**  \n
              track + answer  \n

              **Returns**  \n
              임시 저장된 지원서 요약 정보
              """)
  @PutMapping("/v1/applications/records/draft")
  ResponseEntity<BaseResponse<ApplicationRecordMeta>> saveDraft(
      @Valid @RequestBody ApplicationDraftSaveRequest request);

  @Operation(
      summary = "[ 사용자 | 토큰 O | 지원서 제출 ]",
      description =
          """
              **RequestBody**  \n
              track + answer  \n

              **Returns**  \n
              제출된 지원서 요약 정보
              """)
  @PutMapping("/v1/applications/records/submit")
  ResponseEntity<BaseResponse<ApplicationRecordMeta>> submit(
      @Valid @RequestBody ApplicationDraftSaveRequest request);

  @Operation(
      summary = "[ 사용자 | 토큰 O | 내 인적사항 조회 ]",
      description =
          """
              **Returns**  \n
              사용자 인적사항 + (임시저장된 지원서가 있으면 track, 없으면 null)
              """)
  @GetMapping("/v1/applications/records/personal-info")
  ResponseEntity<BaseResponse<ApplicantUserInfo>> getMyDraftPersonalInfo();

  @Operation(
      summary = "[ 사용자 | 토큰 O | 내 임시 저장 지원서 트랙별 답변 조회 ]",
      description =
          """
              **Query Parameters**  \n
              track: 조회할 트랙  \n

              **Returns**  \n
              질문별 답변 목록 (questionId + answer)
              """)
  @GetMapping("/v1/applications/records/draft/answers")
  ResponseEntity<BaseResponse<List<ApplicationAnswerItem>>> getMyDraftAnswersByTrack(
      @RequestParam Track track);

  @Operation(
      summary = "[ 사용자 | 토큰 O | 내 제출 지원서 조회 ]",
      description =
          """
              **Returns**  \n
              선택된 트랙 + 공통 질문/답변 목록 + 트랙 질문/답변 목록
              """)
  @GetMapping("/v1/applications/records/submit")
  ResponseEntity<BaseResponse<ApplicationRecordResponse>> getMySubmittedApplicationAnswers();

  @Operation(
      summary = "[ 관리자 | 토큰 O | 기수별, 트랙별, 검색어 지원자 목록 무한스크롤 조회 ]",
      description =
          """
              **Query Parameters (선택)**  \n
              semester: 기수 값  \n
              track: 지원 트랙  \n
              search: 검색어  \n
              lastCursor: 마지막으로 받은 applicationRecordId \n
              size: 한 번에 가져올 개수 (default 10) \n

              **Returns**  \n
              제출된 지원서 목록 (무한스크롤)
              """)
  @GetMapping("/v1/admin/applications/records")
  ResponseEntity<BaseResponse<InfiniteResponse<AdminApplicantListItem>>> getApplicants(
      @RequestParam(required = false) Long semester,
      @RequestParam(required = false) Track track,
      @RequestParam(required = false) String search,
      @RequestParam(required = false) Long lastCursor,
      @RequestParam(defaultValue = "10") Integer size);

  @Operation(
      summary = "[ 관리자 | 토큰 O | 특정 지원자의 지원서 조회 ]",
      description =
          """
              **Path Parameters**  \n
              applicationRecordId: 지원서 식별자  \n

              **Returns**  \n
              사용자 정보 + 공통/트랙 질문/답변
              """)
  @GetMapping("/v1/admin/applications/records/{applicationRecordId}")
  ResponseEntity<BaseResponse<ApplicationRecordResponse>> getApplicationDetail(
      @PathVariable Long applicationRecordId);
}
