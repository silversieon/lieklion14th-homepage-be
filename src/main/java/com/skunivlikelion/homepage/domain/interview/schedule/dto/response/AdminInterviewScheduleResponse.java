/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.interview.schedule.dto.response;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record AdminInterviewScheduleResponse(Integer semester, List<TrackGroup> tracks) {

  public record TrackGroup(String track, List<DateGroup> dates) {}

  public record DateGroup(LocalDate date, List<TimeSlot> times) {}

  public record TimeSlot(LocalTime startTime, LocalTime endTime, boolean booked) {}
}
