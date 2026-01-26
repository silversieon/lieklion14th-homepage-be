/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.interview.schedule.mapper;

import java.util.List;

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

  public InterviewScheduleResponse toResponse(InterviewSchedule entity) {
    return InterviewScheduleResponse.builder()
        .id(entity.getId())
        .semester(entity.getSemester())
        .track(entity.getTrack())
        .date(entity.getDate())
        .startTime(entity.getStartTime())
        .endTime(entity.getEndTime())
        .build();
  }

  public List<InterviewScheduleResponse> toResponseList(List<InterviewSchedule> list) {
    return list.stream().map(this::toResponse).toList();
  }
}
