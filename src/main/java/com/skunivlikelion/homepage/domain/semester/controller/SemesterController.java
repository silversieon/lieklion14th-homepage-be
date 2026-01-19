/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.semester.controller;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.skunivlikelion.homepage.domain.semester.dto.request.SemesterRequest;
import com.skunivlikelion.homepage.domain.semester.dto.response.SemesterResponse;

import backend.boilerplate.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Validated
@RequestMapping("/api")
@Tag(name = "Semester", description = "기수 관리 API")
public interface SemesterController {

  @Operation(
      summary = "[관리자 | 토큰 O | 기수 생성]",
      description =
          """
              **Parameters**  \n
              semester: 생성할 기수 값  \n

              **Returns**  \n
              생성된 기수 정보
              """)
  @PostMapping("/v1/admin/semesters")
  ResponseEntity<BaseResponse<SemesterResponse>> createSemester(
      @Valid @RequestBody SemesterRequest request);

  @Operation(
      summary = "[관리자 | 토큰 O | 기수 삭제]",
      description =
          """
              **Parameters**  \n
              semester: 삭제할 기수 값  \n

              **Returns**  \n
              기수 삭제 성공/실패 여부
              """)
  @DeleteMapping("/v1/admin/semesters/{semester}")
  ResponseEntity<BaseResponse<Void>> deleteSemester(@PathVariable @Positive Long semester);

  @Operation(
      summary = "[사용자 | 토큰 X | 등록된 기수 내림차순 조회]",
      description = """
          **Returns**  \n
          등록된 모든 기수 내림차순 목록
          """)
  @GetMapping("/v1/semesters")
  ResponseEntity<BaseResponse<List<SemesterResponse>>> getAllSemesters();
}
