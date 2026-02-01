/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.auth.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.skunivlikelion.homepage.domain.auth.dto.request.EmailVerificationConfirmReqeust;
import com.skunivlikelion.homepage.domain.auth.dto.request.EmailVerificationSendRequest;
import com.skunivlikelion.homepage.domain.auth.dto.request.EmailVerificationStatusRequest;
import com.skunivlikelion.homepage.domain.auth.dto.request.LoginRequest;
import com.skunivlikelion.homepage.domain.auth.dto.request.SignUpRequest;
import com.skunivlikelion.homepage.domain.auth.dto.response.PasswordReissueResponse;
import com.skunivlikelion.homepage.domain.auth.dto.response.TokenResponse;
import com.skunivlikelion.homepage.domain.auth.exception.AuthErrorCode;
import com.skunivlikelion.homepage.domain.auth.service.AuthService;
import com.skunivlikelion.homepage.global.common.BaseResponse;
import com.skunivlikelion.homepage.global.exception.CustomException;
import com.skunivlikelion.homepage.global.security.jwt.JwtCookieWriter;
import com.skunivlikelion.homepage.global.security.jwt.JwtProvider;
import com.skunivlikelion.homepage.global.security.jwt.TokenType;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthControllerImpl implements AuthController {

  private final AuthService authService;
  private final JwtCookieWriter jwtCookieWriter;
  private final JwtProvider jwtProvider;

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
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<BaseResponse<Void>> checkVerification(
      @Valid @RequestBody EmailVerificationStatusRequest request) {
    boolean isVerified = authService.checkVerificationEmail(request);
    if (isVerified) {
      return ResponseEntity.status(200)
          .body(BaseResponse.success(200, "[Verified] 검증된 이메일입니다.", null));
    } else {
      return ResponseEntity.status(200)
          .body(BaseResponse.success(200, "[WARN] 검증되지 않은 이메일입니다.", null));
    }
  }

  @Override
  public ResponseEntity<BaseResponse<Void>> register(@Valid @RequestBody SignUpRequest request) {
    authService.signUp(request);
    return ResponseEntity.status(201).body(BaseResponse.success(201, "회원가입에 성공했습니다.", null));
  }

  @Override
  public ResponseEntity<BaseResponse<TokenResponse>> login(
      @Valid @RequestBody LoginRequest request) {
    TokenResponse tokenResponse = authService.login(request);
    HttpHeaders tokenHeaders = new HttpHeaders();
    tokenHeaders.add(
        HttpHeaders.SET_COOKIE,
        jwtCookieWriter.addAccessTokenToCookie(tokenResponse.getAccessToken()).toString());
    tokenHeaders.add(
        HttpHeaders.SET_COOKIE,
        jwtCookieWriter.addRefreshTokenToCookie(tokenResponse.getRefreshToken()).toString());
    return ResponseEntity.status(200)
        .headers(tokenHeaders)
        .body(BaseResponse.success(200, "로그인에 성공했습니다.", tokenResponse));
  }

  @Override
  public ResponseEntity<BaseResponse<PasswordReissueResponse>> reissuePassword(
      @Valid @RequestBody EmailVerificationConfirmReqeust request) {
    return ResponseEntity.status(200)
        .body(BaseResponse.success(200, "비밀번호 찾기에 성공했습니다.", authService.reissuePassword(request)));
  }

  @Override
  public ResponseEntity<BaseResponse<TokenResponse>> refresh(HttpServletRequest request) {
    String refreshToken = jwtProvider.extractRefreshToken(request);
    TokenResponse tokenResponse = authService.refresh(refreshToken);
    HttpHeaders tokenHeaders = new HttpHeaders();
    tokenHeaders.add(
        HttpHeaders.SET_COOKIE,
        jwtCookieWriter.addAccessTokenToCookie(tokenResponse.getAccessToken()).toString());
    tokenHeaders.add(
        HttpHeaders.SET_COOKIE,
        jwtCookieWriter.addRefreshTokenToCookie(tokenResponse.getRefreshToken()).toString());
    return ResponseEntity.status(200)
        .headers(tokenHeaders)
        .body(BaseResponse.success(200, "토큰 재발급에 성공했습니다.", tokenResponse));
  }

  @Override
  public ResponseEntity<BaseResponse<Void>> logout(HttpServletRequest request) {
    String refreshToken = jwtProvider.extractRefreshToken(request);
    authService.logout(refreshToken);
    HttpHeaders tokenHeaders = new HttpHeaders();
    tokenHeaders.add(
        HttpHeaders.SET_COOKIE,
        jwtCookieWriter.removeTokenFromCookie(TokenType.ACCESS_TOKEN).toString());
    tokenHeaders.add(
        HttpHeaders.SET_COOKIE,
        jwtCookieWriter.removeTokenFromCookie(TokenType.REFRESH_TOKEN).toString());

    return ResponseEntity.status(200)
        .headers(tokenHeaders)
        .body(BaseResponse.success(200, "로그아웃에 성공하였습니다.", null));
  }
}
