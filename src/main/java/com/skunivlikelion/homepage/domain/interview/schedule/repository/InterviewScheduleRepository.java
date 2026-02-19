/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.interview.schedule.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import jakarta.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.skunivlikelion.homepage.domain.common.enums.Track;
import com.skunivlikelion.homepage.domain.interview.booking.repository.AdminInterviewSlotView;
import com.skunivlikelion.homepage.domain.interview.schedule.entity.InterviewSchedule;

public interface InterviewScheduleRepository extends JpaRepository<InterviewSchedule, Long> {

  @Query(
      """
          select s
          from InterviewSchedule s
          where s.semester = :semester
            and (:track is null or s.track = :track)
            and (:dateFrom is null or s.date >= :dateFrom)
            and (:dateTo is null or s.date <= :dateTo)
          order by s.track asc, s.date asc, s.startTime asc
          """)
  List<InterviewSchedule> findAdminSchedules(
      @Param("semester") Long semester,
      @Param("track") Track track,
      @Param("dateFrom") LocalDate dateFrom,
      @Param("dateTo") LocalDate dateTo);

  @Query(
      """
          select s
          from InterviewSchedule s
          where (:semester is null or s.semester = :semester)
            and (:track is null or s.track = :track)
            and (:dateFrom is null or s.date >= :dateFrom)
            and (:dateTo is null or s.date <= :dateTo)
          order by s.date asc, s.startTime asc
          """)
  List<InterviewSchedule> findUserSchedules(
      @Param("semester") Long semester,
      @Param("track") Track track,
      @Param("dateFrom") LocalDate dateFrom,
      @Param("dateTo") LocalDate dateTo);

  @Query(
      """
          select distinct s.track
          from InterviewSchedule s
          where s.semester = :semester
          order by s.track asc
          """)
  List<Track> findDistinctTracksBySemester(@Param("semester") Long semester);

  // 예약 동시성 대비(선착순): 동일 슬롯 예약 충돌 방지용 row lock
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("select s from InterviewSchedule s where s.id = :scheduleId")
  Optional<InterviewSchedule> findByIdForUpdate(@Param("scheduleId") Long scheduleId);

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
              from InterviewSchedule s
              join InterviewBooking b on b.interviewSchedule.id = s.id
              where s.semester = :semester
                and s.date = :date
                and (:track is null or s.track = :track)
              order by s.track asc, s.startTime asc, s.id asc
          """)
  List<AdminInterviewSlotView> findAdminBookedSchedulesBySemesterAndDate(
      @Param("semester") Long semester, @Param("date") LocalDate date, @Param("track") Track track);

  @Query(
      value =
          """
                select count(1)
                from interview_schedule s
                where s.semester = :semester
                  and s.track = :track
                  and s.date between date_sub(:date, interval 1 day) and date_add(:date, interval 1 day)
                  and (
                    timestamp(:date, :startTime)
                      < timestamp(date_add(s.date, interval case when s.end_time < s.start_time then 1 else 0 end day), s.end_time)
                    and
                    timestamp(date_add(:date, interval case when :endTime < :startTime then 1 else 0 end day), :endTime)
                      > timestamp(s.date, s.start_time)
                  )
              """,
      nativeQuery = true)
  Long countOverlappingScheduleOvernight(
      @Param("semester") Long semester,
      @Param("track") String track,
      @Param("date") LocalDate date,
      @Param("startTime") LocalTime startTime,
      @Param("endTime") LocalTime endTime);
}
