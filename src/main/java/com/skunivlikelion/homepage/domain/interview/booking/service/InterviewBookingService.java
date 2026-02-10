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
import com.skunivlikelion.homepage.domain.user.entity.User;

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
   * [ 관리자 | 토큰 O | 면접 예약 일정 조회 ]
   *
   * <p>조회 단위: - InterviewSchedule(시간 슬롯) 기준
   *
   * <p>필수 조건: - semester(기수), date(면접일)
   *
   * <p>정렬 기준: - track ASC, startTime ASC, scheduleId ASC
   *
   * <p>특징: - 예약이 없는 슬롯도 포함하여 반환 (booked=false, bookingInfo=null) - 예약이 있는 슬롯은 booked=true,
   * bookingInfo 포함 - tracks: 해당 기수 정책 기반 트랙 목록을 모두 반환 - search가 존재하는 경우에도 슬롯은 유지되며 검색어가 이름/학번에 매칭되는
   * 예약자만 bookingInfo가 채워짐 (매칭되지 않는 예약 슬롯은 booked=true 이지만 bookingInfo=null)
   *
   * @param semester 기수 (필수)
   * @param date 면접일 (필수)
   * @param track 트랙 필터 (선택)
   * @param search 이름/학번 검색어 (선택)
   * @return 관리자 면접 예약일정 조회 응답
   */
  AdminInterviewBookingResponse getAdminBookings(
      Long semester, LocalDate date, Track track, String search);

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

  /**
   * [ 면접 예약이 존재하는지 여부 확인 메서드 ]
   *
   * @param user 면접 예약 여부를 확인할 사용자
   * @param semester 확인할 면접의 기수
   * @return 면접 예약 여부
   */
  boolean existInterviewBookingByUserAndSemester(User user, Long semester);
}
