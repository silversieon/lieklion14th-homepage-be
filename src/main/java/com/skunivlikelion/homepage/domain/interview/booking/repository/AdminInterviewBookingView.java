/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.interview.booking.repository;

import java.time.LocalDate;
import java.time.LocalTime;

import com.skunivlikelion.homepage.domain.common.enums.Track;

public interface AdminInterviewBookingView {

  Long getScheduleId();

  Long getSemester();

  Track getTrack();

  LocalDate getDate();

  LocalTime getStartTime();

  LocalTime getEndTime();

  // booking
  Long getBookingId();

  // snapshot - user info
  String getName();

  String getDepartment();

  String getStudentNumber();

  String getPhoneNumber();

  Long getApplicationRecordId();
}
