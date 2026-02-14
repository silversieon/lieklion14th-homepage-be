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
import com.skunivlikelion.homepage.global.common.BaseTimeEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"application_form_id", "user_id"})})
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

  private Boolean isDocumentPassed;

  private Boolean isInterviewPassed;

  public void markSubmitted(LocalDateTime submittedAt) {
    this.isSubmitted = true;
    this.submittedAt = submittedAt;
  }

  public void passDocument() {
    this.isDocumentPassed = true;
  }

  public void failDocument() {
    this.isDocumentPassed = false;
  }

  public void passInterview() {
    this.isInterviewPassed = true;
  }

  public void failInterview() {
    this.isInterviewPassed = false;
  }

  public void changeTrack(Track track) {
    this.track = track;
  }

  public void resetInterviewResult() {
    this.isInterviewPassed = null;
  }
}
