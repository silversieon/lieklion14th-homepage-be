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

  @Query(
      """
              select a
              from ApplicationAnswer a
              where a.applicationRecord.id = :recordId
                and a.question.id in :questionIds
          """)
  List<ApplicationAnswer> findAllByRecordIdAndQuestionIds(
      @Param("recordId") Long recordId, @Param("questionIds") List<Long> questionIds);

  @Query(
      """
              select distinct q.id
              from ApplicationAnswer a
              join a.question q
              where a.applicationRecord.id = :recordId
          """)
  List<Long> findQuestionIdsByRecordId(@Param("recordId") Long recordId);

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
}
