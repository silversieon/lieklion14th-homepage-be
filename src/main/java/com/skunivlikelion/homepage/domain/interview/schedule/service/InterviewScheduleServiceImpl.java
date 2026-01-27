/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.interview.schedule.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.skunivlikelion.homepage.domain.application.record.entity.ApplicationRecord;
import com.skunivlikelion.homepage.domain.application.record.repository.ApplicationRecordRepository;
import com.skunivlikelion.homepage.domain.common.enums.Track;
import com.skunivlikelion.homepage.domain.interview.booking.repository.InterviewBookingRepository;
import com.skunivlikelion.homepage.domain.interview.schedule.dto.request.InterviewScheduleCreateRequest;
import com.skunivlikelion.homepage.domain.interview.schedule.dto.response.InterviewScheduleResponse;
import com.skunivlikelion.homepage.domain.interview.schedule.entity.InterviewSchedule;
import com.skunivlikelion.homepage.domain.interview.schedule.exception.InterviewScheduleErrorCode;
import com.skunivlikelion.homepage.domain.interview.schedule.mapper.InterviewScheduleMapper;
import com.skunivlikelion.homepage.domain.interview.schedule.repository.InterviewScheduleRepository;
import com.skunivlikelion.homepage.domain.semester.repository.SemesterRepository;
import com.skunivlikelion.homepage.global.security.CurrentUserProvider;

import backend.boilerplate.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class InterviewScheduleServiceImpl implements InterviewScheduleService {

  private final InterviewScheduleRepository interviewScheduleRepository;
  private final SemesterRepository semesterRepository;
  private final InterviewBookingRepository interviewBookingRepository;
  private final InterviewScheduleMapper interviewScheduleMapper;
  private final CurrentUserProvider currentUserProvider;
  private final ApplicationRecordRepository applicationRecordRepository;

  @Override
  public InterviewScheduleResponse createInterviewSchedule(
      Long semester, Track track, InterviewScheduleCreateRequest request) {

    log.info(
        "[InterviewSchedule] 면접 일정 생성 요청 - semester={}, track={}, date={}, startTime={}, endTime={}",
        semester,
        track,
        request != null ? request.getDate() : null,
        request != null ? request.getStartTime() : null,
        request != null ? request.getEndTime() : null);

    validateSemesterExists(semester);
    validateTrack(track);
    validateRequestBody(request);
    validateTimeRange(request);

    boolean overlap =
        interviewScheduleRepository.existsOverlappingSchedule(
            semester, track, request.getDate(), request.getStartTime(), request.getEndTime());

    if (overlap) {
      log.info(
          "[InterviewSchedule] 면접 일정 생성 실패 - 시간 겹침 - semester={}, track={}, date={}, slot={}~{}",
          semester,
          track,
          request.getDate(),
          request.getStartTime(),
          request.getEndTime());
      throw new CustomException(InterviewScheduleErrorCode.OVERLAPPING_SCHEDULE);
    }

    InterviewSchedule saved =
        interviewScheduleRepository.save(
            interviewScheduleMapper.toEntity(semester, track, request));

    log.info(
        "[InterviewSchedule] 면접 일정 생성 완료 - semester={}, id={}, track={}, date={}, slot={}~{}",
        semester,
        saved.getId(),
        saved.getTrack(),
        saved.getDate(),
        saved.getStartTime(),
        saved.getEndTime());

    return interviewScheduleMapper.toResponse(saved);
  }

  @Override
  @Transactional(readOnly = true)
  public List<InterviewScheduleResponse> getAdminInterviewSchedules(
      Long semester, Track track, LocalDate dateFrom, LocalDate dateTo) {

    log.info(
        "[InterviewSchedule] 관리자 면접 일정 조회 - semester={}, track={}, dateFrom={}, dateTo={}",
        semester,
        track,
        dateFrom,
        dateTo);

    List<InterviewSchedule> schedules =
        interviewScheduleRepository.findAdminSchedules(semester, track, dateFrom, dateTo);

    return interviewScheduleMapper.toResponseList(schedules);
  }

  @Override
  @Transactional(readOnly = true)
  public List<InterviewScheduleResponse> getUserInterviewSchedules(
      Long semester, LocalDate dateFrom, LocalDate dateTo) {

    Long userId = currentUserProvider.getUserId();

    log.info(
        "[InterviewSchedule] 사용자 면접 일정 조회 - userId={}, semester={}, dateFrom={}, dateTo={}",
        userId,
        semester,
        dateFrom,
        dateTo);

    if (semester == null) {
      log.warn("[InterviewSchedule] semester 미입력 - userId={}", userId);
      throw new CustomException(InterviewScheduleErrorCode.REQUIRED_SEMESTER);
    }

    validateSemesterExists(semester);

    ApplicationRecord record =
        applicationRecordRepository
            .findSubmittedBySemesterAndUserId(semester, userId)
            .orElseThrow(
                () -> {
                  log.warn(
                      "[InterviewSchedule] 지원 내역 없음 - userId={}, semester={}", userId, semester);
                  return new CustomException(
                      InterviewScheduleErrorCode.NOT_FOUND_APPLICATION_RECORD);
                });

    if (!record.isPassed()) {
      log.warn(
          "[InterviewSchedule] 서류 불합격자 접근 차단 - userId={}, semester={}, track={}",
          userId,
          semester,
          record.getTrack());
      throw new CustomException(InterviewScheduleErrorCode.NOT_PASSED_APPLICATION);
    }

    Long resolvedSemester = record.getApplicationForm().getSemester().getSemester();
    Track resolvedTrack = record.getTrack();

    List<InterviewSchedule> schedules =
        interviewScheduleRepository.findUserSchedules(
            resolvedSemester, resolvedTrack, dateFrom, dateTo);

    if (schedules.isEmpty()) {
      return List.of();
    }

    Set<Long> scheduleIds =
        schedules.stream().map(InterviewSchedule::getId).collect(Collectors.toSet());

    Set<Long> bookedIds = interviewBookingRepository.findBookedScheduleIds(scheduleIds);

    return interviewScheduleMapper.toResponseList(schedules, bookedIds);
  }

  private void validateSemesterExists(Long semester) {
    if (semester == null || !semesterRepository.existsById(semester)) {
      log.warn("[InterviewSchedule] 기수 검증 실패 - semester={}", semester);
      throw new CustomException(InterviewScheduleErrorCode.NOT_FOUND_SEMESTER);
    }
  }

  private void validateTrack(Track track) {
    if (track == null) {
      log.warn("[InterviewSchedule] 트랙 누락 - track=null");
      throw new CustomException(InterviewScheduleErrorCode.INVALID_TRACK);
    }
  }

  private void validateRequestBody(InterviewScheduleCreateRequest request) {
    if (request == null) {
      log.warn("[InterviewSchedule] 요청 바디 누락 - request=null");
      throw new CustomException(InterviewScheduleErrorCode.INVALID_TIME_RANGE);
    }
  }

  private void validateTimeRange(InterviewScheduleCreateRequest request) {
    if (request.getStartTime() == null || request.getEndTime() == null) {
      log.warn("[InterviewSchedule] 시간 값 누락");
      throw new CustomException(InterviewScheduleErrorCode.INVALID_TIME_RANGE);
    }

    if (!request.getStartTime().isBefore(request.getEndTime())) {
      log.info(
          "[InterviewSchedule] 시간 검증 실패 - startTime >= endTime - startTime={}, endTime={}",
          request.getStartTime(),
          request.getEndTime());
      throw new CustomException(InterviewScheduleErrorCode.INVALID_TIME_RANGE);
    }
  }

  @Override
  public void deleteInterviewSchedule(Long scheduleId) {

    log.info("[InterviewSchedule] 관리자 면접 일정 삭제 요청 - scheduleId={}", scheduleId);

    InterviewSchedule schedule =
        interviewScheduleRepository
            .findById(scheduleId)
            .orElseThrow(
                () -> {
                  log.warn("[InterviewSchedule] 삭제 실패 - 일정 없음 - scheduleId={}", scheduleId);
                  return new CustomException(InterviewScheduleErrorCode.NOT_FOUND_SCHEDULE);
                });

    boolean hasBooking = interviewBookingRepository.existsByScheduleId(scheduleId);
    if (hasBooking) {
      log.warn("[InterviewSchedule] 삭제 실패 - 예약 존재 - scheduleId={}", scheduleId);
      throw new CustomException(InterviewScheduleErrorCode.CANNOT_DELETE_BOOKED_SCHEDULE);
    }

    interviewScheduleRepository.delete(schedule);

    log.info("[InterviewSchedule] 관리자 면접 일정 삭제 완료 - scheduleId={}", scheduleId);
  }
}
