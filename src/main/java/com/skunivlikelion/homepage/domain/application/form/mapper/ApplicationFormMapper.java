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

  public ApplicationForm toEntity(Semester semesterRef, ApplicationFormUpsertRequest request) {
    return ApplicationForm.builder()
        .semester(semesterRef)
        .openAt(request.getOpenAt())
        .closeAt(request.getCloseAt())
        .applicationResultAt(request.getApplicationResultAt())
        .finalResultAt(request.getFinalResultAt())
        .build();
  }

  public ApplicationForm toUpdatedEntity(
      ApplicationForm found, ApplicationFormUpsertRequest request) {
    return ApplicationForm.builder()
        .id(found.getId())
        .semester(found.getSemester())
        .openAt(request.getOpenAt())
        .closeAt(request.getCloseAt())
        .applicationResultAt(request.getApplicationResultAt())
        .finalResultAt(request.getFinalResultAt())
        .build();
  }

  public ApplicationFormResponse toResponse(ApplicationForm entity) {
    return ApplicationFormResponse.builder()
        .id(entity.getId())
        .semester(entity.getSemester().getSemester())
        .openAt(entity.getOpenAt())
        .closeAt(entity.getCloseAt())
        .applicationResultAt(entity.getApplicationResultAt())
        .finalResultAt(entity.getFinalResultAt())
        .build();
  }

  public List<ApplicationFormResponse> toResponseList(List<ApplicationForm> entities) {
    return entities.stream().map(this::toResponse).toList();
  }

  public ApplicationFormSummaryResponse toSummaryResponse(ApplicationForm entity) {
    Long semester = entity.getSemester().getSemester();
    return ApplicationFormSummaryResponse.builder()
        .semester(semester)
        .title(semester + "기")
        .closeAt(entity.getCloseAt())
        .build();
  }

  public List<ApplicationFormSummaryResponse> toSummaryResponseList(
      List<ApplicationForm> entities) {
    return entities.stream().map(this::toSummaryResponse).toList();
  }
}
