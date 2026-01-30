/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.interview.booking.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import jakarta.persistence.LockModeType;

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
              s.id as scheduleId,
              s.track as track,
              s.date as date,
              s.startTime as startTime,
              s.endTime as endTime,

              b.userName as name,
              b.userDepartment as department,
              b.userStudentNumber as studentNumber,
              b.userPhoneNumber as phoneNumber,
              b.applicationRecordId as applicationRecordId

            from InterviewBooking b
            join b.interviewSchedule s
            where b.semesterId = :semester
              and (:track is null or b.track = :track)
              and (:dateFrom is null or s.date >= :dateFrom)
              and (:dateTo is null or s.date <= :dateTo)
              and (
                :search is null
                or lower(b.userName) like concat('%', :search, '%')
                or lower(b.userDepartment) like concat('%', :search, '%')
                or lower(b.userStudentNumber) like concat('%', :search, '%')
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
}
