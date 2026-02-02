/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.application.form.controller;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.skunivlikelion.homepage.domain.application.form.dto.request.ApplicationFormUpsertRequest;
import com.skunivlikelion.homepage.domain.application.form.dto.response.ApplicationFormResponse;
import com.skunivlikelion.homepage.domain.application.form.dto.response.ApplicationFormSummaryResponse;
import com.skunivlikelion.homepage.global.common.BaseResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 멋쟁이사자처럼 홈페이지 모집 공고 일정 관련 Controller interface 입니다.
 *
 * @since 2026.01.20
 * @see com.skunivlikelion.homepage.domain.application.form.entity.ApplicationForm
 * @see com.skunivlikelion.homepage.domain.application.form.service.ApplicationFormService
 * @author Kim Na Kyung
 * @version latest: 1
 */
@RequestMapping("/api")
@Tag(name = "ApplicationForm", description = "모집 공고 일정 관리 API")
public interface ApplicationFormController {

  @Operation(
      summary = "[ 관리자 | 토큰 O | 지원 일정 등록 ]",
      description =
          """
              **RequestBody**  \n
              semester: 등록할 기수 값  \n
              openAt: 모집 시작 일시  \n
              closeAt: 모집 마감 일시  \n
              applicationResultAt: 서류 결과 발표 일시  \n
              interviewScheduleConfirmedAt: 면접 일정 확정 일시    \n
              finalResultAt: 최종 결과 발표 일시  \n

              - openAt < closeAt ≤ applicationResultAt ≤ interviewScheduleConfirmedAt ≤ finalResultAt \n

              **Returns**  \n
              등록된 모집 공고 정보
              """)
  @PostMapping("/v1/admin/applications/forms")
  ResponseEntity<BaseResponse<ApplicationFormResponse>> createApplicationForm(
      @Valid @RequestBody ApplicationFormUpsertRequest request);

  @Operation(
      summary = "[ 관리자 | 토큰 O | 지원 일정 수정 ]",
      description =
          """
              **Parameters**  \n
              application-form-id: 수정할 모집 공고 ID  \n

              **RequestBody**  \n
              openAt: 모집 시작 일시  \n
              closeAt: 모집 마감 일시  \n
              applicationResultAt: 서류 결과 발표 일시  \n
              interviewScheduleConfirmedAt: 면접 일정 확정 일시    \n
              finalResultAt: 최종 결과 발표 일시  \n

              **Returns**  \n
              수정된 모집 공고 정보
              """)
  @PutMapping("/v1/admin/applications/forms/{application-form-id}")
  ResponseEntity<BaseResponse<ApplicationFormResponse>> updateApplicationForm(
      @PathVariable("application-form-id") @Positive Long applicationFormId,
      @Valid @RequestBody ApplicationFormUpsertRequest request);

  @Operation(
      summary = "[ 관리자 | 토큰 O | 지원 일정 삭제 ]",
      description =
          """
              **Parameters**  \n
              application-form-id: 삭제할 모집 공고 ID  \n

              **Returns**  \n
              지원 일정 삭제 성공/실패 여부
              """)
  @DeleteMapping("/v1/admin/applications/forms/{application-form-id}")
  ResponseEntity<BaseResponse<Void>> deleteApplicationForm(
      @PathVariable("application-form-id") @Positive Long applicationFormId);

  @Operation(
      summary = "[ 사용자 | 토큰 X | 진행중 지원 일정 조회 ]",
      description =
          """
              **Returns**  \n
              현재 진행중인 모집 공고 정보
              """)
  @GetMapping("/v1/applications/current-forms")
  ResponseEntity<BaseResponse<ApplicationFormResponse>> getCurrentApplicationForm();

  @Operation(
      summary = "[ 관리자 | 토큰 O | 모집 공고 내림차순 전체 조회 ]",
      description =
          """
              **Returns**  \n
              등록된 모집 공고 전체 목록
              """)
  @GetMapping("/v1/admin/applications/forms")
  ResponseEntity<BaseResponse<List<ApplicationFormResponse>>> getAllApplicationForms();

  @Operation(
      summary = "[ 관리자 | 토큰 O | 질문 미등록 모집 공고 목록 조회 ]",
      description =
          """
              **Returns**  \n
              진행중/진행완료 기수와 마감일 목록
              """)
  @GetMapping("/v1/admin/applications/forms/summaries")
  ResponseEntity<BaseResponse<List<ApplicationFormSummaryResponse>>>
      getApplicationFormSummariesForQuestionRegistration();
}
