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
              **Parameters**  \n
              semester: 등록할 기수 값  \n
              request: 트랙별 질문들(문항번호, 질문 내용)  \n

              **Constraints**  \n
              - ApplicationForm.openAt 이전에만 등록 가능  \n

              **Returns**  \n
              등록된 질문 목록(트랙별 질문들)
              """)
  @PostMapping("/v1/admin/applications/questions/{semester}")
  ResponseEntity<BaseResponse<ApplicationQuestionUpsertResponse>> createQuestions(
      @PathVariable @Positive Long semester,
      @Valid @RequestBody ApplicationQuestionUpsertRequest request);

  @Operation(
      summary = "[ 관리자 | 토큰 O | 지원서 질문 수정 ]",
      description =
          """
              **Parameters**  \n
              semester: 수정할 기수 값  \n
              request: 트랙별 질문들(문항번호, 질문 내용)  \n

              **Constraints**  \n
              ApplicationForm.openAt 이전에만 수정 가능  \n

              **Note**  \n
              groups가 비어 있으면, 질문은 빈 상태로 저장 (지원서 목록에서 조회 가능)  \n

              **Returns**  \n
              수정된 질문 목록(트랙별 질문들)
              """)
  @PutMapping("/v1/admin/applications/questions/{semester}")
  ResponseEntity<BaseResponse<ApplicationQuestionUpsertResponse>> updateQuestions(
      @PathVariable @Positive Long semester,
      @Valid @RequestBody ApplicationQuestionUpsertRequest request);

  @Operation(
      summary = "[ 관리자 | 토큰 O | 지원서 질문 삭제 ]",
      description =
          """
              **Parameters**  \n
              semester: 삭제할 기수 값  \n

              **Constraints**  \n
              - ApplicationForm.openAt 이전에만 삭제 가능  \n

              **Returns**  \n
              삭제 성공/실패 여부
              """)
  @DeleteMapping("/v1/admin/applications/questions/{semester}")
  ResponseEntity<BaseResponse<Void>> deleteQuestions(@PathVariable @Positive Long semester);

  @Operation(
      summary = "[ 사용자 | 토큰 O | 기수별, 트랙별 질문 오름차순 조회 ]",
      description =
          """
              **Query Parameters**  \n
              semester: 조회할 기수 값  \n
              track: 조회할 트랙 값  \n

              **Returns**  \n
              해당 기수/트랙 질문 목록
              """)
  @GetMapping("/v1/applications/questions")
  ResponseEntity<BaseResponse<ApplicationQuestionGetResponse>> getQuestionsBySemesterAndTrack(
      @RequestParam @Positive Long semester, @RequestParam Track track);

  @Operation(
      summary = "[ 관리자 | 토큰 O | 등록된 지원서 목록 조회(진행중/진행완료) ]",
      description =
          """
              **Returns**  \n
              진행중/진행완료로 분리된 지원서 목록 (title, closeAt 포함)  \n
              - 진행중: now < closeAt  \n
              - 진행완료: now >= closeAt  \n
              """)
  @GetMapping("/v1/admin/applications/questions/summaries")
  ResponseEntity<BaseResponse<ApplicationSummaryListResponse>> getApplicationSummaries();
}
