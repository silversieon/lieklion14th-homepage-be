/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.application.form.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

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
}
