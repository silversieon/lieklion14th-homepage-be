/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.interview.schedule.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.skunivlikelion.homepage.domain.common.enums.Track;
import com.skunivlikelion.homepage.domain.interview.schedule.dto.request.InterviewScheduleCreateRequest;
import com.skunivlikelion.homepage.domain.interview.schedule.dto.response.InterviewScheduleResponse;
import com.skunivlikelion.homepage.domain.interview.schedule.entity.InterviewSchedule;
import com.skunivlikelion.homepage.domain.interview.schedule.exception.InterviewScheduleErrorCode;
import com.skunivlikelion.homepage.domain.interview.schedule.mapper.InterviewScheduleMapper;
import com.skunivlikelion.homepage.domain.interview.schedule.repository.InterviewScheduleRepository;
import com.skunivlikelion.homepage.domain.semester.repository.SemesterRepository;

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
  private final InterviewScheduleMapper interviewScheduleMapper;

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
          "[InterviewSchedule] 면접 일정 생성 실패 - 시간 겹침(OVERLAP) - semester={}, track={}, date={}, slot={}~{}",
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

  private void validateSemesterExists(Long semester) {
    if (semester == null || !semesterRepository.existsById(semester)) {
      log.warn("[InterviewSchedule] 기수 검증 실패 - semester={}", semester);
      throw new CustomException(InterviewScheduleErrorCode.NOT_FOUND_SEMESTER);
    }
  }

  private void validateTrack(Track track) {
    if (track == null) {
      log.warn("[InterviewSchedule] 트랙 검증 실패 - track=null");
      throw new CustomException(InterviewScheduleErrorCode.INVALID_TIME_RANGE);
    }
  }

  private void validateRequestBody(InterviewScheduleCreateRequest request) {
    if (request == null) {
      log.warn("[InterviewSchedule] 요청 바디 검증 실패 - request=null");
      throw new CustomException(InterviewScheduleErrorCode.INVALID_TIME_RANGE);
    }
  }

  private void validateTimeRange(InterviewScheduleCreateRequest request) {
    if (request.getStartTime() == null || request.getEndTime() == null) {
      log.warn("[InterviewSchedule] 시간 검증 실패 - 시간 값 누락");
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
}
