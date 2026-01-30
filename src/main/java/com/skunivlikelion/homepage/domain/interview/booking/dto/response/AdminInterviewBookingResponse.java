/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.interview.booking.dto.response;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record AdminInterviewBookingResponse(Integer semester, List<TrackGroup> tracks) {

  public record TrackGroup(String track, List<DateGroup> dates) {}

  public record DateGroup(LocalDate date, List<TimeSlot> times) {}

  public record TimeSlot(
      Long scheduleId,
      LocalTime startTime,
      LocalTime endTime,
      boolean booked,
      BookingInfo bookingInfo) {}

  public record BookingInfo(
      Long bookingId,
      String name,
      String department,
      String studentNumber,
      String phoneNumber,
      Long applicationRecordId) {}
}
