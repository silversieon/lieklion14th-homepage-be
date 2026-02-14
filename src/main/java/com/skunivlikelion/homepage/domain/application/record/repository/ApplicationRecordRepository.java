/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.application.record.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.skunivlikelion.homepage.domain.application.record.dto.response.AdminApplicantListItem;
import com.skunivlikelion.homepage.domain.application.record.entity.ApplicationRecord;
import com.skunivlikelion.homepage.domain.common.enums.Track;

public interface ApplicationRecordRepository extends JpaRepository<ApplicationRecord, Long> {

  Optional<ApplicationRecord> findByApplicationFormIdAndUserId(Long formId, Long userId);

  @Query(
      """
              select count(r) > 0
              from ApplicationRecord r
              where r.applicationForm.id = :formId
                and r.user.id = :userId
                and r.isSubmitted = true
          """)
  boolean existsSubmitted(@Param("formId") Long formId, @Param("userId") Long userId);

  @Query(
      """
        select r
        from ApplicationRecord r
        join fetch r.applicationForm f
        where f.semester.semester = :semesterId
          and r.user.id = :userId
          and r.isSubmitted = true
      """)
  Optional<ApplicationRecord> findSubmittedBySemesterAndUserId(
      @Param("semesterId") Long semesterId, @Param("userId") Long userId);

  @Query(
      """
          select new com.skunivlikelion.homepage.domain.application.record.dto.response.AdminApplicantListItem(
              r.id,
              u.name,
              u.department,
              u.studentNumber,
              r.track,
              r.isDocumentPassed,
              r.isInterviewPassed
          )
          from ApplicationRecord r
          join r.user u
          where r.isSubmitted = true
            and (:semester is null or r.applicationForm.semester.semester = :semester)
            and (:track is null or r.track = :track)
            and (:lastCursor is null or r.id < :lastCursor)
            and (
              :search is null
              or lower(u.name) like concat('%', :search, '%')
              or lower(u.department) like concat('%', :search, '%')
              or lower(u.studentNumber) like concat('%', :search, '%')
            )
          order by r.id desc
          """)
  List<AdminApplicantListItem> findAdminApplicantListItemsInfinite(
      @Param("semester") Long semester,
      @Param("track") Track track,
      @Param("search") String search,
      @Param("lastCursor") Long lastCursor,
      Pageable pageable);

  @Query(
      """
          select r
          from ApplicationRecord r
          join fetch r.user u
          join fetch r.applicationForm f
          join fetch f.semester s
          where r.id = :recordId
            and r.isSubmitted = true
          """)
  Optional<ApplicationRecord> findSubmittedWithUserAndForm(@Param("recordId") Long recordId);

  boolean existsByApplicationFormId(Long applicationFormId);

  @Query("select r.id from ApplicationRecord r where r.id in :recordIds")
  List<Long> findExistingIds(@Param("recordIds") List<Long> recordIds);

  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query("delete from ApplicationRecord r where r.id in :recordIds")
  int bulkDeleteByIds(@Param("recordIds") List<Long> recordIds);
}
