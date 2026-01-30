/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.interview.booking.service;

import java.time.LocalDate;

import com.skunivlikelion.homepage.domain.common.enums.Track;
import com.skunivlikelion.homepage.domain.interview.booking.dto.request.InterviewBookingCreateRequest;
import com.skunivlikelion.homepage.domain.interview.booking.dto.response.AdminInterviewBookingResponse;
import com.skunivlikelion.homepage.domain.interview.booking.dto.response.InterviewBookingResponse;
import com.skunivlikelion.homepage.domain.interview.booking.dto.response.UserInterviewBookingResponse;

public interface InterviewBookingService {

  /**
   * [ 사용자 | 토큰 O | 면접 일정 예약 ]
   *
   * <p>정책 - 사용자당 예약 1개만 가능 - 일정(슬롯) 1개당 예약 1개만 가능
   *
   * @param request scheduleId 포함
   * @return 예약 결과(예약된 일정 정보)
   */
  InterviewBookingResponse createBooking(InterviewBookingCreateRequest request);

  /**
   * [ 관리자 | 토큰 O | 예약된 면접 일정 조회·검색 ]
   *
   * <p>조회 기준 - semester: 기수 (필수) - track: 트랙 (선택) - dateFrom / dateTo: 날짜 범위 (선택) - search: 이름 / 학과
   * / 학번 검색 (선택)
   *
   * <p>응답 구조 - 트랙 → 날짜 → 시간 슬롯 - 각 슬롯에는 예약자 정보(스냅샷) + 지원서 ID
   */
  AdminInterviewBookingResponse getAdminBookings(
      Long semester, Track track, LocalDate dateFrom, LocalDate dateTo, String search);

  /**
   * [ 사용자 | 토큰 O | 내 면접 예약 조회 ]
   *
   * @param semester 기수 (Optional, 미입력 시 현재 공고 기수 자동 적용)
   * @return 사용자 예약 정보 1건
   */
  UserInterviewBookingResponse getMyBooking(Long semester);

  /**
   * [ 사용자 | 토큰 O | 내 면접 예약 변경 ]
   *
   * @param semester (Optional) 미입력 시 현재 공고 기수 자동 적용
   * @param scheduleId 변경할 면접 일정(슬롯) ID
   * @return 변경된 예약 정보
   */
  UserInterviewBookingResponse updateMyBooking(Long semester, Long scheduleId);

  /**
   * [ 관리자 | 토큰 O | 예약된 면접 일정 삭제 ]
   *
   * @param bookingId 삭제할 면접 예약 ID
   */
  void deleteAdminBooking(Long bookingId);
}
