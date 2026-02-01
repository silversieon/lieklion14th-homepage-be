/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.interview.schedule.dto.response;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import com.skunivlikelion.homepage.domain.common.enums.Track;

public record UserInterviewScheduleResponse(
    Integer semester, boolean documentPassed, Track track, List<DateGroup> dates) {

  public record DateGroup(LocalDate date, List<TimeSlot> times) {}

  public record TimeSlot(Long scheduleId, LocalTime startTime, LocalTime endTime, boolean booked) {}
}
