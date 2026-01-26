/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.interview.schedule.service;

import java.time.LocalDate;
import java.util.List;

import com.skunivlikelion.homepage.domain.common.enums.Track;
import com.skunivlikelion.homepage.domain.interview.schedule.dto.request.InterviewScheduleCreateRequest;
import com.skunivlikelion.homepage.domain.interview.schedule.dto.response.InterviewScheduleResponse;

public interface InterviewScheduleService {

  /**
   * [ 관리자 | 면접 일정 생성 메서드 ]
   *
   * @param semester 기수
   * @param track 트랙 (QueryParam)
   * @param request 면접 일정 생성 요청 DTO (date/startTime/endTime)
   * @return 생성된 면접 일정 응답 DTO
   */
  InterviewScheduleResponse createInterviewSchedule(
      Long semester, Track track, InterviewScheduleCreateRequest request);

  /**
   * [ 관리자 | 면접 일정 조회 메서드 ]
   *
   * <p>필터 미입력(null) 시 전체 조회
   *
   * @param semester 기수 (Optional)
   * @param track 트랙 (Optional)
   * @param dateFrom 시작 날짜 (Optional, yyyy-MM-dd)
   * @param dateTo 종료 날짜 (Optional, yyyy-MM-dd)
   * @return 조회된 면접 일정 목록 (date ASC, startTime ASC)
   */
  List<InterviewScheduleResponse> getAdminInterviewSchedules(
      Long semester, Track track, LocalDate dateFrom, LocalDate dateTo);
}
