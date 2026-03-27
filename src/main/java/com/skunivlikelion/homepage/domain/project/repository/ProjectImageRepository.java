/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.project.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.skunivlikelion.homepage.domain.project.entity.ProjectImage;

public interface ProjectImageRepository extends JpaRepository<ProjectImage, Long> {

  @Query(
      """
        select pi.imageUrl from ProjectImage pi
                where pi.project.id = :projectId
        """)
  List<String> findProjectImageUrlByProject_Id(@Param("projectId") Long projectId);

  Optional<ProjectImage> findFirstByProjectId(Long projectId);
}
