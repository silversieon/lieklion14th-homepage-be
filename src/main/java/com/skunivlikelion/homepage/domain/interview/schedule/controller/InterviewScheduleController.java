/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.interview.schedule.controller;

import java.time.LocalDate;

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
import com.skunivlikelion.homepage.domain.interview.schedule.dto.response.AdminInterviewScheduleResponse;
import com.skunivlikelion.homepage.domain.interview.schedule.dto.response.InterviewScheduleResponse;
import com.skunivlikelion.homepage.domain.interview.schedule.dto.response.UserInterviewScheduleResponse;

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
              **Path Variable (Required)**
              - semester: 기수

              **Query Parameter (Optional)**
              - track: 트랙 Enum (미입력 시 전체 트랙)
              - dateFrom: 조회 시작 날짜 (yyyy-MM-dd)
              - dateTo: 조회 종료 날짜 (yyyy-MM-dd)

              **응답 구조**
              - tracks[]: 트랙별 그룹
                - dates[]: 날짜별 그룹
                  - times[]: 시간 슬롯 목록 (booked 포함)

              **정렬 기준**
              - track ASC, date ASC, startTime ASC
              """)
  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping("/v1/admin/interviews/schedules/{semester}")
  ResponseEntity<BaseResponse<AdminInterviewScheduleResponse>> getAdminInterviewSchedules(
      @PathVariable @Positive Long semester,
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
              - semester: 기수 (Optional, 미입력 시 현재 진행 중인 공고의 기수로 자동 적용)
              - dateFrom: 조회 시작 날짜 (Optional, yyyy-MM-dd)
              - dateTo: 조회 종료 날짜 (Optional, yyyy-MM-dd)

              **접근 정책**
              - 로그인 사용자만 접근 가능
              - 트랙은 ApplicationRecord.track 기준 자동 적용 (클라이언트 입력 X)

              **응답 구조**
              - documentPassed: 서류 합격 여부
              - track: 트랙 정보
              - dates[]: 날짜별 그룹 (서류 불합격 시 빈 배열 [])
                - times[]: 시간 슬롯 목록 (booked 포함)

              **정렬 기준**
              - date ASC
              - times[]: startTime ASC

              **참고**
              - 서류 불합격이어도 200 OK 응답
              - 서류 불합격 시 documentPassed=false, dates=[]
              """)
  @PreAuthorize("isAuthenticated()")
  @GetMapping("/v1/interviews/schedules")
  ResponseEntity<BaseResponse<UserInterviewScheduleResponse>> getUserInterviewSchedules(
      @RequestParam(required = false) @Positive Long semester,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate dateFrom,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate dateTo);

  @Operation(
      summary = "[ 관리자 | 토큰 O | 면접 일정 삭제 ]",
      description =
          """
              **Path Variable**
              - scheduleId: 면접 일정 id

              **삭제 정책**
              - 예약(InterviewBooking)이 존재하는 일정은 삭제할 수 없습니다.

              **Returns**
              - 면접 일정 삭제 성공/실패 여부
              """)
  @PreAuthorize("hasRole('ADMIN')")
  @DeleteMapping("/v1/admin/interviews/schedules/{scheduleId}")
  ResponseEntity<BaseResponse<Void>> deleteInterviewSchedule(
      @PathVariable @Positive Long scheduleId);
}
