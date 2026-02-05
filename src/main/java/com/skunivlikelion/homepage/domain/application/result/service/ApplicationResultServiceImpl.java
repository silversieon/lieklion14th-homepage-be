/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.application.result.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.skunivlikelion.homepage.domain.application.form.entity.ApplicationForm;
import com.skunivlikelion.homepage.domain.application.form.exception.ApplicationFormErrorCode;
import com.skunivlikelion.homepage.domain.application.form.repository.ApplicationFormRepository;
import com.skunivlikelion.homepage.domain.application.record.entity.ApplicationRecord;
import com.skunivlikelion.homepage.domain.application.record.repository.ApplicationRecordRepository;
import com.skunivlikelion.homepage.domain.application.result.dto.response.AdminApplicationResultConfirmResponse;
import com.skunivlikelion.homepage.domain.application.result.dto.response.AdminDocumentResultUpdateResponse;
import com.skunivlikelion.homepage.domain.application.result.dto.response.MyInterviewResultResponse;
import com.skunivlikelion.homepage.domain.application.result.exception.ApplicationResultErrorCode;
import com.skunivlikelion.homepage.domain.user.entity.ClubMember;
import com.skunivlikelion.homepage.domain.user.entity.User;
import com.skunivlikelion.homepage.domain.user.enums.Position;
import com.skunivlikelion.homepage.domain.user.exception.UserErrorCode;
import com.skunivlikelion.homepage.domain.user.repository.ClubMemberRepository;
import com.skunivlikelion.homepage.domain.user.repository.UserRepository;
import com.skunivlikelion.homepage.global.exception.CustomException;
import com.skunivlikelion.homepage.global.security.CurrentUserProvider;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ApplicationResultServiceImpl implements ApplicationResultService {

  private static final long INTERVIEW_RESULT_GRACE_DAYS = 7L;

  private final ApplicationRecordRepository applicationRecordRepository;
  private final CurrentUserProvider currentUserProvider;
  private final ClubMemberRepository clubMemberRepository;
  private final UserRepository userRepository;
  private final ApplicationFormRepository applicationFormRepository;

  @Override
  public AdminDocumentResultUpdateResponse updateDocumentResult(
      Long applicationRecordId, boolean isDocumentPassed) {

    ApplicationRecord record =
        applicationRecordRepository
            .findById(applicationRecordId)
            .orElseThrow(() -> new CustomException(ApplicationResultErrorCode.NOT_FOUND_RECORD));

    if (!record.isSubmitted()) {
      log.warn("[ApplicationResult] 제출되지 않은 지원서 처리 시도 - recordId={}", applicationRecordId);
      throw new CustomException(ApplicationResultErrorCode.ONLY_SUBMITTED_RECORD_ALLOWED);
    }

    LocalDateTime now = LocalDateTime.now();
    if (record.getApplicationForm().isAfterApplicationResultAt(now)) {
      log.warn(
          "[ApplicationResult] 서류 결과 발표 이후 수정 시도 - recordId={}, formId={}, now={}, applicationResultAt={}",
          record.getId(),
          record.getApplicationForm().getId(),
          now,
          record.getApplicationForm().getApplicationResultAt());
      throw new CustomException(ApplicationResultErrorCode.DOCUMENT_RESULT_ALREADY_ANNOUNCED);
    }

    if (isDocumentPassed) {
      record.markDocumentPassed();
    } else {
      record.unmarkDocumentPassed();
    }

    log.info(
        "[ApplicationResult] 서류 합격 여부 수정 완료 - recordId={}, isDocumentPassed={}",
        applicationRecordId,
        record.isDocumentPassed());

    return AdminDocumentResultUpdateResponse.builder()
        .applicationRecordId(record.getId())
        .isDocumentPassed(record.isDocumentPassed())
        .build();
  }

  @Override
  @Transactional
  public AdminApplicationResultConfirmResponse confirmDocumentResult(
      Long applicationRecordId, boolean passed) {

    ApplicationRecord applicationRecord =
        applicationRecordRepository
            .findById(applicationRecordId)
            .orElseThrow(() -> new CustomException(ApplicationResultErrorCode.NOT_FOUND_RECORD));

    if (!applicationRecord.isSubmitted()) {
      log.warn(
          "[ApplicationResult] 제출되어 있지 않은 지원서를 임의로 합격 처리 발생 - recordId={}", applicationRecordId);
      throw new CustomException(ApplicationResultErrorCode.ONLY_SUBMITTED_RECORD_ALLOWED);
    }

    if (!applicationRecord.isDocumentPassed()) {
      log.info(
          "[ApplicationResult] 서류 결과가 합격되지 않은 구성원 면접 합격 여부 처리 발생 - recordId={}",
          applicationRecordId);
      throw new CustomException(ApplicationResultErrorCode.ONLY_PASSED_DOCUMENT_ALLOWED);
    }
    LocalDateTime now = LocalDateTime.now();
    if (applicationRecord.getApplicationForm().getFinalResultAt().isBefore(now)) {
      log.warn("[ApplicationResult] 최종 결과 이후 면접 결과 수정 시도 - recordId={}", applicationRecordId);
      throw new CustomException(ApplicationResultErrorCode.FINAL_RESULT_ALREADY_ANNOUNCED);
    }

    if (!passed) {
      applicationRecord.unmarkInterviewPassed();
      log.info("[ApplicationResult] 면접 불합격 처리 - recordId={}", applicationRecordId);
      return AdminApplicationResultConfirmResponse.builder()
          .applicationRecordId(applicationRecord.getId())
          .passed(applicationRecord.isInterviewPassed())
          .build();
    } else {
      applicationRecord.markInterviewPassed();
      User user =
          userRepository
              .findById(applicationRecord.getUser().getId())
              .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));
      ApplicationForm applicationForm =
          applicationFormRepository
              .findById(applicationRecord.getApplicationForm().getId())
              .orElseThrow(
                  () -> new CustomException(ApplicationFormErrorCode.NOT_FOUND_APPLICATION_FORM));

      ClubMember newClubMember =
          ClubMember.builder()
              .position(Position.BABYLION)
              .track(applicationRecord.getTrack())
              .user(user)
              .semester(applicationForm.getSemester())
              .build();
      clubMemberRepository.save(newClubMember);

      log.info(
          "[ApplicationResult] 면접 합격 처리 - recordId={}, 새 구성원으로 등록된 사용자 userId={}, name={}",
          applicationRecordId,
          user.getId(),
          user.getName());
      return AdminApplicationResultConfirmResponse.builder()
          .applicationRecordId(applicationRecord.getId())
          .passed(applicationRecord.isInterviewPassed())
          .build();
    }
  }

  @Override
  @Transactional(readOnly = true)
  public MyInterviewResultResponse getCurrentUserInterviewResult() {

    User currentUser = currentUserProvider.getCurrentUser();

    LocalDateTime now = LocalDateTime.now();
    LocalDateTime threshold = now.minusDays(INTERVIEW_RESULT_GRACE_DAYS);

    ApplicationForm form =
        applicationFormRepository
            .findCurrentOrGraceApplicationForm(now, threshold)
            .orElseThrow(
                () -> {
                  log.info(
                      "[ApplicationResult] 면접 결과 조회 가능 모집 공고 없음 - now={}, threshold={}, graceDays={}",
                      now,
                      threshold,
                      INTERVIEW_RESULT_GRACE_DAYS);
                  return new CustomException(
                      ApplicationResultErrorCode.INTERVIEW_RESULT_VIEW_PERIOD_EXPIRED);
                });

    ApplicationRecord applicationRecord =
        applicationRecordRepository
            .findLatestByFormIdAndUserId(form.getId(), currentUser.getId())
            .orElseThrow(() -> new CustomException(ApplicationResultErrorCode.NOT_FOUND_RECORD));

    if (!applicationRecord.isSubmitted()) {
      throw new CustomException(ApplicationResultErrorCode.ONLY_SUBMITTED_RECORD_ALLOWED);
    }

    return MyInterviewResultResponse.builder()
        .documentPassed(applicationRecord.isDocumentPassed())
        .interviewPassed(applicationRecord.isInterviewPassed())
        .track(applicationRecord.getTrack())
        .semester(form.getSemester().getSemester())
        .build();
  }
}
