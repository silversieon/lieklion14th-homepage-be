/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.application.record.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.skunivlikelion.homepage.domain.application.record.dto.request.ApplicationDraftSaveRequest;
import com.skunivlikelion.homepage.domain.application.record.dto.response.AdminApplicantListItem;
import com.skunivlikelion.homepage.domain.application.record.dto.response.ApplicantUserInfo;
import com.skunivlikelion.homepage.domain.application.record.dto.response.ApplicationAnswerItem;
import com.skunivlikelion.homepage.domain.application.record.dto.response.ApplicationRecordMeta;
import com.skunivlikelion.homepage.domain.application.record.dto.response.ApplicationRecordResponse;
import com.skunivlikelion.homepage.domain.application.record.service.ApplicationRecordService;
import com.skunivlikelion.homepage.domain.common.enums.Track;
import com.skunivlikelion.homepage.global.page.response.InfiniteResponse;

import backend.boilerplate.response.BaseResponse;
import lombok.RequiredArgsConstructor;

@Validated
@RestController
@RequiredArgsConstructor
public class ApplicationRecordControllerImpl implements ApplicationRecordController {

  private final ApplicationRecordService applicationRecordService;

  @Override
  public ResponseEntity<BaseResponse<ApplicationRecordMeta>> saveFirstDraft(
      @Valid @RequestBody ApplicationDraftSaveRequest request) {

    ApplicationRecordMeta response = applicationRecordService.saveFirstDraft(request);

    return ResponseEntity.status(200)
        .body(BaseResponse.success(200, "지원서 최초 임시 저장에 성공했습니다.", response));
  }

  @Override
  public ResponseEntity<BaseResponse<ApplicationRecordMeta>> saveDraft(
      @Valid @RequestBody ApplicationDraftSaveRequest request) {

    ApplicationRecordMeta response = applicationRecordService.saveDraft(request);

    return ResponseEntity.status(200)
        .body(BaseResponse.success(200, "지원서 임시 저장에 성공했습니다.", response));
  }

  @Override
  public ResponseEntity<BaseResponse<ApplicationRecordMeta>> submit(
      @Valid @RequestBody ApplicationDraftSaveRequest request) {

    ApplicationRecordMeta response = applicationRecordService.submit(request);

    return ResponseEntity.status(200).body(BaseResponse.success(200, "지원서 제출에 성공했습니다.", response));
  }

  @Override
  public ResponseEntity<BaseResponse<ApplicantUserInfo>> getMyDraftPersonalInfo() {

    ApplicantUserInfo response = applicationRecordService.getMyDraftPersonalInfo();

    return ResponseEntity.status(200)
        .body(BaseResponse.success(200, "내 인적사항 조회에 성공했습니다.", response));
  }

  @Override
  public ResponseEntity<BaseResponse<List<ApplicationAnswerItem>>> getMyDraftAnswersByTrack(
      @RequestParam Track track) {

    List<ApplicationAnswerItem> response = applicationRecordService.getMyDraftAnswersByTrack(track);

    return ResponseEntity.status(200)
        .body(BaseResponse.success(200, "내 임시 저장 지원서 답변 조회에 성공했습니다.", response));
  }

  @Override
  public ResponseEntity<BaseResponse<ApplicationRecordResponse>>
      getMySubmittedApplicationAnswers() {

    ApplicationRecordResponse response =
        applicationRecordService.getMySubmittedApplicationAnswers();

    return ResponseEntity.status(200)
        .body(BaseResponse.success(200, "내 지원서 조회에 성공했습니다.", response));
  }

  @Override
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<BaseResponse<InfiniteResponse<AdminApplicantListItem>>> getApplicants(
      @RequestParam(required = false) Long semester,
      @RequestParam(required = false) Track track,
      @RequestParam(required = false) String search,
      @RequestParam(required = false) Long lastCursor,
      @RequestParam(defaultValue = "10") Integer size) {

    InfiniteResponse<AdminApplicantListItem> response =
        applicationRecordService.getApplicants(semester, track, search, lastCursor, size);

    return ResponseEntity.status(200)
        .body(BaseResponse.success(200, "지원자 목록 조회에 성공했습니다.", response));
  }

  @Override
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<BaseResponse<ApplicationRecordResponse>> getApplicationDetail(
      @PathVariable Long applicationRecordId) {

    ApplicationRecordResponse response =
        applicationRecordService.getApplicationDetail(applicationRecordId);

    return ResponseEntity.status(200).body(BaseResponse.success(200, "지원서 조회에 성공했습니다.", response));
  }
}
