/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.application.result.controller;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.skunivlikelion.homepage.domain.application.result.dto.request.AdminApplicationResultConfirmRequest;
import com.skunivlikelion.homepage.domain.application.result.dto.request.AdminDocumentResultUpdateRequest;
import com.skunivlikelion.homepage.domain.application.result.dto.response.AdminApplicationResultConfirmResponse;
import com.skunivlikelion.homepage.domain.application.result.dto.response.AdminDocumentResultUpdateResponse;
import com.skunivlikelion.homepage.domain.application.result.dto.response.MyInterviewResultResponse;
import com.skunivlikelion.homepage.domain.application.result.service.ApplicationResultService;

import backend.boilerplate.response.BaseResponse;
import lombok.RequiredArgsConstructor;

@Validated
@RestController
@RequiredArgsConstructor
public class ApplicationResultControllerImpl implements ApplicationResultController {

  private final ApplicationResultService applicationResultService;

  @Override
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<BaseResponse<AdminDocumentResultUpdateResponse>> updateDocumentResult(
      @PathVariable Long applicationRecordId,
      @Valid @RequestBody AdminDocumentResultUpdateRequest request) {

    AdminDocumentResultUpdateResponse response =
        applicationResultService.updateDocumentResult(
            applicationRecordId, request.getIsDocumentPassed());

    return ResponseEntity.status(200)
        .body(BaseResponse.success(200, "서류 합격 여부 수정에 성공했습니다.", response));
  }

  @Override
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<BaseResponse<AdminApplicationResultConfirmResponse>>
      confirmApplicationResult(
          @PathVariable Long applicationRecordId,
          @Valid @RequestBody AdminApplicationResultConfirmRequest request) {
    return ResponseEntity.status(201)
        .body(
            BaseResponse.success(
                200,
                "최종 합격 여부 반영에 성공했습니다.",
                applicationResultService.confirmDocumentResult(
                    applicationRecordId, request.getPassed())));
  }

  @Override
  public ResponseEntity<BaseResponse<MyInterviewResultResponse>> getMyInterviewResult() {
    return ResponseEntity.status(200)
        .body(
            BaseResponse.success(
                200,
                "면접 합격 결과 조회에 성공했습니다.",
                applicationResultService.getCurrentUserInterviewResult()));
  }
}
