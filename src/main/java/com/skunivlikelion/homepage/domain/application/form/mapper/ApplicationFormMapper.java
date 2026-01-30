/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.application.form.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.skunivlikelion.homepage.domain.application.form.dto.request.ApplicationFormUpsertRequest;
import com.skunivlikelion.homepage.domain.application.form.dto.response.ApplicationFormResponse;
import com.skunivlikelion.homepage.domain.application.form.dto.response.ApplicationFormSummaryResponse;
import com.skunivlikelion.homepage.domain.application.form.entity.ApplicationForm;
import com.skunivlikelion.homepage.domain.semester.entity.Semester;

@Component
public class ApplicationFormMapper {

  public ApplicationForm toEntity(Semester semester, ApplicationFormUpsertRequest request) {
    return ApplicationForm.builder()
        .semester(semester)
        .openAt(request.getOpenAt())
        .closeAt(request.getCloseAt())
        .applicationResultAt(request.getApplicationResultAt())
        .interviewScheduleConfirmedAt(request.getInterviewScheduleConfirmedAt())
        .finalResultAt(request.getFinalResultAt())
        .build();
  }

  public ApplicationFormResponse toResponse(ApplicationForm applicationForm) {
    return ApplicationFormResponse.builder()
        .id(applicationForm.getId())
        .semester(applicationForm.getSemester().getSemester())
        .openAt(applicationForm.getOpenAt())
        .closeAt(applicationForm.getCloseAt())
        .applicationResultAt(applicationForm.getApplicationResultAt())
        .interviewScheduleConfirmedAt(applicationForm.getInterviewScheduleConfirmedAt())
        .finalResultAt(applicationForm.getFinalResultAt())
        .build();
  }

  public List<ApplicationFormResponse> toResponseList(List<ApplicationForm> applicationForms) {
    return applicationForms.stream().map(this::toResponse).toList();
  }

  public ApplicationFormSummaryResponse toSummaryResponse(ApplicationForm applicationForm) {
    Long semester = applicationForm.getSemester().getSemester();
    return ApplicationFormSummaryResponse.builder()
        .semester(semester)
        .title(semester + "기 아기사자 모집 지원서")
        .closeAt(applicationForm.getCloseAt())
        .build();
  }

  public List<ApplicationFormSummaryResponse> toSummaryResponseList(
      List<ApplicationForm> applicationForms) {
    return applicationForms.stream().map(this::toSummaryResponse).toList();
  }
}
