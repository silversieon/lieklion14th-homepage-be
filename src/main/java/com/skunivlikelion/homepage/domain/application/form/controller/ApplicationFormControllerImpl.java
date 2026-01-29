/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.application.form.controller;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.skunivlikelion.homepage.domain.application.form.dto.request.ApplicationFormUpsertRequest;
import com.skunivlikelion.homepage.domain.application.form.dto.response.ApplicationFormResponse;
import com.skunivlikelion.homepage.domain.application.form.dto.response.ApplicationFormSummaryResponse;
import com.skunivlikelion.homepage.domain.application.form.service.ApplicationFormService;

import backend.boilerplate.response.BaseResponse;
import lombok.RequiredArgsConstructor;

@Validated
@RestController
@RequiredArgsConstructor
public class ApplicationFormControllerImpl implements ApplicationFormController {

  private final ApplicationFormService applicationFormService;

  @Override
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<BaseResponse<ApplicationFormResponse>> createApplicationForm(
      @Valid @RequestBody ApplicationFormUpsertRequest request) {

    ApplicationFormResponse response = applicationFormService.createApplicationForm(request);
    return ResponseEntity.status(201)
        .body(BaseResponse.success(201, "지원 일정 등록에 성공했습니다.", response));
  }

  @Override
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<BaseResponse<ApplicationFormResponse>> updateApplicationForm(
      @PathVariable @Positive Long applicationFormId,
      @Valid @RequestBody ApplicationFormUpsertRequest request) {

    ApplicationFormResponse response =
        applicationFormService.updateApplicationForm(applicationFormId, request);

    return ResponseEntity.status(200)
        .body(BaseResponse.success(200, "지원 일정 수정에 성공했습니다.", response));
  }

  @Override
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<BaseResponse<Void>> deleteApplicationForm(
      @PathVariable @Positive Long applicationFormId) {

    applicationFormService.deleteApplicationForm(applicationFormId);
    return ResponseEntity.status(200).body(BaseResponse.success(200, "지원 일정 삭제에 성공했습니다.", null));
  }

  @Override
  public ResponseEntity<BaseResponse<ApplicationFormResponse>> getCurrentApplicationForm() {

    ApplicationFormResponse response = applicationFormService.getCurrentApplicationFormResponse();
    return ResponseEntity.status(200)
        .body(BaseResponse.success(200, "진행중 지원 일정 조회에 성공했습니다.", response));
  }

  @Override
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<BaseResponse<List<ApplicationFormResponse>>> getAllApplicationForms() {
    List<ApplicationFormResponse> result = applicationFormService.getAllApplicationForms();
    return ResponseEntity.status(200)
        .body(BaseResponse.success(200, "모집 공고 내림차순 전체 조회에 성공했습니다.", result));
  }

  @Override
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<BaseResponse<List<ApplicationFormSummaryResponse>>>
      getApplicationFormSummariesForQuestionRegistration() {

    List<ApplicationFormSummaryResponse> result =
        applicationFormService.getApplicationFormSummariesForQuestionRegistration();

    return ResponseEntity.status(200)
        .body(BaseResponse.success(200, "모집 공고 제목 목록 조회에 성공했습니다.", result));
  }
}
