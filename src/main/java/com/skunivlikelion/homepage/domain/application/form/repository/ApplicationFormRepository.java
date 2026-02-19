/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.application.form.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import jakarta.persistence.LockModeType;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.skunivlikelion.homepage.domain.application.form.entity.ApplicationForm;

public interface ApplicationFormRepository extends JpaRepository<ApplicationForm, Long> {

  Optional<ApplicationForm> findBySemester_Semester(Long semester);

  boolean existsBySemester_Semester(Long semester);

  boolean existsBySemester_SemesterAndIdNot(Long semester, Long id);

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

  @Query(
      """
          select (count(af) > 0)
          from ApplicationForm af
          where :newOpenAt <= af.finalResultAt
            and :newFinalResultAt >= af.openAt
          """)
  boolean existsOverlappedApplicationForm(
      @Param("newOpenAt") LocalDateTime newOpenAt,
      @Param("newFinalResultAt") LocalDateTime newFinalResultAt);

  // 수정하기 전을 제외하고 모집 기간이 겹치는지 확인
  @Query(
      """
          select (count(af) > 0)
          from ApplicationForm af
          where af.id <> :excludeId
            and :newOpenAt <= af.finalResultAt
            and :newFinalResultAt >= af.openAt
          """)
  boolean existsOverlappedApplicationFormExcludingId(
      @Param("excludeId") Long excludeId,
      @Param("newOpenAt") LocalDateTime newOpenAt,
      @Param("newFinalResultAt") LocalDateTime newFinalResultAt);

  @Query(
      """
          select af
          from ApplicationForm af
          join fetch af.semester s
          where af.openAt <= :now
            and af.finalResultAt >= :now
          """)
  Optional<ApplicationForm> findCurrentApplicationForm(@Param("now") LocalDateTime now);

  @Query(
      """
      select af
      from ApplicationForm af
      join fetch af.semester s
      where af.openAt > :now
      order by af.openAt asc
  """)
  List<ApplicationForm> findNextApplicationForm(@Param("now") LocalDateTime now, Pageable pageable);

  @Query(
      """
      select af
      from ApplicationForm af
      join fetch af.semester s
      where af.finalResultAt < :now
      order by af.finalResultAt desc
  """)
  List<ApplicationForm> findPrevApplicationForm(@Param("now") LocalDateTime now, Pageable pageable);

  @Query(
      """
          select af
          from ApplicationForm af
          join fetch af.semester s
          where af.openAt <= :now
            and af.closeAt > :now
          """)
  Optional<ApplicationForm> findSubmittableApplicationForm(@Param("now") LocalDateTime now);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query(
      """
          select af
          from ApplicationForm af
          join fetch af.semester s
          where af.id = :applicationFormId
          """)
  Optional<ApplicationForm> findByIdForUpdate(@Param("applicationFormId") Long applicationFormId);

  @Query(
      """
          select af
          from ApplicationForm af
          join fetch af.semester s
          where af.openAt <= :now
          and af.finalResultAt >= :threshold
          """)
  Optional<ApplicationForm> findCurrentOrGraceApplicationForm(
      @Param("now") LocalDateTime now, @Param("threshold") LocalDateTime threshold);
}
