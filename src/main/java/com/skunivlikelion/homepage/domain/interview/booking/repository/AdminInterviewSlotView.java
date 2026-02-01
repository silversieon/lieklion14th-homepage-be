/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.interview.booking.repository;

import java.time.LocalDate;
import java.time.LocalTime;

import com.skunivlikelion.homepage.domain.common.enums.Track;

public interface AdminInterviewSlotView {

  Long getScheduleId();

  Track getTrack();

  LocalDate getDate();

  LocalTime getStartTime();

  LocalTime getEndTime();

  // booking (nullable)
  Long getBookingId();

  Long getUserId();

  String getSnapshotName();

  String getSnapshotStudentNumber();

  Long getApplicationRecordId();
}
