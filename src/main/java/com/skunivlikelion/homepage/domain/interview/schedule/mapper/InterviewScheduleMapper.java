/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.interview.schedule.mapper;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.skunivlikelion.homepage.domain.common.enums.Track;
import com.skunivlikelion.homepage.domain.interview.schedule.dto.request.InterviewScheduleCreateRequest;
import com.skunivlikelion.homepage.domain.interview.schedule.dto.response.InterviewScheduleResponse;
import com.skunivlikelion.homepage.domain.interview.schedule.entity.InterviewSchedule;

@Component
public class InterviewScheduleMapper {

  public InterviewSchedule toEntity(
      Long semester, Track track, InterviewScheduleCreateRequest request) {
    return InterviewSchedule.builder()
        .semester(semester)
        .track(track)
        .date(request.getDate())
        .startTime(request.getStartTime())
        .endTime(request.getEndTime())
        .build();
  }

  public InterviewScheduleResponse toResponse(InterviewSchedule s) {
    return toResponse(s, false);
  }

  public InterviewScheduleResponse toResponse(InterviewSchedule s, boolean booked) {
    return InterviewScheduleResponse.builder()
        .id(s.getId())
        .semester(s.getSemester())
        .track(s.getTrack())
        .date(s.getDate())
        .startTime(s.getStartTime())
        .endTime(s.getEndTime())
        .booked(booked)
        .build();
  }

  public List<InterviewScheduleResponse> toResponseList(List<InterviewSchedule> list) {
    return list.stream().map(this::toResponse).toList();
  }

  public List<InterviewScheduleResponse> toResponseList(
      List<InterviewSchedule> schedules, Set<Long> bookedIds) {

    return schedules.stream()
        .map(s -> toResponse(s, bookedIds != null && bookedIds.contains(s.getId())))
        .toList();
  }
}
