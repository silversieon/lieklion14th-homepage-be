/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.application.question.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.skunivlikelion.homepage.domain.application.question.entity.ApplicationQuestion;
import com.skunivlikelion.homepage.domain.common.enums.Track;

public interface ApplicationQuestionRepository extends JpaRepository<ApplicationQuestion, Long> {

  List<ApplicationQuestion> findAllByApplicationForm_IdAndTrackOrderByOrderNumberAsc(
      Long applicationFormId, Track track);

  @Query(
      """
              select q
              from ApplicationQuestion q
              where q.applicationForm.id = :formId
                and q.id in :questionIds
          """)
  List<ApplicationQuestion> findAllByFormIdAndQuestionIds(
      @Param("formId") Long formId, @Param("questionIds") List<Long> questionIds);

  List<ApplicationQuestion> findAllByApplicationForm_IdOrderByTrackAscOrderNumberAsc(
      Long applicationFormId);

  @Modifying(flushAutomatically = true)
  @Query("delete from ApplicationQuestion q where q.applicationForm.id = :applicationFormId")
  void deleteAllByApplicationFormId(@Param("applicationFormId") Long applicationFormId);
}
