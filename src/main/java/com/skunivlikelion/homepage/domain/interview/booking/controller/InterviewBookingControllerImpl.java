/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.interview.booking.controller;

import java.time.LocalDate;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.skunivlikelion.homepage.domain.common.enums.Track;
import com.skunivlikelion.homepage.domain.interview.booking.dto.request.InterviewBookingCreateRequest;
import com.skunivlikelion.homepage.domain.interview.booking.dto.response.AdminInterviewBookingResponse;
import com.skunivlikelion.homepage.domain.interview.booking.dto.response.InterviewBookingResponse;
import com.skunivlikelion.homepage.domain.interview.booking.dto.response.UserInterviewBookingResponse;
import com.skunivlikelion.homepage.domain.interview.booking.service.InterviewBookingService;
import com.skunivlikelion.homepage.global.common.BaseResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class InterviewBookingControllerImpl implements InterviewBookingController {

  private final InterviewBookingService interviewBookingService;

  @Override
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<BaseResponse<InterviewBookingResponse>> createBooking(
      @Valid @RequestBody InterviewBookingCreateRequest request) {

    InterviewBookingResponse result = interviewBookingService.createBooking(request);

    return ResponseEntity.status(201).body(BaseResponse.success(201, "면접 예약에 성공했습니다.", result));
  }

  @Override
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<BaseResponse<AdminInterviewBookingResponse>> getAdminBookings(
      Long semester, LocalDate date, Track track, String search) {
    AdminInterviewBookingResponse result =
        interviewBookingService.getAdminBookings(semester, date, track, search);

    return ResponseEntity.ok(BaseResponse.success(200, "면접 예약 조회에 성공했습니다.", result));
  }

  @Override
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<BaseResponse<UserInterviewBookingResponse>> getMyBooking(Long semester) {

    UserInterviewBookingResponse result = interviewBookingService.getMyBooking(semester);

    return ResponseEntity.ok(BaseResponse.success(200, "내 면접 예약 조회에 성공했습니다.", result));
  }

  @Override
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<BaseResponse<UserInterviewBookingResponse>> updateMyBooking(
      @Positive Long semester, @Positive Long scheduleId) {

    UserInterviewBookingResponse result =
        interviewBookingService.updateMyBooking(semester, scheduleId);

    return ResponseEntity.ok(BaseResponse.success(200, "내 면접 예약 변경에 성공했습니다.", result));
  }

  @Override
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<BaseResponse<Void>> deleteAdminBooking(Long bookingId) {

    interviewBookingService.deleteAdminBooking(bookingId);

    return ResponseEntity.ok(BaseResponse.success(200, "예약된 면접 일정 삭제에 성공했습니다.", null));
  }
}
