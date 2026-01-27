/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.application.form.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import jakarta.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.skunivlikelion.homepage.domain.application.form.entity.ApplicationForm;

public interface ApplicationFormRepository extends JpaRepository<ApplicationForm, Long> {

  Optional<ApplicationForm> findBySemester_Semester(Long semester);

  boolean existsBySemester_Semester(Long semester);

  @Query(
      """
          select af
          from ApplicationForm af
          join fetch af.semester s
          order by s.semester desc
          """)
  List<ApplicationForm> findAllWithSemesterOrderBySemesterDesc();

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query(
      """
          select af
          from ApplicationForm af
          join af.semester s
          where s.semester = :semester
          """)
  Optional<ApplicationForm> findBySemesterForUpdate(@Param("semester") Long semester);

  @Query(
      """
          select af
          from ApplicationForm af
          join fetch af.semester s
          where af.hasQuestions = true
          order by s.semester desc
          """)
  List<ApplicationForm> findAllConfiguredWithSemesterOrderBySemesterDesc();

  @Query(
      """
          select af
          from ApplicationForm af
          join fetch af.semester s
          where af.hasQuestions = false
            and af.closeAt > :now
          order by s.semester desc
          """)
  List<ApplicationForm> findAllAvailableForQuestionRegistrationWithSemesterOrderBySemesterDesc(
      @Param("now") LocalDateTime now);

  @Query(
      """
              select f
              from ApplicationForm f
              where f.semester.semester = :semester
          """)
  Optional<ApplicationForm> findBySemester(@Param("semester") Long semester);
}
