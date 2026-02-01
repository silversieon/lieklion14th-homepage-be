/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.semester.controller;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.skunivlikelion.homepage.domain.semester.dto.request.SemesterRequest;
import com.skunivlikelion.homepage.domain.semester.dto.response.SemesterResponse;
import com.skunivlikelion.homepage.domain.semester.service.SemesterService;
import com.skunivlikelion.homepage.global.common.BaseResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class SemesterControllerImpl implements SemesterController {

  private final SemesterService semesterService;

  @Override
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<BaseResponse<SemesterResponse>> createSemester(
      @Valid @RequestBody SemesterRequest request) {
    SemesterResponse response = semesterService.createSemester(request);
    return ResponseEntity.status(201).body(BaseResponse.success(201, "기수 생성에 성공했습니다.", response));
  }

  @Override
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<BaseResponse<Void>> deleteSemester(@PathVariable @Positive Long semester) {
    semesterService.deleteSemester(semester);
    return ResponseEntity.status(200).body(BaseResponse.success(200, "기수 삭제에 성공했습니다.", null));
  }

  @Override
  public ResponseEntity<BaseResponse<List<SemesterResponse>>> getAllSemesters() {
    List<SemesterResponse> result = semesterService.getAllSemesters();
    return ResponseEntity.status(200).body(BaseResponse.success(200, "기수 전체 조회에 성공했습니다.", result));
  }
}
