/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.application.result.controller;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.skunivlikelion.homepage.domain.application.result.dto.request.AdminApplicationResultConfirmRequest;
import com.skunivlikelion.homepage.domain.application.result.dto.request.AdminDocumentResultUpdateRequest;
import com.skunivlikelion.homepage.domain.application.result.dto.response.AdminApplicationResultConfirmResponse;
import com.skunivlikelion.homepage.domain.application.result.dto.response.AdminDocumentResultUpdateResponse;
import com.skunivlikelion.homepage.domain.application.result.dto.response.MyDocumentResultResponse;

import backend.boilerplate.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RequestMapping("/api")
@Tag(name = "ApplicationResult", description = "지원 결과(서류/면접 합불) 관련 API")
public interface ApplicationResultController {

  @Operation(
      summary = "[ 관리자 | 토큰 O | 특정 지원서 서류 합격 여부 수정 ]",
      description =
          """
              **Path Parameters**  \n
              applicationRecordId: 지원서 식별자  \n

              **Request Body**  \n
              isDocumentPassed: 서류 합격 여부(true/false)  \n

              **Returns**  \n
              수정된 서류 합격 여부
              """)
  @PatchMapping("/v1/admin/applications/{applicationRecordId}/document-result")
  ResponseEntity<BaseResponse<AdminDocumentResultUpdateResponse>> updateDocumentResult(
      @PathVariable Long applicationRecordId,
      @Valid @RequestBody AdminDocumentResultUpdateRequest request);

  @Operation(
      summary = "[ 관리자 | 토큰 O | 특정 지원서 면접 합격 여부 확정 ]",
      description =
          """
              **Path Parameters**  \n
              applicationRecordId: 지원서 식별자  \n

              **Request Body**  \n
              passed: 최종 합격 여부(true/false)  \n

              **Returns**  \n
              수정된 면접 합격 여부
              """)
  @PostMapping("/v1/admin/applications/{applicationRecordId}/application-result")
  ResponseEntity<BaseResponse<AdminApplicationResultConfirmResponse>> confirmApplicationResult(
      @PathVariable Long applicationRecordId,
      @Valid @RequestBody AdminApplicationResultConfirmRequest request);

  @Operation(
      summary = "[ 사용자 | 토큰 O | 본인 서류 합격 여부 조회 ]",
      description =
          """
              **Returns**  \n
              진행중인 모집 공고에 대한 본인 지원서의 서류 합격 여부
              """)
  @GetMapping("/v1/applications/document-result")
  ResponseEntity<BaseResponse<MyDocumentResultResponse>> getMyDocumentResult();
}
