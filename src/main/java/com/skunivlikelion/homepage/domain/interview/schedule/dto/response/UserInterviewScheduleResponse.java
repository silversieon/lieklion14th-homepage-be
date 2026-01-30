/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.interview.schedule.dto.response;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record UserInterviewScheduleResponse(Integer semester, List<DateGroup> dates) {

  public record DateGroup(LocalDate date, List<TimeSlot> times) {}

  public record TimeSlot(Long scheduleId, LocalTime startTime, LocalTime endTime, boolean booked) {}
}
