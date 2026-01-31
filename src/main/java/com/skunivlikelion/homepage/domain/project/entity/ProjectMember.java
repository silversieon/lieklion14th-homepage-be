/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.project.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import com.skunivlikelion.homepage.domain.common.enums.Track;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ProjectMember {

  @Enumerated(EnumType.STRING)
  @Column(name = "track", nullable = false)
  private Track track;

  @Column(name = "member_name", nullable = false)
  private String name;
}
