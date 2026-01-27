/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.interview.booking.repository;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.skunivlikelion.homepage.domain.interview.booking.entity.InterviewBooking;

public interface InterviewBookingRepository extends JpaRepository<InterviewBooking, Long> {

  @Query(
      """
          select b.schedule.id
          from InterviewBooking b
          where b.schedule.id in :scheduleIds
          """)
  Set<Long> findBookedScheduleIds(@Param("scheduleIds") Set<Long> scheduleIds);
}
