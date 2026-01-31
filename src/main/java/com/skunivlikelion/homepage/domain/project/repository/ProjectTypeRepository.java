/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.project.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.skunivlikelion.homepage.domain.project.entity.ProjectType;

public interface ProjectTypeRepository extends JpaRepository<ProjectType, Long> {
  boolean existsByProjectTypeName(String projectTypeName);

  List<ProjectType> findAllByOrderByIdAsc();

  @Modifying
  @Query(value = "DELETE FROM project_type WHERE id = :projectTypeId", nativeQuery = true)
  int deleteByProjectTypeIdNative(@Param("projectTypeId") Long projectTypeId);
}
