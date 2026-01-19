/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.application.form.controller;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import com.skunivlikelion.homepage.domain.application.form.dto.request.ApplicationFormUpsertRequest;
import com.skunivlikelion.homepage.domain.application.form.dto.response.ApplicationFormResponse;
import com.skunivlikelion.homepage.domain.application.form.service.ApplicationFormService;

import backend.boilerplate.response.BaseResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class ApplicationFormControllerImpl implements ApplicationFormController {

  private final ApplicationFormService applicationFormService;

  @Override
  public ResponseEntity<BaseResponse<ApplicationFormResponse>> createApplicationForm(
      @Positive Long semester, @Valid ApplicationFormUpsertRequest request) {
    ApplicationFormResponse response =
        applicationFormService.createApplicationForm(semester, request);
    return ResponseEntity.status(201)
        .body(BaseResponse.success(201, "모집 공고 등록에 성공했습니다.", response));
  }

  @Override
  public ResponseEntity<BaseResponse<ApplicationFormResponse>> updateApplicationForm(
      @Positive Long semester, @Valid ApplicationFormUpsertRequest request) {
    ApplicationFormResponse response =
        applicationFormService.updateApplicationForm(semester, request);
    return ResponseEntity.status(200)
        .body(BaseResponse.success(200, "모집 공고 수정에 성공했습니다.", response));
  }

  @Override
  public ResponseEntity<BaseResponse<Void>> deleteApplicationForm(@Positive Long semester) {
    applicationFormService.deleteApplicationForm(semester);
    return ResponseEntity.status(200).body(BaseResponse.success(200, "모집 공고 삭제에 성공했습니다.", null));
  }

  @Override
  public ResponseEntity<BaseResponse<ApplicationFormResponse>> getApplicationFormBySemester(
      @Positive Long semester) {
    ApplicationFormResponse response =
        applicationFormService.getApplicationFormBySemester(semester);
    return ResponseEntity.status(200)
        .body(BaseResponse.success(200, "모집 공고 기수별 조회에 성공했습니다.", response));
  }

  @Override
  public ResponseEntity<BaseResponse<List<ApplicationFormResponse>>> getAllApplicationForms() {
    List<ApplicationFormResponse> result = applicationFormService.getAllApplicationForms();
    return ResponseEntity.status(200)
        .body(BaseResponse.success(200, "모집 공고 내림차순 전체 조회에 성공했습니다.", result));
  }
}
