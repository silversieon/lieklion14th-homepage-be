/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.semester.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.skunivlikelion.homepage.domain.application.form.repository.ApplicationFormRepository;
import com.skunivlikelion.homepage.domain.semester.dto.request.SemesterRequest;
import com.skunivlikelion.homepage.domain.semester.dto.response.SemesterResponse;
import com.skunivlikelion.homepage.domain.semester.entity.Semester;
import com.skunivlikelion.homepage.domain.semester.exception.SemesterErrorCode;
import com.skunivlikelion.homepage.domain.semester.mapper.SemesterMapper;
import com.skunivlikelion.homepage.domain.semester.repository.SemesterRepository;

import backend.boilerplate.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class SemesterServiceImpl implements SemesterService {

  private final SemesterRepository semesterRepository;
  private final ApplicationFormRepository applicationFormRepository;
  private final SemesterMapper semesterMapper;

  @Override
  public SemesterResponse createSemester(SemesterRequest request) {
    Long semesterValue = request.getSemester();

    if (semesterRepository.existsBySemester(semesterValue)) {
      log.info("[Semester] 기수 생성 실패 - 이미 존재하는 기수 - semester={}", semesterValue);
      throw new CustomException(SemesterErrorCode.ALREADY_EXIST_SEMESTER);
    }

    Semester semester = semesterMapper.toEntity(request);
    Semester saved = semesterRepository.save(semester);

    log.info("[Semester] 기수 생성 완료 - semester={}", saved.getSemester());
    return semesterMapper.toResponse(saved);
  }

  @Override
  public void deleteSemester(Long semesterId) {
    Semester found =
        semesterRepository
            .findById(semesterId)
            .orElseThrow(
                () -> {
                  log.warn("[Semester] 기수 삭제 실패 - 존재하지 않는 기수 - semester={}", semesterId);
                  return new CustomException(SemesterErrorCode.NOT_FOUND_SEMESTER);
                });

    if (applicationFormRepository.existsBySemester_Semester(semesterId)) {
      log.info("[Semester] 기수 삭제 실패 - 참조된 모집 공고 존재 - semester={}", semesterId);
      throw new CustomException(SemesterErrorCode.SEMESTER_IN_USE);
    }

    semesterRepository.delete(found);
    log.info("[Semester] 기수 삭제 완료 - semester={}", semesterId);
  }

  @Override
  @Transactional(readOnly = true)
  public List<SemesterResponse> getAllSemesters() {
    List<Semester> semesters = semesterRepository.findAllByOrderBySemesterDesc();
    List<SemesterResponse> result = semesterMapper.toResponseList(semesters);

    log.info("[Semester] 전체 기수 조회 완료 - count={}", result.size());
    return result;
  }
}
