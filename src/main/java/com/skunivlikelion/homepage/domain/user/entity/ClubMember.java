/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.user.entity;

import jakarta.persistence.*;

import com.skunivlikelion.homepage.domain.common.enums.Track;
import com.skunivlikelion.homepage.domain.semester.entity.Semester;
import com.skunivlikelion.homepage.domain.user.enums.Position;

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
public class ClubMember extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Track track;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Position position;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "semester_id")
  private Semester semester;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  public void updateClubMemberRecord(Semester semester, Position position, Track track) {
    this.semester = semester;
    this.position = position;
    this.track = track;
  }
}
