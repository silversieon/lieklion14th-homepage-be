/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.project.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.skunivlikelion.homepage.domain.common.enums.Track;
import com.skunivlikelion.homepage.domain.project.entity.Project;
import com.skunivlikelion.homepage.domain.project.entity.ProjectMember;
import com.skunivlikelion.homepage.domain.project.repository.ProjectMemberRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectMemberServiceImpl implements ProjectMemberService {

  private final ProjectMemberRepository projectMemberRepository;

  @Override
  public void createProjectMembers(Project project, Map<Track, List<String>> projectMembers) {
    Set<Track> tracks = projectMembers.keySet();
    for (Track track : tracks) {
      List<String> projectMemberNames = projectMembers.get(track);
      for (String name : projectMemberNames) {
        ProjectMember projectMember =
            ProjectMember.builder().track(track).projectMemberName(name).project(project).build();
        ProjectMember savedProjectMember = projectMemberRepository.save(projectMember);
        project.addProjectMember(savedProjectMember);
      }
    }
  }

  @Override
  public void updateProjectMembers(
      Project project, List<Long> remainingMemberIds, Map<Track, List<String>> newMembers) {
    deleteProjectMembersByRemainingMemberIds(project, remainingMemberIds);
    createProjectMembers(project, newMembers);
  }

  private void deleteProjectMembersByRemainingMemberIds(
      Project project, List<Long> remainingMemberIds) {
    project
        .getProjectMembers()
        .removeIf(
            pm -> {
              boolean remove = !remainingMemberIds.contains(pm.getId());
              if (remove) {
                pm.setProject(null);
              }
              return remove;
            });
  }
}
