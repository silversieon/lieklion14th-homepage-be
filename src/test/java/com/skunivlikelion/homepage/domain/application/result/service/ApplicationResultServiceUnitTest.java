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
import com.skunivlikelion.homepage.domain.application.result.exception.ApplicationResultErrorCode;
import com.skunivlikelion.homepage.domain.common.enums.Track;
import com.skunivlikelion.homepage.domain.semester.entity.Semester;
import com.skunivlikelion.homepage.domain.user.entity.ClubMember;
import com.skunivlikelion.homepage.domain.user.entity.User;
import com.skunivlikelion.homepage.domain.user.enums.Position;
import com.skunivlikelion.homepage.domain.user.repository.ClubMemberRepository;
import com.skunivlikelion.homepage.global.exception.CustomException;

@ExtendWith(MockitoExtension.class)
class ApplicationResultServiceUnitTest {

  @Mock ApplicationRecordRepository applicationRecordRepository;
  @Mock ClubMemberRepository clubMemberRepository;

  @InjectMocks ApplicationResultServiceImpl applicationResultService;

  @Test
  @DisplayName("면접 불합격_구성원 존재 시_불합격 처리 및 구성원 삭제한다")
  void fail_applicationResult_clubmemberExist() {
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
  @DisplayName("면접 불합격_구성원 미 존재 시_불합격 처리만 한다")
  void fail_applicationResult_clubmemberNotExist() {
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
  @DisplayName("면접 합격_합격 처리 및 구성원 추가한다")
  void pass_applicationResult() {
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
  @DisplayName("면접 결과 수정_제출되지 않은 지원서_예외 발생")
  void change_applicationResult_notSubmitted_applicationRecord_exception() {
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
  @DisplayName("면접 결과 수정_서류 결과 불합격 지원서_예외 발생")
  void change_applicationResult_notDocumentPassed_applicationRecord_exception() {
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
  @DisplayName("면접 결과 수정_최종 결과 이후_예외 발생")
  void change_applicationResult_notActive_applicationForm_exception() {
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
