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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.skunivlikelion.homepage.domain.application.form.entity.ApplicationForm;
import com.skunivlikelion.homepage.domain.application.form.repository.ApplicationFormRepository;
import com.skunivlikelion.homepage.domain.application.record.entity.ApplicationRecord;
import com.skunivlikelion.homepage.domain.application.record.repository.ApplicationRecordRepository;
import com.skunivlikelion.homepage.domain.application.result.dto.response.MyInterviewResultResponse;
import com.skunivlikelion.homepage.domain.application.result.exception.ApplicationResultErrorCode;
import com.skunivlikelion.homepage.domain.common.enums.Track;
import com.skunivlikelion.homepage.domain.semester.entity.Semester;
import com.skunivlikelion.homepage.domain.user.entity.User;
import com.skunivlikelion.homepage.global.exception.CustomException;
import com.skunivlikelion.homepage.global.security.CurrentUserProvider;

@ExtendWith(MockitoExtension.class)
public class ApplicationResultQueryServiceUnitTest {

  @Mock CurrentUserProvider currentUserProvider;
  @Mock ApplicationFormRepository applicationFormRepository;
  @Mock ApplicationRecordRepository applicationRecordRepository;
  @Mock ApplicationForm applicationForm;
  @Mock ApplicationRecord applicationRecord;
  @Mock User user;

  @InjectMocks ApplicationResultServiceImpl applicationResultService;

  @Test
  @DisplayName("현재 사용자 면접 결과 조회 - (성공)")
  void getCurrentUserInterviewResult_success() {
    // given
    Long applicationFormId = 1L;
    Long userId = 1L;
    Long semesterId = 14L;
    when(applicationFormRepository.findCurrentOrGraceApplicationForm(
            any(LocalDateTime.class), any(LocalDateTime.class)))
        .thenReturn(Optional.of(applicationForm));
    when(currentUserProvider.getCurrentUser()).thenReturn(user);
    when(applicationForm.getId()).thenReturn(applicationFormId);
    when(user.getId()).thenReturn(userId);
    when(applicationRecordRepository.findByApplicationFormIdAndUserId(applicationFormId, userId))
        .thenReturn(Optional.of(applicationRecord));
    when(applicationRecord.isSubmitted()).thenReturn(true);

    when(applicationRecord.getIsDocumentPassed()).thenReturn(true);
    when(applicationRecord.getIsInterviewPassed()).thenReturn(true);
    when(applicationRecord.getTrack()).thenReturn(Track.BACKEND);
    when(applicationForm.getSemester()).thenReturn(Semester.builder().semester(semesterId).build());

    // when
    MyInterviewResultResponse result = applicationResultService.getCurrentUserInterviewResult();

    // then
    assertThat(result.getDocumentPassed()).isTrue();
    assertThat(result.getInterviewPassed()).isTrue();
    assertThat(result.getTrack()).isEqualTo(Track.BACKEND);
    assertThat(result.getSemester()).isEqualTo(semesterId);
  }

  @Test
  @DisplayName("현재 사용자 면접 결과 조회 - 모집 중인 모집 공고 없음 (실패)")
  void getCurrentUserInterviewResult_notActiveApplicationForm_fail() {
    // given
    when(applicationFormRepository.findCurrentOrGraceApplicationForm(
            any(LocalDateTime.class), any(LocalDateTime.class)))
        .thenReturn(Optional.empty());

    // when
    assertThatThrownBy(() -> applicationResultService.getCurrentUserInterviewResult())
        .isInstanceOf(CustomException.class)
        .satisfies(
            ex -> {
              CustomException ce = (CustomException) ex;
              assertThat(ce.getErrorCode())
                  .isEqualTo(ApplicationResultErrorCode.INTERVIEW_RESULT_VIEW_PERIOD_EXPIRED);
            });

    // then
    verifyNoInteractions(currentUserProvider);
    verifyNoInteractions(applicationRecordRepository);
  }

  @Test
  @DisplayName("현재 사용자 면접 결과 조회 - 존재하지 않은 지원서 (실패)")
  void getCurrentUserInterviewResult_notExist_fail() {
    // given
    Long userId = 1L;
    Long applicationFormId = 1L;
    when(applicationFormRepository.findCurrentOrGraceApplicationForm(
            any(LocalDateTime.class), any(LocalDateTime.class)))
        .thenReturn(Optional.of(applicationForm));
    when(currentUserProvider.getCurrentUser()).thenReturn(user);
    when(applicationForm.getId()).thenReturn(applicationFormId);
    when(user.getId()).thenReturn(userId);
    when(applicationRecordRepository.findByApplicationFormIdAndUserId(applicationFormId, userId))
        .thenReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> applicationResultService.getCurrentUserInterviewResult())
        .isInstanceOf(CustomException.class)
        .satisfies(
            ex -> {
              CustomException ce = (CustomException) ex;
              assertThat(ce.getErrorCode()).isEqualTo(ApplicationResultErrorCode.NOT_FOUND_RECORD);
            });
  }

  @Test
  @DisplayName("현재 사용자 면접 결과 조회 - 제출하지 않은 지원서 (실패)")
  void getCurrentUserInterviewResult_notSubmitted_fail() {
    // given
    Long userId = 1L;
    Long applicationFormId = 1L;
    when(applicationFormRepository.findCurrentOrGraceApplicationForm(
            any(LocalDateTime.class), any(LocalDateTime.class)))
        .thenReturn(Optional.of(applicationForm));
    when(currentUserProvider.getCurrentUser()).thenReturn(user);
    when(applicationForm.getId()).thenReturn(applicationFormId);
    when(user.getId()).thenReturn(userId);
    when(applicationRecordRepository.findByApplicationFormIdAndUserId(applicationFormId, userId))
        .thenReturn(Optional.of(applicationRecord));
    when(applicationRecord.isSubmitted()).thenReturn(false);

    // when & then
    assertThatThrownBy(() -> applicationResultService.getCurrentUserInterviewResult())
        .isInstanceOf(CustomException.class)
        .satisfies(
            ex -> {
              CustomException ce = (CustomException) ex;
              assertThat(ce.getErrorCode())
                  .isEqualTo(ApplicationResultErrorCode.ONLY_SUBMITTED_RECORD_ALLOWED);
            });
  }
}
