/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.application.result.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.skunivlikelion.homepage.domain.application.form.entity.ApplicationForm;
import com.skunivlikelion.homepage.domain.application.record.entity.ApplicationRecord;
import com.skunivlikelion.homepage.domain.application.record.repository.ApplicationRecordRepository;
import com.skunivlikelion.homepage.domain.application.result.dto.response.AdminDocumentResultUpdateResponse;
import com.skunivlikelion.homepage.domain.application.result.exception.ApplicationResultErrorCode;
import com.skunivlikelion.homepage.domain.common.enums.Track;
import com.skunivlikelion.homepage.domain.semester.entity.Semester;
import com.skunivlikelion.homepage.domain.user.entity.ClubMember;
import com.skunivlikelion.homepage.domain.user.entity.User;
import com.skunivlikelion.homepage.domain.user.enums.Position;
import com.skunivlikelion.homepage.domain.user.repository.ClubMemberRepository;
import com.skunivlikelion.homepage.global.exception.CustomException;

@ExtendWith(MockitoExtension.class)
class ApplicationResultCommandServiceUnitTest {

  @Mock ApplicationRecordRepository applicationRecordRepository;
  @Mock ClubMemberRepository clubMemberRepository;
  @Mock ApplicationRecord applicationRecord;
  @Mock ApplicationForm applicationForm;

  @InjectMocks ApplicationResultServiceImpl applicationResultService;

  @Test
  @DisplayName("서류 결과 수정 - 합격 처리 (성공)")
  void updateDocumentResult_pass_success() {
    // given
    Long applicationRecordId = 1L;
    when(applicationRecord.getId()).thenReturn(applicationRecordId);
    when(applicationRecordRepository.findById(applicationRecordId))
        .thenReturn(Optional.of(applicationRecord));
    when(applicationRecord.isSubmitted()).thenReturn(true);
    when(applicationRecord.getApplicationForm()).thenReturn(applicationForm);
    when(applicationForm.isAfterApplicationResultAt(any(LocalDateTime.class))).thenReturn(false);
    when(applicationRecord.getIsDocumentPassed()).thenReturn(true);

    // when
    AdminDocumentResultUpdateResponse result =
        applicationResultService.updateDocumentResult(applicationRecordId, true);

    // then
    verify(applicationRecord).passDocument();
    verify(applicationRecord, never()).failDocument();
    verify(applicationRecord, never()).resetInterviewResult();

    assertThat(result.getApplicationRecordId()).isEqualTo(applicationRecordId);
    assertThat(result.getIsDocumentPassed()).isTrue();
  }

  @Test
  @DisplayName("서류 결과 수정 - 불합격 처리 (성공)")
  void updateDocumentResult_nonePass_success() {
    // given
    Long applicationRecordId = 1L;
    when(applicationRecord.getId()).thenReturn(applicationRecordId);
    when(applicationRecordRepository.findById(applicationRecordId))
        .thenReturn(Optional.of(applicationRecord));
    when(applicationRecord.isSubmitted()).thenReturn(true);
    when(applicationRecord.getApplicationForm()).thenReturn(applicationForm);
    when(applicationForm.isAfterApplicationResultAt(any(LocalDateTime.class))).thenReturn(false);
    when(applicationRecord.getIsDocumentPassed()).thenReturn(false);

    // when
    AdminDocumentResultUpdateResponse result =
        applicationResultService.updateDocumentResult(applicationRecordId, false);

    // then
    verify(applicationRecord).failDocument();
    verify(applicationRecord).resetInterviewResult();
    verify(applicationRecord, never()).passDocument();

    assertThat(result.getApplicationRecordId()).isEqualTo(applicationRecordId);
    assertThat(result.getIsDocumentPassed()).isFalse();
  }

  @Test
  @DisplayName("서류 결과 수정 - 제출하지 않은 지원서 (실패)")
  void updateDocumentResult_notSubmitted_fail() {
    // given
    Long applicationRecordId = 1L;
    when(applicationRecordRepository.findById(applicationRecordId))
        .thenReturn(Optional.of(applicationRecord));
    when(applicationRecord.isSubmitted()).thenReturn(false);

    // when
    assertThatThrownBy(
            () -> applicationResultService.updateDocumentResult(applicationRecordId, true))
        .isInstanceOf(CustomException.class)
        .satisfies(
            ex -> {
              CustomException ce = (CustomException) ex;
              assertThat(ce.getErrorCode())
                  .isEqualTo(ApplicationResultErrorCode.ONLY_SUBMITTED_RECORD_ALLOWED);
            });

    // then
    verifyNoMoreInteractions(applicationRecord);
  }

  @Test
  @DisplayName("서류 결과 수정 - 서류 결과 발표 이후 수정 (실패)")
  void updateDocumentResult_notSubmitPeriod_fail() {
    // given
    Long applicationRecordId = 1L;
    Long applicationFormId = 1L;
    when(applicationRecord.getId()).thenReturn(applicationRecordId);
    when(applicationRecordRepository.findById(applicationRecordId))
        .thenReturn(Optional.of(applicationRecord));
    when(applicationRecord.isSubmitted()).thenReturn(true);
    when(applicationRecord.getApplicationForm()).thenReturn(applicationForm);
    when(applicationForm.getId()).thenReturn(applicationFormId);
    when(applicationForm.isAfterApplicationResultAt(any(LocalDateTime.class))).thenReturn(true);

    // when
    assertThatThrownBy(
            () -> applicationResultService.updateDocumentResult(applicationRecordId, true))
        .isInstanceOf(CustomException.class)
        .satisfies(
            ex -> {
              CustomException ce = (CustomException) ex;
              assertThat(ce.getErrorCode())
                  .isEqualTo(ApplicationResultErrorCode.DOCUMENT_RESULT_ALREADY_ANNOUNCED);
            });

    // then
  }

  @Test
  @DisplayName("면접 결과 수정 - 구성원 존재 시 불합격 처리 및 구성원 삭제 (성공)")
  void confirmDocumentResult_clubMemberExist_nonePass_success() {
    // given
    Long recordId = 1L;
    Long userId = 10L;
    Semester semester = Semester.builder().semester(14L).build();
    Track track = Track.BACKEND;

    ApplicationForm applicationForm = mock(ApplicationForm.class);
    when(applicationForm.getSemester()).thenReturn(semester); // 14기로 가정
    when(applicationForm.getFinalResultAt())
        .thenReturn(LocalDateTime.now().plusDays(1L)); // 아직 끝나지 않은 모집 공고로 가정

    User user = mock(User.class);
    when(user.getId()).thenReturn(userId);

    ApplicationRecord applicationRecord = mock(ApplicationRecord.class);
    when(applicationRecord.getApplicationForm()).thenReturn(applicationForm);
    when(applicationRecord.isSubmitted()).thenReturn(true);
    when(applicationRecord.getIsDocumentPassed()).thenReturn(true);
    when(applicationRecord.getUser()).thenReturn(user);
    when(applicationRecord.getTrack()).thenReturn(track);
    when(applicationRecordRepository.findById(recordId)).thenReturn(Optional.of(applicationRecord));
    when(clubMemberRepository.existsByUser_IdAndSemesterAndTrack(userId, semester, track))
        .thenReturn(true);

    // when
    applicationResultService.confirmDocumentResult(recordId, false);

    // then
    verify(applicationRecord).failInterview();
    verify(clubMemberRepository).deleteByUser_IdAndSemesterAndTrack(userId, semester, track);
  }

  @Test
  @DisplayName("면접 결과 수정 - 구성원 미 존재 시 불합격만 처리 (성공)")
  void confirmDocumentResult_clubMemberNotExist_nonePass_success() {
    // given
    Long recordId = 1L;
    Long userId = 10L;
    Semester semester = Semester.builder().semester(14L).build();
    Track track = Track.BACKEND;

    ApplicationForm applicationForm = mock(ApplicationForm.class);
    when(applicationForm.getSemester()).thenReturn(semester); // 14기로 가정
    when(applicationForm.getFinalResultAt())
        .thenReturn(LocalDateTime.now().plusDays(1L)); // 아직 끝나지 않은 모집 공고로 가정

    User user = mock(User.class);
    when(user.getId()).thenReturn(userId);

    ApplicationRecord applicationRecord = mock(ApplicationRecord.class);
    when(applicationRecord.getApplicationForm()).thenReturn(applicationForm);
    when(applicationRecord.isSubmitted()).thenReturn(true);
    when(applicationRecord.getIsDocumentPassed()).thenReturn(true);
    when(applicationRecord.getUser()).thenReturn(user);
    when(applicationRecord.getTrack()).thenReturn(track);
    when(applicationRecordRepository.findById(recordId)).thenReturn(Optional.of(applicationRecord));
    when(clubMemberRepository.existsByUser_IdAndSemesterAndTrack(userId, semester, track))
        .thenReturn(false);

    // when
    applicationResultService.confirmDocumentResult(recordId, false);

    // then
    verify(applicationRecord).failInterview();
    verify(clubMemberRepository, never())
        .deleteByUser_IdAndSemesterAndTrack(userId, semester, track);
  }

  @Test
  @DisplayName("면접 결과 수정 - 합격 처리 및 구성원 추가 (성공)")
  void confirmDocumentResult_pass_success() {
    // given
    Long recordId = 1L;
    Long userId = 10L;
    Semester semester = Semester.builder().semester(14L).build();
    Track track = Track.BACKEND;

    ApplicationForm applicationForm = mock(ApplicationForm.class);
    when(applicationForm.getSemester()).thenReturn(semester); // 14기로 가정
    when(applicationForm.getFinalResultAt())
        .thenReturn(LocalDateTime.now().plusDays(1L)); // 아직 끝나지 않은 모집 공고로 가정

    User user = mock(User.class);
    when(user.getId()).thenReturn(userId);

    ApplicationRecord applicationRecord = mock(ApplicationRecord.class);
    when(applicationRecord.getApplicationForm()).thenReturn(applicationForm);
    when(applicationRecord.isSubmitted()).thenReturn(true);
    when(applicationRecord.getIsDocumentPassed()).thenReturn(true);
    when(applicationRecord.getUser()).thenReturn(user);
    when(applicationRecord.getTrack()).thenReturn(track);
    when(applicationRecordRepository.findById(recordId)).thenReturn(Optional.of(applicationRecord));

    // when
    applicationResultService.confirmDocumentResult(recordId, true);

    // then
    verify(applicationRecord).passInterview();
    verify(applicationRecord, never()).failInterview();

    ArgumentCaptor<ClubMember> captor = ArgumentCaptor.forClass(ClubMember.class);
    verify(clubMemberRepository).save(captor.capture());

    ClubMember savedClubMember = captor.getValue();

    assertThat(savedClubMember.getPosition()).isEqualTo(Position.BABYLION);
    assertThat(savedClubMember.getUser()).isEqualTo(user);
    assertThat(savedClubMember.getTrack()).isEqualTo(track);
    assertThat(savedClubMember.getSemester()).isEqualTo(semester);
  }

  @Test
  @DisplayName("면접 결과 수정 - 제출되지 않은 지원서 예외 발생 (실패)")
  void confirmDocumentResult_notSubmittedApplicationRecord_fail() {
    // given
    Long applicationRecordId = 1L;

    ApplicationForm applicationForm = mock(ApplicationForm.class);
    ApplicationRecord applicationRecord = mock(ApplicationRecord.class);

    when(applicationRecordRepository.findById(applicationRecordId))
        .thenReturn(Optional.of(applicationRecord));
    when(applicationRecord.getApplicationForm()).thenReturn(applicationForm);
    when(applicationRecord.isSubmitted()).thenReturn(false);

    // when
    assertThatThrownBy(
            () -> applicationResultService.confirmDocumentResult(applicationRecordId, true))
        .isInstanceOf(CustomException.class)
        .satisfies(
            ex -> {
              CustomException ce = (CustomException) ex;
              assertThat(ce.getErrorCode())
                  .isEqualTo(ApplicationResultErrorCode.ONLY_SUBMITTED_RECORD_ALLOWED);
            });

    // then
    verify(applicationRecord, never()).getIsDocumentPassed();
    verify(applicationRecord, never()).passInterview();
  }

  @Test
  @DisplayName("면접 결과 수정 - 서류 결과 불합격 지원서 (실패)")
  void confirmDocumentResult_notPassedDocument_fail() {
    // given
    Long applicationRecordId = 1L;

    ApplicationForm applicationForm = mock(ApplicationForm.class);
    ApplicationRecord applicationRecord = mock(ApplicationRecord.class);

    when(applicationRecordRepository.findById(applicationRecordId))
        .thenReturn(Optional.of(applicationRecord));
    when(applicationRecord.getApplicationForm()).thenReturn(applicationForm);
    when(applicationRecord.isSubmitted()).thenReturn(true);
    when(applicationRecord.getIsDocumentPassed()).thenReturn(false);

    // when
    assertThatThrownBy(
            () -> applicationResultService.confirmDocumentResult(applicationRecordId, true))
        .isInstanceOf(CustomException.class)
        .satisfies(
            ex -> {
              CustomException ce = (CustomException) ex;
              assertThat(ce.getErrorCode())
                  .isEqualTo(ApplicationResultErrorCode.ONLY_PASSED_DOCUMENT_ALLOWED);
            });

    // then
    verify(applicationForm, never()).getFinalResultAt();
    verify(applicationRecord, never()).passInterview();
  }

  @Test
  @DisplayName("면접 결과 수정 - 최종 결과 이후 변경 시 (실패)")
  void confirmDocumentResult_notActiveApplicationForm_fail() {
    // given
    Long applicationRecordId = 1L;

    ApplicationForm applicationForm = mock(ApplicationForm.class);
    ApplicationRecord applicationRecord = mock(ApplicationRecord.class);

    when(applicationRecordRepository.findById(applicationRecordId))
        .thenReturn(Optional.of(applicationRecord));
    when(applicationRecord.getApplicationForm()).thenReturn(applicationForm);
    when(applicationRecord.isSubmitted()).thenReturn(true);
    when(applicationRecord.getIsDocumentPassed()).thenReturn(true);

    when(applicationForm.getFinalResultAt()).thenReturn(LocalDateTime.now().minusDays(1));

    // when ad
    assertThatThrownBy(
            () -> applicationResultService.confirmDocumentResult(applicationRecordId, true))
        .isInstanceOf(CustomException.class)
        .satisfies(
            ex -> {
              CustomException ce = (CustomException) ex;
              assertThat(ce.getErrorCode())
                  .isEqualTo(ApplicationResultErrorCode.FINAL_RESULT_ALREADY_ANNOUNCED);
            });

    // then
    verify(applicationRecord, never()).passInterview();
  }
}
