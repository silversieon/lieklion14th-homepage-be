/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.interview.booking.validator;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import com.skunivlikelion.homepage.domain.application.form.entity.ApplicationForm;
import com.skunivlikelion.homepage.domain.application.form.repository.ApplicationFormRepository;
import com.skunivlikelion.homepage.domain.interview.booking.exception.InterviewBookingErrorCode;
import com.skunivlikelion.homepage.domain.interview.schedule.entity.InterviewSchedule;
import com.skunivlikelion.homepage.global.exception.CustomException;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class InterviewBookingValidator {

  private final ApplicationFormRepository applicationFormRepository;

  public void validateBookingWindow(Long semester) {
    ApplicationForm form =
        applicationFormRepository
            .findBySemester(semester)
            .orElseThrow(
                () -> new CustomException(InterviewBookingErrorCode.APPLICATION_FORM_NOT_FOUND));

    LocalDateTime applicationResultAt = form.getApplicationResultAt();
    LocalDateTime interviewScheduleConfirmedAt = form.getInterviewScheduleConfirmedAt();

    if (applicationResultAt == null || interviewScheduleConfirmedAt == null) {
      throw new CustomException(InterviewBookingErrorCode.APPLICATION_FORM_NOT_FOUND);
    }

    LocalDateTime now = LocalDateTime.now();

    if (now.isBefore(applicationResultAt) || !now.isBefore(interviewScheduleConfirmedAt)) {
      throw new CustomException(InterviewBookingErrorCode.BOOKING_WINDOW_CLOSED);
    }
  }

  public void validateSlotNotStarted(InterviewSchedule schedule) {
    LocalDateTime slotStartAt = LocalDateTime.of(schedule.getDate(), schedule.getStartTime());
    if (!LocalDateTime.now().isBefore(slotStartAt)) {
      throw new CustomException(InterviewBookingErrorCode.PAST_SCHEDULE_BOOKING);
    }
  }
}
