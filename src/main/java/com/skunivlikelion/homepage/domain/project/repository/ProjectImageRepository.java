/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.project.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.skunivlikelion.homepage.domain.project.entity.ProjectImage;

public interface ProjectImageRepository extends JpaRepository<ProjectImage, Long> {

  List<ProjectImage> findImagesByProjectId(Long projectId);
}
