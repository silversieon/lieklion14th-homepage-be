/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.application.question.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.skunivlikelion.homepage.domain.application.question.dto.request.ApplicationQuestionUpsertRequest;
import com.skunivlikelion.homepage.domain.application.question.dto.response.ApplicationQuestionGetResponse;
import com.skunivlikelion.homepage.domain.application.question.dto.response.ApplicationQuestionUpsertResponse;
import com.skunivlikelion.homepage.domain.application.question.dto.response.ApplicationSummaryListResponse;
import com.skunivlikelion.homepage.domain.application.question.service.ApplicationQuestionService;
import com.skunivlikelion.homepage.domain.common.enums.Track;

import backend.boilerplate.response.BaseResponse;
import lombok.RequiredArgsConstructor;

@Validated
@RestController
@RequiredArgsConstructor
public class ApplicationQuestionControllerImpl implements ApplicationQuestionController {

  private final ApplicationQuestionService applicationQuestionService;

  @Override
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<BaseResponse<ApplicationQuestionUpsertResponse>> createQuestions(
      @PathVariable @Positive Long semester,
      @Valid @RequestBody ApplicationQuestionUpsertRequest request) {
    ApplicationQuestionUpsertResponse response =
        applicationQuestionService.createQuestions(semester, request);
    return ResponseEntity.status(201)
        .body(BaseResponse.success(201, "지원서 질문 등록에 성공했습니다.", response));
  }

  @Override
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<BaseResponse<ApplicationQuestionUpsertResponse>> updateQuestions(
      @PathVariable @Positive Long semester,
      @Valid @RequestBody ApplicationQuestionUpsertRequest request) {
    ApplicationQuestionUpsertResponse response =
        applicationQuestionService.updateQuestions(semester, request);
    return ResponseEntity.status(200)
        .body(BaseResponse.success(200, "지원서 질문 수정에 성공했습니다.", response));
  }

  @Override
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<BaseResponse<Void>> deleteQuestions(@PathVariable @Positive Long semester) {
    applicationQuestionService.deleteQuestions(semester);
    return ResponseEntity.status(200).body(BaseResponse.success(200, "지원서 질문 삭제에 성공했습니다.", null));
  }

  @Override
  public ResponseEntity<BaseResponse<ApplicationQuestionGetResponse>>
      getQuestionsBySemesterAndTrack(
          @RequestParam @Positive Long semester, @RequestParam Track track) {
    ApplicationQuestionGetResponse response =
        applicationQuestionService.getQuestionsBySemesterAndTrack(semester, track);
    return ResponseEntity.status(200)
        .body(BaseResponse.success(200, "지원서 질문 조회에 성공했습니다.", response));
  }

  @Override
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<BaseResponse<ApplicationSummaryListResponse>> getApplicationSummaries() {
    ApplicationSummaryListResponse response = applicationQuestionService.getApplicationSummaries();
    return ResponseEntity.status(200)
        .body(BaseResponse.success(200, "등록된 지원서 목록 조회에 성공했습니다.", response));
  }
}
