/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.interview.booking.dto.response;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record AdminInterviewBookingInfiniteResponse(
    int semester,
    List<String> tracks,
    List<Item> items,
    String nextCursor,
    boolean hasNext,
    int size) {

  public record Item(
      Long scheduleId,
      String track,
      LocalDate date,
      LocalTime startTime,
      LocalTime endTime,
      boolean booked,
      BookingInfo bookingInfo) {}

  public record BookingInfo(
      Long bookingId,
      String name,
      String department, // 유저 존재 시만 값, 삭제 시 null
      String studentNumber,
      String phone, // 유저 존재 시만 값, 삭제 시 null
      Long applicationRecordId) {}
}
