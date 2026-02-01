/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.application.form.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;

import com.skunivlikelion.homepage.domain.semester.entity.Semester;
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
public class ApplicationForm extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToOne
  @JoinColumn(name = "semester_id", nullable = false, unique = true)
  private Semester semester;

  @Column(nullable = false)
  private LocalDateTime openAt;

  @Column(nullable = false)
  private LocalDateTime closeAt;

  @Column(nullable = false)
  private LocalDateTime applicationResultAt;

  @Column(nullable = false)
  private LocalDateTime interviewScheduleConfirmedAt;

  @Column(nullable = false)
  private LocalDateTime finalResultAt;

  @Column(nullable = false)
  @Builder.Default
  private boolean hasQuestions = false;

  public void update(
      Semester semester,
      LocalDateTime openAt,
      LocalDateTime closeAt,
      LocalDateTime applicationResultAt,
      LocalDateTime interviewScheduleConfirmedAt,
      LocalDateTime finalResultAt) {
    this.semester = semester;
    this.openAt = openAt;
    this.closeAt = closeAt;
    this.applicationResultAt = applicationResultAt;
    this.interviewScheduleConfirmedAt = interviewScheduleConfirmedAt;
    this.finalResultAt = finalResultAt;
  }

  public void markHasQuestions() {
    this.hasQuestions = true;
  }

  public void unmarkHasQuestions() {
    this.hasQuestions = false;
  }

  public boolean isAfterApplicationResultAt(LocalDateTime now) {
    return now.isAfter(this.applicationResultAt) || now.isEqual(this.applicationResultAt);
  }

  public boolean isAfterInterviewScheduleConfirmedAt(LocalDateTime now) {
    return now.isAfter(this.interviewScheduleConfirmedAt)
        || now.isEqual(this.interviewScheduleConfirmedAt);
  }
}
