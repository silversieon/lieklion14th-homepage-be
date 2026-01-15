/* 
 * Copyright (c) SKU LIKELION 
 */
package com.likelionskuniv.website.domain.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.likelionskuniv.website.domain.auth.dto.request.EmailVerificationConfirmReqeust;
import com.likelionskuniv.website.domain.auth.dto.request.EmailVerificationSendRequest;
import com.likelionskuniv.website.domain.auth.dto.request.EmailVerificationStatusRequest;
import com.likelionskuniv.website.domain.auth.exception.AuthErrorCode;
import com.likelionskuniv.website.domain.auth.service.AuthService;

import backend.boilerplate.exception.CustomException;
import backend.boilerplate.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthControllerImpl implements AuthController {

  private final AuthService authService;

  @Override
  public ResponseEntity<BaseResponse<Void>> requestVerification(
      @RequestBody EmailVerificationSendRequest request) {
    String email = request.getEmail();
    validateEmail(email);
    try {
      authService.sendVerificationEmail(email).get();

      return ResponseEntity.status(201).body(BaseResponse.success(201, "인증 코드 전송에 성공했습니다.", null));
    } catch (Exception e) {
      log.error("[Auth] 인증 코드 전송 최종 실패 - 에러: {}", e.getMessage());
      throw new CustomException(AuthErrorCode.INTERNAL_SERVER_ERROR_EMAIL);
    }
  }

  @Override
  public ResponseEntity<BaseResponse<Void>> confirmVerification(
      @RequestBody EmailVerificationConfirmReqeust request) {
    boolean isConfirm = authService.confirmVerificationCode(request);
    if (isConfirm) {
      return ResponseEntity.status(201).body(BaseResponse.success(201, "인증 코드 검증에 성공했습니다.", null));
    } else {
      return ResponseEntity.status(400).body(BaseResponse.error(400, "인증 코드 검증에 실패했습니다."));
    }
  }

  @Override
  public ResponseEntity<BaseResponse<Void>> checkVerification(
      @RequestBody EmailVerificationStatusRequest request) {
    boolean isVerified = authService.checkVerificationEmail(request);
    if (isVerified) {
      return ResponseEntity.status(200).body(BaseResponse.success(200, "검증된 이메일입니다.", null));
    } else {
      return ResponseEntity.status(200).body(BaseResponse.success(200, "검증되지 않은 이메일입니다.", null));
    }
  }

  private void validateEmail(String email) {
    if (!email.matches("^[A-Za-z0-9._%+-]+@skuniv\\.ac\\.kr$")) {
      log.error("[Auth] 유효하지 않은 이메일 값 입력 - 이메일: {}", email);
      throw new CustomException(AuthErrorCode.INVALID_INPUT_EMAIL);
    }
  }
}
