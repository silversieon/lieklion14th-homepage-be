/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.project.service;

import java.util.List;
import java.util.Map;

import com.skunivlikelion.homepage.domain.common.enums.Track;
import com.skunivlikelion.homepage.domain.project.entity.Project;

public interface ProjectMemberService {

  void createProjectMembers(Project project, Map<Track, List<String>> projectMembers);

  void updateProjectMembers(
      Project project, List<Long> remainingMemberIds, Map<Track, List<String>> newMembers);
}
