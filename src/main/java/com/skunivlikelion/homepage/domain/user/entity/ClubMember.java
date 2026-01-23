/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.user.entity;

import jakarta.persistence.*;

import com.skunivlikelion.homepage.domain.common.enums.Track;
import com.skunivlikelion.homepage.domain.user.enums.Position;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class ClubMember {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private Long semester;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Track track;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Position position;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  public void updateClubMemberRecord(Long semester, Position position, Track track) {
    this.semester = semester;
    this.position = position;
    this.track = track;
  }
}
