/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.interview.schedule.validator;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import com.skunivlikelion.homepage.domain.application.form.entity.ApplicationForm;
import com.skunivlikelion.homepage.domain.application.form.repository.ApplicationFormRepository;
import com.skunivlikelion.homepage.domain.common.enums.Track;
import com.skunivlikelion.homepage.domain.interview.schedule.dto.request.InterviewScheduleCreateRequest;
import com.skunivlikelion.homepage.domain.interview.schedule.exception.InterviewScheduleErrorCode;
import com.skunivlikelion.homepage.global.exception.CustomException;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class InterviewScheduleValidator {

  private final ApplicationFormRepository applicationFormRepository;

  public void validateCreateRequest(
      Long semester, Track track, InterviewScheduleCreateRequest request) {
    if (track == null) {
      throw new CustomException(InterviewScheduleErrorCode.INVALID_TRACK);
    }
    if (request == null
        || request.getDate() == null
        || request.getStartTime() == null
        || request.getEndTime() == null) {
      throw new CustomException(InterviewScheduleErrorCode.INVALID_TIME_RANGE);
    }
    if (!request.getStartTime().isBefore(request.getEndTime())) {
      throw new CustomException(InterviewScheduleErrorCode.INVALID_TIME_RANGE);
    }
  }

  public void validateCreateWindow(Long semester, LocalDateTime slotStartAt) {
    ApplicationForm form =
        applicationFormRepository
            .findBySemester(semester)
            .orElseThrow(
                () -> new CustomException(InterviewScheduleErrorCode.APPLICATION_FORM_NOT_FOUND));

    LocalDateTime confirmedAt = form.getInterviewScheduleConfirmedAt();
    LocalDateTime finalResultAt = form.getFinalResultAt();

    if (confirmedAt == null || finalResultAt == null) {
      throw new CustomException(InterviewScheduleErrorCode.APPLICATION_FORM_NOT_FOUND);
    }

    if (slotStartAt.isBefore(confirmedAt) || slotStartAt.isAfter(finalResultAt)) {
      throw new CustomException(InterviewScheduleErrorCode.INTERVIEW_DATE_OUT_OF_RANGE);
    }

    LocalDateTime now = LocalDateTime.now();
    if (!now.isBefore(confirmedAt)) {
      throw new CustomException(InterviewScheduleErrorCode.INTERVIEW_SCHEDULE_CREATE_AFTER_CLOSE);
    }
  }
}
