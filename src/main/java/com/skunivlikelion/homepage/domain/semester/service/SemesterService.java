/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.semester.service;

import java.util.List;

import com.skunivlikelion.homepage.domain.semester.dto.request.SemesterRequest;
import com.skunivlikelion.homepage.domain.semester.dto.response.SemesterResponse;

public interface SemesterService {

  /**
   * [ 기수 생성 메서드 ] 새로운 기수를 생성·등록
   *
   * @param request 생성할 기수 정보를 담은 요청 DTO
   * @return 생성된 기수 정보를 담은 응답 DTO
   */
  SemesterResponse createSemester(SemesterRequest request);

  /**
   * [ 기수 삭제 메서드 ] 전달받은 기수 값에 해당하는 기수 삭제
   *
   * @param semester 삭제할 기수 값
   */
  void deleteSemester(Long semester);

  /**
   * [ 전체 기수 조회 메서드 ] 모든 기수를 조회하여 내림차순 목록으로 반환
   *
   * @return 등록된 모든 기수 정보를 담은 응답 DTO 리스트
   */
  List<SemesterResponse> getAllSemesters();
}
