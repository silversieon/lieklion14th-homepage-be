/* 
 * Copyright (c) SKU LIKELION 
 */
package com.likelionskuniv.website.domain.auth.controller;

import jakarta.validation.Valid;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.likelionskuniv.website.domain.auth.dto.request.EmailVerificationConfirmReqeust;
import com.likelionskuniv.website.domain.auth.dto.request.EmailVerificationSendRequest;
import com.likelionskuniv.website.domain.auth.dto.request.EmailVerificationStatusRequest;
import com.likelionskuniv.website.domain.auth.dto.request.LoginRequest;
import com.likelionskuniv.website.domain.auth.dto.request.SignUpRequest;
import com.likelionskuniv.website.domain.auth.dto.response.LoginResponse;
import com.likelionskuniv.website.domain.auth.dto.response.TokenResponse;
import com.likelionskuniv.website.domain.auth.exception.AuthErrorCode;
import com.likelionskuniv.website.domain.auth.mapper.AuthMapper;
import com.likelionskuniv.website.domain.auth.service.AuthService;
import com.likelionskuniv.website.global.jwt.JwtCookieWriter;

import backend.boilerplate.exception.CustomException;
import backend.boilerplate.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthControllerImpl implements AuthController {

  private final AuthService authService;
  private final JwtCookieWriter jwtCookieWriter;
  private final AuthMapper authMapper;

  @Override
  public ResponseEntity<BaseResponse<Void>> requestVerification(
      @Valid @RequestBody EmailVerificationSendRequest request) {
    try {
      authService.sendVerificationEmail(request.getEmail()).get();

      return ResponseEntity.status(201).body(BaseResponse.success(201, "인증 코드 전송에 성공했습니다.", null));
    } catch (Exception e) {
      log.error("[Auth] 인증 코드 전송 최종 실패 - 에러: {}", e.getMessage());
      throw new CustomException(AuthErrorCode.INTERNAL_SERVER_ERROR_EMAIL);
    }
  }

  @Override
  public ResponseEntity<BaseResponse<Void>> confirmVerification(
      @Valid @RequestBody EmailVerificationConfirmReqeust request) {
    boolean isConfirm = authService.confirmVerificationCode(request);
    if (isConfirm) {
      return ResponseEntity.status(201).body(BaseResponse.success(201, "인증 코드 검증에 성공했습니다.", null));
    } else {
      return ResponseEntity.status(400).body(BaseResponse.error(400, "인증 코드 검증에 실패했습니다."));
    }
  }

  @Override
  public ResponseEntity<BaseResponse<Void>> checkVerification(
      @Valid @RequestBody EmailVerificationStatusRequest request) {
    boolean isVerified = authService.checkVerificationEmail(request);
    if (isVerified) {
      return ResponseEntity.status(200).body(BaseResponse.success(200, "검증된 이메일입니다.", null));
    } else {
      return ResponseEntity.status(200).body(BaseResponse.success(200, "검증되지 않은 이메일입니다.", null));
    }
  }

  @Override
  public ResponseEntity<BaseResponse<Void>> register(@Valid @RequestBody SignUpRequest request) {
    authService.signUp(request);
    return ResponseEntity.status(201).body(BaseResponse.success(201, "회원가입에 성공했습니다.", null));
  }

  @Override
  public ResponseEntity<BaseResponse<LoginResponse>> login(
      @Valid @RequestBody LoginRequest request) {
    TokenResponse tokenResponse = authService.login(request);

    ResponseCookie refreshCookie =
        jwtCookieWriter.addRefreshTokenToCookie(tokenResponse.getRefreshToken());

    return ResponseEntity.status(200)
        .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
        .body(
            BaseResponse.success(
                200, "로그인에 성공했습니다.", authMapper.toLoginResponse(tokenResponse.getAccessToken())));
  }
}
