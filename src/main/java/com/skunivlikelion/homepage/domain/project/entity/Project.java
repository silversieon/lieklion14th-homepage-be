/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.project.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

import com.skunivlikelion.homepage.domain.project.dto.request.ProjectUpdateRequest;
import com.skunivlikelion.homepage.domain.semester.entity.Semester;

import backend.boilerplate.common.BaseTimeEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class Project extends BaseTimeEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String title;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String content;

  @Column private boolean award;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "project_type_id", nullable = false)
  private ProjectType projectType;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "semester_id", nullable = false)
  private Semester semester;

  @Builder.Default
  @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<ProjectMember> projectMembers = new ArrayList<>();

  @Builder.Default
  @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<ProjectImage> projectImages = new ArrayList<>();

  public void update(ProjectUpdateRequest request) {
    if (request == null) return;

    if (request.getTitle() != null) {
      this.title = request.getTitle();
    }
    if (request.getContent() != null) {
      this.content = request.getContent();
    }
    this.award = request.isAward();
  }

  public void update(ProjectUpdateRequest request, Semester semester, ProjectType projectType) {
    update(request);

    if (semester != null) this.semester = semester;
    if (projectType != null) this.projectType = projectType;
  }

  public void addProjectMember(ProjectMember projectMember) {
    this.projectMembers.add(projectMember);
  }

  public void addProjectImage(ProjectImage projectImage) {
    this.projectImages.add(projectImage);
  }
}
