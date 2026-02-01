/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.project.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.skunivlikelion.homepage.domain.project.entity.Project;
import com.skunivlikelion.homepage.domain.project.entity.ProjectType;

public interface ProjectRepository extends JpaRepository<Project, Long> {

  @Query(
      """
        SELECT p
        From Project p
        WHERE (:projectTypeId IS NULL OR p.projectType.id = :projectTypeId)
          AND (:semester IS NULL OR p.semester.semester = :semester)
          AND (
                :search IS NULL OR :search = ''
                OR LOWER(p.title) LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(p.content) LIKE LOWER(CONCAT('%', :search, '%'))
              )
          ORDER BY p.createdAt DESC
        """)
  Page<Project> findProjectsByFilters(
      @Param("projectTypeId") Long projectTypeId,
      @Param("semester") Long semester,
      @Param("search") String search,
      Pageable pageable);

  boolean existsByTitleAndSemester_SemesterAndProjectType(
      String title, Long semester, ProjectType projectType);

  boolean existsByTitleAndSemester_SemesterAndProjectTypeAndIdNot(
      String title, Long semester, ProjectType projectType, Long id);

  @Query(
      """
        SELECT p
        FROM Project p
        WHERE p.award = true
        AND (:projectId IS NULL OR p.id < :projectId)
        ORDER BY p.id DESC
        """)
  List<Project> findAwardProjects(Pageable pageable, @Param("projectId") Long projectId);
}
