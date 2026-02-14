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
   * <p>조회 단위: - InterviewSchedule(시간 슬롯) 기준 - 예약된 슬롯만 반환 (예약이 없는 슬롯은 제외)
   *
   * <p>필수 조건: - semester(기수) - date
   *
   * <p>선택 조건: - track: 트랙 필터 (지정 시 해당 트랙만 반환) - search: 이름/학번 검색어
   *
   * <p>정렬 기준: - track ASC - date ASC - startTime ASC - scheduleId ASC
   *
   * <p>search가 존재하는 경우, (기수 + 날짜(+track)) 조건으로 조회된 예약 결과에 대해 이름 또는 학번이 매칭되는 예약만 최종 응답에 포함됨 - track
   * 필터가 존재하면 tracks 배열에는 해당 트랙만 포함됨
   *
   * @param semester 기수 (필수)
   * @param date 면접일 목록 (필수)
   * @param track 트랙 필터 (선택)
   * @param search 이름/학번 검색어 (선택)
   * @return 관리자 면접 예약 일정 조회 응답
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
