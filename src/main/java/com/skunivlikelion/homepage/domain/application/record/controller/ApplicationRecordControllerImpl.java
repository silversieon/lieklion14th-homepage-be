/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.application.record.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.skunivlikelion.homepage.domain.application.record.dto.request.ApplicationDraftSaveRequest;
import com.skunivlikelion.homepage.domain.application.record.dto.response.AdminApplicantListResponse;
import com.skunivlikelion.homepage.domain.application.record.dto.response.AdminApplicationDetailResponse;
import com.skunivlikelion.homepage.domain.application.record.dto.response.ApplicationAnswersGetResponse;
import com.skunivlikelion.homepage.domain.application.record.dto.response.ApplicationDraftSaveResponse;
import com.skunivlikelion.homepage.domain.application.record.dto.response.ApplicationSubmitDateResponse;
import com.skunivlikelion.homepage.domain.application.record.dto.response.ApplicationSubmitResponse;
import com.skunivlikelion.homepage.domain.application.record.service.ApplicationRecordService;
import com.skunivlikelion.homepage.domain.common.enums.Track;

import backend.boilerplate.response.BaseResponse;
import lombok.RequiredArgsConstructor;

@Validated
@RestController
@RequiredArgsConstructor
public class ApplicationRecordControllerImpl implements ApplicationRecordController {

  private final ApplicationRecordService applicationRecordService;

  @Override
  public ResponseEntity<BaseResponse<ApplicationDraftSaveResponse>> saveFirstDraft(
      @PathVariable @Positive Long semester,
      @Valid @RequestBody ApplicationDraftSaveRequest request) {
    ApplicationDraftSaveResponse response =
        applicationRecordService.saveFirstDraft(semester, request);
    return ResponseEntity.status(200)
        .body(BaseResponse.success(200, "지원서 최초 임시 저장에 성공했습니다.", response));
  }

  @Override
  public ResponseEntity<BaseResponse<ApplicationDraftSaveResponse>> saveDraft(
      @PathVariable Long applicationRecordId,
      @Valid @RequestBody ApplicationDraftSaveRequest request) {
    ApplicationDraftSaveResponse response =
        applicationRecordService.saveDraft(applicationRecordId, request);
    return ResponseEntity.status(200)
        .body(BaseResponse.success(200, "지원서 임시 저장에 성공했습니다.", response));
  }

  @Override
  public ResponseEntity<BaseResponse<ApplicationSubmitResponse>> submit(
      @PathVariable @Positive Long semester,
      @Valid @RequestBody ApplicationDraftSaveRequest request) {
    ApplicationSubmitResponse response = applicationRecordService.submit(semester, request);
    return ResponseEntity.status(200).body(BaseResponse.success(200, "지원서 제출에 성공했습니다.", response));
  }

  @Override
  public ResponseEntity<BaseResponse<ApplicationAnswersGetResponse>> getMyApplicationAnswers(
      @PathVariable @Positive Long semester) {
    ApplicationAnswersGetResponse response =
        applicationRecordService.getMyApplicationAnswers(semester);
    return ResponseEntity.status(200).body(BaseResponse.success(200, "지원서 조회에 성공했습니다.", response));
  }

  @Override
  public ResponseEntity<BaseResponse<ApplicationSubmitDateResponse>> getMySubmitDate(
      @PathVariable @Positive Long semester) {

    ApplicationSubmitDateResponse response = applicationRecordService.getMySubmitDate(semester);

    return ResponseEntity.status(200)
        .body(BaseResponse.success(200, "지원서 제출 일자 조회에 성공했습니다.", response));
  }

  @Override
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<BaseResponse<AdminApplicantListResponse>> getApplicants(
      @RequestParam(required = false) Long semester,
      @RequestParam(required = false) Track track,
      @RequestParam(required = false) String search) {

    AdminApplicantListResponse response =
        applicationRecordService.getApplicants(semester, track, search);

    return ResponseEntity.status(200)
        .body(BaseResponse.success(200, "지원자 목록 조회에 성공했습니다.", response));
  }

  @Override
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<BaseResponse<AdminApplicationDetailResponse>> getApplicationDetail(
      @PathVariable Long applicationRecordId) {

    AdminApplicationDetailResponse response =
        applicationRecordService.getApplicationDetail(applicationRecordId);

    return ResponseEntity.status(200).body(BaseResponse.success(200, "지원서 조회에 성공했습니다.", response));
  }
}
