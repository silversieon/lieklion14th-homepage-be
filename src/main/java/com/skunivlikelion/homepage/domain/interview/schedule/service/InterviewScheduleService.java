/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.interview.schedule.service;

import java.time.LocalDate;

import com.skunivlikelion.homepage.domain.common.enums.Track;
import com.skunivlikelion.homepage.domain.interview.schedule.dto.request.InterviewScheduleCreateRequest;
import com.skunivlikelion.homepage.domain.interview.schedule.dto.response.AdminInterviewScheduleResponse;
import com.skunivlikelion.homepage.domain.interview.schedule.dto.response.InterviewScheduleResponse;
import com.skunivlikelion.homepage.domain.interview.schedule.dto.response.UserInterviewScheduleResponse;

public interface InterviewScheduleService {

  /**
   * [ 관리자 | 면접 일정 생성 ]
   *
   * @param semester 기수 (PathVariable)
   * @param track 트랙 (QueryParam)
   * @param request 면접 일정 생성 요청 DTO (date/startTime/endTime)
   * @return 생성된 면접 일정(슬롯) 1건 응답 DTO
   */
  InterviewScheduleResponse createInterviewSchedule(
      Long semester, Track track, InterviewScheduleCreateRequest request);

  /**
   * [ 관리자 | 면접 일정 조회(기수별/그룹 응답) ]
   *
   * @param semester 기수 (Required, PathVariable)
   * @param track 트랙 (Optional)
   * @param dateFrom 조회 시작 날짜 (Optional)
   * @param dateTo 조회 종료 날짜 (Optional)
   * @return 트랙→날짜→시간(times) 그룹 응답
   */
  AdminInterviewScheduleResponse getAdminInterviewSchedules(
      Long semester, Track track, LocalDate dateFrom, LocalDate dateTo);

  /**
   * [ 사용자 | 토큰 O | 면접 일정 조회(예약 상태 포함) ]
   *
   * <p>접근 정책 - 로그인 사용자만 접근 가능 - 서류 합격자(ApplicationRecord.isPassed = true)만 조회 가능
   *
   * <p>조회 기준 - semester: 기수 (Optional, 미입력 시 기수 선택 필요) - track: ApplicationRecord.track 기준 자동 적용
   * (클라이언트 입력 X)
   *
   * <p>예약 상태 필드 - booked: true → 이미 예약된 일정 - booked: false → 예약 가능 일정
   *
   * @param semester 기수 (Optional)
   * @param dateFrom 조회 시작 날짜 (Optional, yyyy-MM-dd)
   * @param dateTo 조회 종료 날짜 (Optional, yyyy-MM-dd)
   * @return 날짜 → 시간 기준으로 그룹화된 면접 일정 응답
   */
  UserInterviewScheduleResponse getUserInterviewSchedules(
      Long semester, LocalDate dateFrom, LocalDate dateTo);

  /**
   * [ 관리자 | 면접 일정 삭제 ]
   *
   * <p>예약된 일정은 삭제 불가
   *
   * @param scheduleId 면접 일정 id
   */
  void deleteInterviewSchedule(Long scheduleId);
}
