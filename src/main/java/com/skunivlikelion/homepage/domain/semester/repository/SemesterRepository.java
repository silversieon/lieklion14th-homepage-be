/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.semester.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.skunivlikelion.homepage.domain.semester.entity.Semester;

public interface SemesterRepository extends JpaRepository<Semester, Long> {

  boolean existsBySemester(Long semester);

  List<Semester> findAllByOrderBySemesterDesc();
}
