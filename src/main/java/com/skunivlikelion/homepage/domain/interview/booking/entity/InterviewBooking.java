/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.interview.booking.entity;

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

import com.skunivlikelion.homepage.domain.common.enums.Track;
import com.skunivlikelion.homepage.domain.interview.schedule.entity.InterviewSchedule;
import com.skunivlikelion.homepage.global.common.BaseTimeEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(
    name = "interview_booking",
    uniqueConstraints = {
      @UniqueConstraint(
          name = "uk_interview_booking_schedule",
          columnNames = {"interview_schedule_id"}),
      @UniqueConstraint(
          name = "uk_interview_booking_semester_applicant",
          columnNames = {"semester_id", "applicant_key"})
    })
public class InterviewBooking extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "booked_at", nullable = false)
  private LocalDateTime bookedAt;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "interview_schedule_id", nullable = false)
  private InterviewSchedule interviewSchedule;

  @Column(name = "user_id")
  private Long userId;

  // Snapshot - 사용자 삭제 이후에도 관리자 조회 가능하도록 유지
  @Column(name = "user_name_masked", nullable = false)
  private String userNameMasked;

  @Column(name = "user_student_number_masked", nullable = false)
  private String userStudentNumberMasked;

  @Column(name = "user_email_masked", nullable = false)
  private String userEmailMasked;

  @Column(name = "semester_id", nullable = false)
  private Long semesterId;

  @Enumerated(EnumType.STRING)
  @Column(name = "track", nullable = false)
  private Track track;

  @Column(name = "applicant_key", nullable = false, length = 64)
  private String applicantKey;

  @Column(name = "application_record_id", nullable = false)
  private Long applicationRecordId;

  public void changeSchedule(InterviewSchedule newSchedule) {
    this.interviewSchedule = newSchedule;
  }

  public void changeTrack(Track track) {
    this.track = track;
  }

  public void changeSemesterId(Long semesterId) {
    this.semesterId = semesterId;
  }
}
