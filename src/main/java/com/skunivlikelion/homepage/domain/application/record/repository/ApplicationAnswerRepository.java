/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.application.record.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.skunivlikelion.homepage.domain.application.record.entity.ApplicationAnswer;
import com.skunivlikelion.homepage.domain.common.enums.Track;

public interface ApplicationAnswerRepository extends JpaRepository<ApplicationAnswer, Long> {

  boolean existsByQuestion_ApplicationForm_Id(Long applicationFormId);

  @Query(
      """
              select a
              from ApplicationAnswer a
              where a.applicationRecord.id = :recordId
                and a.question.id in :questionIds
          """)
  List<ApplicationAnswer> findAllByRecordIdAndQuestionIds(
      @Param("recordId") Long recordId, @Param("questionIds") List<Long> questionIds);

  @Modifying(flushAutomatically = true)
  @Query(
      """
              delete from ApplicationAnswer a
              where a.applicationRecord.id = :recordId
                and a.question.track = :track
          """)
  int deleteAnswersByRecordIdAndTrack(
      @Param("recordId") Long recordId, @Param("track") Track track);

  @Query(
      """
              select a
              from ApplicationAnswer a
              join fetch a.question q
              where a.applicationRecord.id = :recordId
          """)
  List<ApplicationAnswer> findAllWithQuestionByRecordId(@Param("recordId") Long recordId);

  @Query(
      """
          select a
          from ApplicationAnswer a
          join fetch a.question q
          where a.applicationRecord.id = :recordId
            and q.track = :track
          order by q.orderNumber asc
          """)
  List<ApplicationAnswer> findAllWithQuestionByRecordIdAndTrack(
      @Param("recordId") Long recordId, @Param("track") Track track);

  @Modifying(flushAutomatically = true)
  @Query(
      value =
          """
          insert into application_answer (question_id, application_record_id, content)
          select q.id, :recordId, ''
          from application_question q
          where q.application_form_id = :formId
            and q.track in (:commonTrack, :track)
            and not exists (
              select 1
              from application_answer a
              where a.application_record_id = :recordId
                and a.question_id = q.id
            )
          """,
      nativeQuery = true)
  int insertMissingAnswersForRecord(
      @Param("recordId") Long recordId,
      @Param("formId") Long formId,
      @Param("commonTrack") String commonTrack,
      @Param("track") String track);
}
