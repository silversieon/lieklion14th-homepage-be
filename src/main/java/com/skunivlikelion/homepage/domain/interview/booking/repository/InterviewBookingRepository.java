/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.interview.booking.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import jakarta.persistence.LockModeType;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.skunivlikelion.homepage.domain.common.enums.Track;
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

  @Query(
      """
            select
              b.id as bookingId,
              b.userId as userId,

              s.id as scheduleId,
              s.track as track,
              s.date as date,
              s.startTime as startTime,
              s.endTime as endTime,

              b.userNameMasked as snapshotName,
              b.userStudentNumberMasked as snapshotStudentNumber,
              b.applicationRecordId as applicationRecordId

            from InterviewBooking b
            join b.interviewSchedule s
            where b.semesterId = :semester
              and (:track is null or b.track = :track)
              and (:dateFrom is null or s.date >= :dateFrom)
              and (:dateTo is null or s.date <= :dateTo)
              and (
                :search is null
                or lower(b.userNameMasked) like concat('%', :search, '%')
                or lower(b.userStudentNumberMasked) like concat('%', :search, '%')
              )
            order by s.track asc, s.date asc, s.startTime asc
          """)
  List<AdminInterviewBookingView> findAdminBookings(
      @Param("semester") Long semester,
      @Param("track") Track track,
      @Param("dateFrom") LocalDate dateFrom,
      @Param("dateTo") LocalDate dateTo,
      @Param("search") String search);

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

  @Query(
      """
            select
              s.id as scheduleId,
              s.track as track,
              s.date as date,
              s.startTime as startTime,
              s.endTime as endTime,

              b.id as bookingId,
              b.userId as userId,
              b.userNameMasked as snapshotName,
              b.userStudentNumberMasked as snapshotStudentNumber,
              b.applicationRecordId as applicationRecordId

            from InterviewBooking b
            join b.interviewSchedule s
            where b.semesterId = :semester
              and (:track is null or s.track = :track)
              and (:dateFrom is null or s.date >= :dateFrom)
              and (:dateTo is null or s.date <= :dateTo)

              and (
                :search is null
                or lower(b.userNameMasked) like concat('%', :search, '%')
                or lower(b.userStudentNumberMasked) like concat('%', :search, '%')
              )

              and (
                :cursorTrack is null
                or (
                  s.track > :cursorTrack
                  or (s.track = :cursorTrack and s.date > :cursorDate)
                  or (s.track = :cursorTrack and s.date = :cursorDate and s.startTime > :cursorStartTime)
                  or (
                    s.track = :cursorTrack and s.date = :cursorDate and s.startTime = :cursorStartTime
                    and s.id > :cursorScheduleId
                  )
                )
              )

            order by s.track asc, s.date asc, s.startTime asc, s.id asc
          """)
  List<AdminInterviewSlotView> findAdminBookedSlotsInfinite(
      @Param("semester") Long semester,
      @Param("track") Track track,
      @Param("dateFrom") LocalDate dateFrom,
      @Param("dateTo") LocalDate dateTo,
      @Param("search") String search,
      @Param("cursorTrack") Track cursorTrack,
      @Param("cursorDate") LocalDate cursorDate,
      @Param("cursorStartTime") LocalTime cursorStartTime,
      @Param("cursorScheduleId") Long cursorScheduleId,
      Pageable pageable);
}
