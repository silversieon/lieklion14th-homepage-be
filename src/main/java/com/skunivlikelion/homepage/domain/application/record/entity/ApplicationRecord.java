/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.application.record.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import com.skunivlikelion.homepage.domain.application.form.entity.ApplicationForm;
import com.skunivlikelion.homepage.domain.common.enums.Track;
import com.skunivlikelion.homepage.domain.user.entity.User;

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
@Table(
    uniqueConstraints = {
      @UniqueConstraint(columnNames = {"application_form_id", "user_id", "track"})
    })
public class ApplicationRecord extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "application_form_id", nullable = false)
  private ApplicationForm applicationForm;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Track track;

  @Builder.Default
  @Column(nullable = false)
  private boolean isSubmitted = false;

  private LocalDateTime submittedAt;

  @Builder.Default
  @Column(nullable = false)
  private boolean isDocumentPassed = false;

  @Builder.Default
  @Column(nullable = false)
  private boolean isInterviewPassed = false;

  public void markSubmitted(LocalDateTime submittedAt) {
    this.isSubmitted = true;
    this.submittedAt = submittedAt;
  }

  public void markDocumentPassed() {
    this.isDocumentPassed = true;
  }

  public void unmarkDocumentPassed() {
    this.isDocumentPassed = false;
  }

  public void markInterviewPassed() {
    this.isInterviewPassed = true;
  }

  public void unmarkInterviewPassed() {
    this.isInterviewPassed = false;
  }

  public void changeTrack(Track track) {
    this.track = track;
  }
}
