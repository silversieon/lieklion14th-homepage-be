/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.interview.schedule.controller;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.skunivlikelion.homepage.domain.common.enums.Track;
import com.skunivlikelion.homepage.domain.interview.schedule.dto.request.InterviewScheduleCreateRequest;
import com.skunivlikelion.homepage.domain.interview.schedule.dto.response.InterviewScheduleResponse;

import backend.boilerplate.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Validated
@RequestMapping("/api")
@Tag(name = "InterviewSchedule", description = "면접 일정 관리 API")
public interface InterviewScheduleController {

  @Operation(
      summary = "[ 관리자 | 토큰 O | 면접 일정 생성(기수별) ]",
      description =
          """
              **Path Variable**
              - semester: 기수 값

              **Query Parameter**
              - track: 트랙(Enum)

              **Request Body**
              - date: 면접 날짜 (yyyy-MM-dd)
              - startTime: 시작 시간 (HH:mm:ss)
              - endTime: 종료 시간 (HH:mm:ss)

              **Returns**
              - 생성된 면접 일정(슬롯) 1건
              """)
  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping("/v1/admin/interviews/schedules/{semester}")
  ResponseEntity<BaseResponse<InterviewScheduleResponse>> createInterviewSchedule(
      @PathVariable @Positive Long semester,
      @RequestParam Track track,
      @Valid @RequestBody InterviewScheduleCreateRequest request);

  @Operation(
      summary = "[ 관리자 | 토큰 O | 면접 일정 조회 ]",
      description =
          """
              **Query Parameter (Optional)**
              - semester: 기수 (미입력 시 전체 기수)
              - track: 트랙 Enum (미입력 시 전체 트랙)
              - dateFrom: 조회 시작 날짜 (yyyy-MM-dd)
              - dateTo: 조회 종료 날짜 (yyyy-MM-dd)

              **Date Filter 동작 방식**
              - dateFrom만 입력: 해당 날짜부터 ~ 이후 전체 조회
              - dateTo만 입력: 해당 날짜까지 ~ 이전 전체 조회
              - dateFrom/dateTo 둘 다 입력: 기간 조회 (포함, inclusive)
              - dateFrom == dateTo: 해당 날짜(하루)만 조회

              **정렬 기준**
              - date ASC, startTime ASC
              """)
  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping("/v1/admin/interviews/schedules")
  ResponseEntity<BaseResponse<List<InterviewScheduleResponse>>> getAdminInterviewSchedules(
      @RequestParam(required = false) @Positive Long semester,
      @RequestParam(required = false) Track track,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate dateFrom,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate dateTo);

  @Operation(
      summary = "[ 사용자 | 토큰 O | 면접 일정 조회(예약 상태 포함) ]",
      description =
          """
              **Query Parameter**
              - semester: 기수 (Optional, 미입력 시 기수 선택 필요)
              - dateFrom: 조회 시작 날짜 (Optional, yyyy-MM-dd)
              - dateTo: 조회 종료 날짜 (Optional, yyyy-MM-dd)

              **접근 정책**
              - 로그인 사용자만 접근 가능
              - 서류 합격자(ApplicationRecord.isPassed = true)만 조회 가능
              - 트랙은 ApplicationRecord.track 기준 자동 적용 (클라이언트 입력 X)

              **정렬 기준**
              - date ASC, startTime ASC

              **예약 상태 필드**
              - booked: true → 이미 예약된 일정
              - booked: false → 예약 가능 일정
              """)
  @PreAuthorize("isAuthenticated()")
  @GetMapping("/v1/interviews/schedules")
  ResponseEntity<BaseResponse<List<InterviewScheduleResponse>>> getUserInterviewSchedules(
      @RequestParam(required = false) @Positive Long semester,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate dateFrom,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate dateTo);
}
