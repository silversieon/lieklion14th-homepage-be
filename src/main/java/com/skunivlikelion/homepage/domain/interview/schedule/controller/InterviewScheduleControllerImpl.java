/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.interview.schedule.controller;

import java.time.LocalDate;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.skunivlikelion.homepage.domain.common.enums.Track;
import com.skunivlikelion.homepage.domain.interview.schedule.dto.request.InterviewScheduleCreateRequest;
import com.skunivlikelion.homepage.domain.interview.schedule.dto.response.AdminInterviewScheduleResponse;
import com.skunivlikelion.homepage.domain.interview.schedule.dto.response.InterviewScheduleResponse;
import com.skunivlikelion.homepage.domain.interview.schedule.dto.response.UserInterviewScheduleResponse;
import com.skunivlikelion.homepage.domain.interview.schedule.service.InterviewScheduleService;
import com.skunivlikelion.homepage.global.common.BaseResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class InterviewScheduleControllerImpl implements InterviewScheduleController {

  private final InterviewScheduleService interviewScheduleService;

  @Override
  public ResponseEntity<BaseResponse<InterviewScheduleResponse>> createInterviewSchedule(
      @PathVariable @Positive Long semester,
      @RequestParam Track track,
      @Valid @RequestBody InterviewScheduleCreateRequest request) {

    InterviewScheduleResponse response =
        interviewScheduleService.createInterviewSchedule(semester, track, request);

    return ResponseEntity.status(201)
        .body(BaseResponse.success(201, "면접 일정 생성에 성공했습니다.", response));
  }

  @Override
  public ResponseEntity<BaseResponse<AdminInterviewScheduleResponse>> getAdminInterviewSchedules(
      Long semester, Track track, LocalDate dateFrom, LocalDate dateTo) {

    AdminInterviewScheduleResponse result =
        interviewScheduleService.getAdminInterviewSchedules(semester, track, dateFrom, dateTo);

    return ResponseEntity.ok(BaseResponse.success(200, "면접 일정 조회에 성공했습니다.", result));
  }

  @Override
  public ResponseEntity<BaseResponse<UserInterviewScheduleResponse>> getUserInterviewSchedules(
      Long semester, LocalDate dateFrom, LocalDate dateTo) {

    UserInterviewScheduleResponse result =
        interviewScheduleService.getUserInterviewSchedules(semester, dateFrom, dateTo);

    return ResponseEntity.ok(BaseResponse.success(200, "면접 일정 조회에 성공했습니다.", result));
  }

  @Override
  public ResponseEntity<BaseResponse<Void>> deleteInterviewSchedule(@Positive Long scheduleId) {

    interviewScheduleService.deleteInterviewSchedule(scheduleId);

    return ResponseEntity.ok(BaseResponse.success(200, "면접 일정 삭제에 성공했습니다.", null));
  }
}
