/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.semester.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.skunivlikelion.homepage.domain.semester.dto.request.SemesterRequest;
import com.skunivlikelion.homepage.domain.semester.dto.response.SemesterResponse;
import com.skunivlikelion.homepage.domain.semester.entity.Semester;

@Component
public class SemesterMapper {

  public Semester toEntity(SemesterRequest request) {
    return Semester.builder().semester(request.getSemester()).build();
  }

  public SemesterResponse toResponse(Semester semester) {
    return SemesterResponse.builder().semester(semester.getSemester()).build();
  }

  public List<SemesterResponse> toResponseList(List<Semester> semesters) {
    return semesters.stream().map(this::toResponse).toList();
  }
}
