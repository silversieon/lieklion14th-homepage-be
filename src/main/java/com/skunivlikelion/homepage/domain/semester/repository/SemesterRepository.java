/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.semester.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.skunivlikelion.homepage.domain.semester.entity.Semester;

public interface SemesterRepository extends JpaRepository<Semester, Long> {

  boolean existsBySemester(Long semester);

  List<Semester> findAllByOrderBySemesterDesc();

  Optional<Semester> findFirstByOrderBySemesterDesc();

  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query(value = "delete from semester where semester = :semester", nativeQuery = true)
  int deleteBySemesterNative(@Param("semester") Long semester);
}
