/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.interview.booking.controller;

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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.skunivlikelion.homepage.domain.common.enums.Track;
import com.skunivlikelion.homepage.domain.interview.booking.dto.request.InterviewBookingCreateRequest;
import com.skunivlikelion.homepage.domain.interview.booking.dto.response.AdminInterviewBookingResponse;
import com.skunivlikelion.homepage.domain.interview.booking.dto.response.InterviewBookingResponse;
import com.skunivlikelion.homepage.domain.interview.booking.dto.response.UserInterviewBookingResponse;
import com.skunivlikelion.homepage.global.common.BaseResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Validated
@RequestMapping("/api")
@Tag(name = "InterviewBooking", description = "면접 예약 API")
public interface InterviewBookingController {

  @Operation(
      summary = "[ 사용자 | 토큰 O | 면접 일정 예약 ]",
      description =
          """
              **Request Body**
              - scheduleId: 예약할 면접 일정 id

              **정책**
              - 슬롯당 1명 예약(선착순): UNIQUE(interview_schedule_id)
              - 기수별 사용자 1회 예약: UNIQUE(semester_id, applicant_key)

              **Returns**
              - 예약 id, schedule id
              """)
  @PreAuthorize("isAuthenticated()")
  @PostMapping("/v1/interviews/bookings")
  ResponseEntity<BaseResponse<InterviewBookingResponse>> createBooking(
      @Valid @RequestBody InterviewBookingCreateRequest request);

  @Operation(
      summary = "[ 관리자 | 토큰 O | 면접 예약 일정 조회 ]",
      description =
          """
              **필수 Query**
              - semester: 기수
              - dates: 면접일 (yyyy-MM-dd)

              **선택 Query**
              - track: 트랙 필터
              - search: 이름/학번 검색

              **정책**
              - track 지정 시 해당 트랙만 반환
              - 예약된 슬롯(booked=true)만 반환
              - 해당 날짜에 등록된 면접 슬롯이 존재하나 예약이 없으면 dates는 포함되고 times는 빈 배열
              - 해당 날짜에 등록된 면접 슬롯이 없으면 dates는 빈 배열
              """)
  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping("/v1/admin/interviews/bookings")
  ResponseEntity<BaseResponse<AdminInterviewBookingResponse>> getAdminBookings(
      @RequestParam @Positive Long semester,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
      @RequestParam(required = false) Track track,
      @RequestParam(required = false) String search);

  @Operation(
      summary = "[ 사용자 | 토큰 O | 내 면접 예약 조회 ]",
      description =
          """
              **Query**
              - semester (Optional): 미입력 시 현재 모집중인(Active) 공고의 기수로 조회

              **정책**
              - 사용자당 기수별 예약 1건만 존재
              - 예약이 없으면 200 OK + booking=null 반환
              """)
  @PreAuthorize("isAuthenticated()")
  @GetMapping("/v1/interviews/bookings")
  ResponseEntity<BaseResponse<UserInterviewBookingResponse>> getMyBooking(
      @RequestParam(required = false) @Positive Long semester);

  @Operation(
      summary = "[ 사용자 | 토큰 O | 내 면접 예약 변경 ]",
      description =
          """
              **Path**
              - scheduleId: 변경할 면접 일정(슬롯) ID

              **Query**
              - semester (Optional): 미입력 시 현재 진행 중인(Active) 모집 공고 기수 자동 적용

              **정책**
              - 기존 예약이 반드시 존재해야 함
              - 새 슬롯이 이미 예약되어 있으면 변경 불가
              - 동일 슬롯으로는 변경 불가
              - 서류 합격자만 변경 가능
              - 지원서 트랙과 변경할 면접 일정 트랙이 일치해야 변경 가능
              """)
  @PreAuthorize("isAuthenticated()")
  @PutMapping("/v1/interviews/bookings/{scheduleId}")
  ResponseEntity<BaseResponse<UserInterviewBookingResponse>> updateMyBooking(
      @RequestParam(required = false) @Positive Long semester,
      @PathVariable @Positive Long scheduleId);

  @Operation(
      summary = "[ 관리자 | 토큰 O | 예약된 면접 일정 삭제 ]",
      description =
          """
              **Path Variable**
              - bookingId: 삭제할 예약 ID

              **정책**
              - 이미 시작된 일정의 예약은 삭제 불가
              """)
  @PreAuthorize("hasRole('ADMIN')")
  @DeleteMapping("/v1/admin/interviews/bookings/{bookingId}")
  ResponseEntity<BaseResponse<Void>> deleteAdminBooking(@PathVariable @Positive Long bookingId);
}
