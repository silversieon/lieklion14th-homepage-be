/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.project.exception;

import org.springframework.http.HttpStatus;

import backend.boilerplate.exception.model.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProjectErrorCode implements BaseErrorCode {
  INVALID_PROJECT_REQUEST("PROJECT4001", "프로젝트 요청 값이 올바르지 않습니다.", HttpStatus.BAD_REQUEST),
  INVALID_PROJECT_TYPE_REQUEST("PTYPE4001", "프로젝트 타입 요청 값이 올바르지 않습니다.", HttpStatus.BAD_REQUEST),
  ALREADY_EXIST_PROJECT("PROJECT4002", "이미 존재하는 프로젝트입니다.", HttpStatus.BAD_REQUEST),
  ALREADY_EXIST_PROJECT_TYPE("PTYPE4002", "이미 존재하는 프로젝트 타입 입니다.", HttpStatus.BAD_REQUEST),
  INVALID_LIMIT_EXCEEDED("PROJECT4004", "프로젝트 이미지 개수가 허용 범위를 초과했습니다.", HttpStatus.BAD_REQUEST),
  NOT_FOUND_PROJECT("PROJECT4041", "해당 프로젝트를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
  NOT_FOUND_PROJECT_IMAGE("PROJECT4042", "해당 프로젝트 이미지를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
  NOT_FOUND_PROJECT_TYPE("PTYPE4041", "해당 프로젝트 타입을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
  CANNOT_DELETE_PROJECT_WITH_IMAGES(
      "PROJECT4091", "이미지가 남아 있는 프로젝트는 삭제할 수 없습니다.", HttpStatus.CONFLICT),
  CANNOT_DELETE_PROJECT_TYPE_WITH_PROJECTS(
      "PTYPE4091", "프로젝트가 남아 있는 프로젝트 타입은 삭제할 수 없습니다.", HttpStatus.CONFLICT),
  PROJECT_IMAGE_UPLOAD_FAIL(
      "PROJECT5001", "프로젝트 이미지 업로드에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
  PROJECT_TYPE_DELETE_FAIL("PTYPE5002", "프로젝트 타입 삭제에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
  PROJECT_IMAGE_DELETE_FAIL(
      "PROJECT5002", "프로젝트 이미지 삭제에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);

  private final String code;
  private final String message;
  private final HttpStatus status;
}
