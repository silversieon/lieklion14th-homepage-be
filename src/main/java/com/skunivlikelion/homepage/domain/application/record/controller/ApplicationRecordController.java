/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.application.record.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.skunivlikelion.homepage.domain.application.record.dto.request.ApplicationDraftSaveRequest;
import com.skunivlikelion.homepage.domain.application.record.dto.response.AdminApplicantListResponse;
import com.skunivlikelion.homepage.domain.application.record.dto.response.AdminApplicationDetailResponse;
import com.skunivlikelion.homepage.domain.application.record.dto.response.ApplicationAnswersGetResponse;
import com.skunivlikelion.homepage.domain.application.record.dto.response.ApplicationDraftSaveResponse;
import com.skunivlikelion.homepage.domain.application.record.dto.response.ApplicationSubmitDateResponse;
import com.skunivlikelion.homepage.domain.application.record.dto.response.ApplicationSubmitResponse;
import com.skunivlikelion.homepage.domain.common.enums.Track;

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
              **Parameters**  \n
              semester: 지원할 기수 값  \n
              request: track + answer  \n

              **Returns**  \n
              임시 저장된 지원서 요약 정보
              """)
  @PostMapping("/v1/applications/draft/{semester}")
  ResponseEntity<BaseResponse<ApplicationDraftSaveResponse>> saveFirstDraft(
      @PathVariable @Positive Long semester,
      @Valid @RequestBody ApplicationDraftSaveRequest request);

  @Operation(
      summary = "[ 사용자 | 토큰 O | 지원서 임시 저장 ]",
      description =
          """
              **Parameters**  \n
              applicationRecordId: 지원서 id  \n
              request: track + answer  \n

              **Returns**  \n
              임시 저장된 지원서 요약 정보
              """)
  @PutMapping("/v1/applications/draft/{applicationRecordId}")
  ResponseEntity<BaseResponse<ApplicationDraftSaveResponse>> saveDraft(
      @PathVariable Long applicationRecordId,
      @Valid @RequestBody ApplicationDraftSaveRequest request);

  @Operation(
      summary = "[ 사용자 | 토큰 O | 지원서 제출 ]",
      description =
          """
              **Parameters**  \n
              semester: 제출할 기수 값  \n
              request: track + answer  \n

              **Returns**  \n
              제출된 지원서 요약 정보
              """)
  @PutMapping("/v1/applications/submit/{semester}")
  ResponseEntity<BaseResponse<ApplicationSubmitResponse>> submit(
      @PathVariable @Positive Long semester,
      @Valid @RequestBody ApplicationDraftSaveRequest request);

  @Operation(
      summary = "[ 사용자 | 토큰 O | 본인 임시저장/제출 지원서 조회 ]",
      description =
          """
              **Parameters**  \n
              semester: 조회할 기수 값  \n

              **Returns**  \n
              선택된 트랙 + (공통/트랙) 질문 오름차순 답변 목록
              """)
  @GetMapping("/v1/applications/answers/{semester}")
  ResponseEntity<BaseResponse<ApplicationAnswersGetResponse>> getMyApplicationAnswers(
      @PathVariable @Positive Long semester);

  @Operation(
      summary = "[ 사용자 | 토큰 O | 지원서 제출 일자 조회 ]",
      description =
          """
              **Parameters**  \n
              semester: 조회할 기수 값  \n

              **Returns**  \n
              해당 기수 지원서의 제출 여부 및 제출 일시
              """)
  @GetMapping("/v1/applications/submit/date/{semester}")
  ResponseEntity<BaseResponse<ApplicationSubmitDateResponse>> getMySubmitDate(
      @PathVariable @Positive Long semester);

  @Operation(
      summary = "[ 관리자 | 토큰 O | 기수별, 트랙별, 검색어 지원자 목록 조회 ]",
      description =
          """
              **Query Parameters(선택)**  \n
              semester: 기수 값  \n
              track: 지원 트랙  \n
              search: 검색어  \n

              **Returns**  \n
              제출된 지원서 목록
              """)
  @GetMapping("/v1/admin/applications")
  ResponseEntity<BaseResponse<AdminApplicantListResponse>> getApplicants(
      @RequestParam(required = false) Long semester,
      @RequestParam(required = false) Track track,
      @RequestParam(required = false) String search);

  @Operation(
      summary = "[ 관리자 | 토큰 O | 특정 지원자의 지원서 조회 ]",
      description =
          """
              **Path Parameters**  \n
              applicationRecordId: 지원서 식별자  \n

              **Returns**  \n
              사용자 정보 + 공통/트랙 질문/답변
              """)
  @GetMapping("/v1/admin/applications/{applicationRecordId}")
  ResponseEntity<BaseResponse<AdminApplicationDetailResponse>> getApplicationDetail(
      @PathVariable Long applicationRecordId);
}
