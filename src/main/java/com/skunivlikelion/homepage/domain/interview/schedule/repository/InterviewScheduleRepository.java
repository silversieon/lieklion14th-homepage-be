/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.interview.schedule.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.skunivlikelion.homepage.domain.common.enums.Track;
import com.skunivlikelion.homepage.domain.interview.schedule.entity.InterviewSchedule;

public interface InterviewScheduleRepository extends JpaRepository<InterviewSchedule, Long> {

  @Query(
      """
          select count(s) > 0
          from InterviewSchedule s
          where s.semester = :semester
            and s.track = :track
            and s.date = :date
            and :startTime < s.endTime
            and :endTime > s.startTime
          """)
  boolean existsOverlappingSchedule(
      @Param("semester") Long semester,
      @Param("track") Track track,
      @Param("date") LocalDate date,
      @Param("startTime") LocalTime startTime,
      @Param("endTime") LocalTime endTime);

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
}
