/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.application.form.controller;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
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

import backend.boilerplate.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Validated
@RequestMapping("/api")
@Tag(name = "ApplicationForm", description = "모집 공고 일정 관리 API")
public interface ApplicationFormController {

  @Operation(
      summary = "[ 관리자 | 토큰 O | 모집 공고 등록 ]",
      description =
          """
              **Parameters**  \n
              semester: 등록할 기수 값  \n
              openAt: 모집 시작 일시  \n
              closeAt: 모집 마감 일시  \n
              applicationResultAt: 서류 결과 발표 일시  \n
              finalResultAt: 최종 결과 발표 일시  \n

              **Returns**  \n
              등록된 모집 공고 정보
              """)
  @PostMapping("/v1/admin/applications/forms/{semester}")
  ResponseEntity<BaseResponse<ApplicationFormResponse>> createApplicationForm(
      @PathVariable @Positive Long semester,
      @Valid @RequestBody ApplicationFormUpsertRequest request);

  @Operation(
      summary = "[ 관리자 | 토큰 O | 모집 공고 수정 ]",
      description =
          """
              **Parameters**  \n
              semester: 수정할 기수 값  \n
              openAt: 모집 시작 일시  \n
              closeAt: 모집 마감 일시  \n
              applicationResultAt: 서류 결과 발표 일시  \n
              finalResultAt: 최종 결과 발표 일시  \n

              **Returns**  \n
              수정된 모집 공고 정보
              """)
  @PutMapping("/v1/admin/applications/forms/{semester}")
  ResponseEntity<BaseResponse<ApplicationFormResponse>> updateApplicationForm(
      @PathVariable @Positive Long semester,
      @Valid @RequestBody ApplicationFormUpsertRequest request);

  @Operation(
      summary = "[ 관리자 | 토큰 O | 모집 공고 삭제 ]",
      description =
          """
              **Parameters**  \n
              semester: 삭제할 기수 값  \n

              **Returns**  \n
              모집 공고 삭제 성공/실패 여부
              """)
  @DeleteMapping("/v1/admin/applications/forms/{semester}")
  ResponseEntity<BaseResponse<Void>> deleteApplicationForm(@PathVariable @Positive Long semester);

  @Operation(
      summary = "[ 관리자 | 토큰 O | 모집 공고 기수별 조회 ]",
      description =
          """
              **Parameters**  \n
              semester: 조회할 기수 값  \n

              **Returns**  \n
              해당 기수의 모집 공고 정보
              """)
  @GetMapping("/v1/admin/applications/forms/{semester}")
  ResponseEntity<BaseResponse<ApplicationFormResponse>> getApplicationFormBySemester(
      @PathVariable @Positive Long semester);

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
