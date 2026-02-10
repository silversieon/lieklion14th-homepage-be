/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.interview.booking.repository;

import java.util.Optional;
import java.util.Set;

import jakarta.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.skunivlikelion.homepage.domain.interview.booking.entity.InterviewBooking;

public interface InterviewBookingRepository extends JpaRepository<InterviewBooking, Long> {

  @Query(
      """
          select b.interviewSchedule.id
          from InterviewBooking b
          where b.interviewSchedule.id in :scheduleIds
          """)
  Set<Long> findBookedScheduleIds(@Param("scheduleIds") Set<Long> scheduleIds);

  boolean existsByInterviewSchedule_Id(Long scheduleId);

  boolean existsBySemesterIdAndApplicantKey(Long semesterId, String applicantKey);

  Optional<InterviewBooking> findBySemesterIdAndApplicantKey(Long semesterId, String applicantKey);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query(
      """
              select b
              from InterviewBooking b
              where b.semesterId = :semesterId
                and b.applicantKey = :applicantKey
          """)
  Optional<InterviewBooking> findBySemesterIdAndApplicantKeyForUpdate(
      @Param("semesterId") Long semesterId, @Param("applicantKey") String applicantKey);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query(
      """
            select b
            from InterviewBooking b
            join fetch b.interviewSchedule s
            where b.id = :bookingId
          """)
  Optional<InterviewBooking> findByIdForUpdate(@Param("bookingId") Long bookingId);
}
