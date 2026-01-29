/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.application.question.controller;

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
import org.springframework.web.bind.annotation.RequestParam;

import com.skunivlikelion.homepage.domain.application.question.dto.request.ApplicationQuestionUpsertRequest;
import com.skunivlikelion.homepage.domain.application.question.dto.response.ApplicationQuestionGetResponse;
import com.skunivlikelion.homepage.domain.application.question.dto.response.ApplicationQuestionUpsertResponse;
import com.skunivlikelion.homepage.domain.application.question.dto.response.ApplicationSummaryListResponse;
import com.skunivlikelion.homepage.domain.common.enums.Track;

import backend.boilerplate.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RequestMapping("/api")
@Tag(name = "ApplicationQuestion", description = "지원서 질문 관리 API")
public interface ApplicationQuestionController {

  @Operation(
      summary = "[ 관리자 | 토큰 O | 지원서 질문 등록 ]",
      description =
          """
              **Path Parameter**
              semester: 기수 값

              **Request Body**
              트랙별 질문 목록 (문항번호, 질문 내용)

              **Returns**
              등록된 지원서 질문 목록
              """)
  @PostMapping("/v1/admin/applications/questions/{semester}")
  ResponseEntity<BaseResponse<ApplicationQuestionUpsertResponse>> createQuestions(
      @PathVariable @Positive Long semester,
      @Valid @RequestBody ApplicationQuestionUpsertRequest request);

  @Operation(
      summary = "[ 관리자 | 토큰 O | 지원서 질문 수정 ]",
      description =
          """
              **Path Parameter**
              applicationFormId: 모집 공고 식별자

              **Request Body**
              트랙별 질문 목록 (문항번호, 질문 내용)

              **Returns**
              수정된 지원서 질문 목록
              """)
  @PutMapping("/v1/admin/applications/forms/{applicationFormId}/questions")
  ResponseEntity<BaseResponse<ApplicationQuestionUpsertResponse>> updateQuestions(
      @PathVariable @Positive Long applicationFormId,
      @Valid @RequestBody ApplicationQuestionUpsertRequest request);

  @Operation(
      summary = "[ 관리자 | 토큰 O | 지원서 질문 삭제 ]",
      description =
          """
              **Path Parameter**
              applicationFormId: 모집 공고 식별자

              **Returns**
              삭제 성공 여부
              """)
  @DeleteMapping("/v1/admin/applications/forms/{applicationFormId}/questions")
  ResponseEntity<BaseResponse<Void>> deleteQuestions(
      @PathVariable @Positive Long applicationFormId);

  @Operation(
      summary = "[ 사용자 | 토큰 O | 진행중 지원서 질문 조회 ]",
      description =
          """
              **Query Parameters**
              track: 트랙 값

              **Returns**
              진행중 지원서 질문 목록
              """)
  @GetMapping("/v1/applications/questions")
  ResponseEntity<BaseResponse<ApplicationQuestionGetResponse>> getCurrentQuestionsByTrack(
      @RequestParam Track track);

  @Operation(
      summary = "[ 관리자 | 토큰 O | 지원서 목록 조회 ]",
      description =
          """
              **Returns**
              진행중 / 진행완료로 분리된 지원서 목록
              """)
  @GetMapping("/v1/admin/applications/questions/summaries")
  ResponseEntity<BaseResponse<ApplicationSummaryListResponse>> getApplicationSummaries();

  @Operation(
      summary = "[ 개발자 | 토큰 O | 특정 기수 질문 전체 조회 ]",
      description =
          """
              **Path Parameter**
              semester: 기수 값

              **Returns**
              해당 기수에 등록된 지원서 질문 전체(트랙별, 문항번호 오름차순)
              """)
  @GetMapping("/v1/dev/applications/questions/{semester}")
  ResponseEntity<BaseResponse<ApplicationQuestionUpsertResponse>> getQuestionsBySemesterForDev(
      @PathVariable @Positive Long semester);
}
