/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.project.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.skunivlikelion.homepage.domain.project.entity.ProjectMember;

public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Long> {

  List<ProjectMember> findByProjectId(Long projectId);

  @Modifying(clearAutomatically = true)
  @Query(
      "delete from ProjectMember pm where pm.project.id = :projectId AND pm.id not in :remainingIds")
  void deleteNotInIds(
      @Param("projectId") Long projectId, @Param("remainingIds") List<Long> remainingIds);
}
