/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.auth.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.skunivlikelion.homepage.domain.auth.dto.request.EmailVerificationConfirmReqeust;
import com.skunivlikelion.homepage.domain.auth.dto.request.EmailVerificationSendRequest;
import com.skunivlikelion.homepage.domain.auth.dto.request.EmailVerificationStatusRequest;
import com.skunivlikelion.homepage.domain.auth.dto.request.LoginRequest;
import com.skunivlikelion.homepage.domain.auth.dto.request.SignUpRequest;
import com.skunivlikelion.homepage.domain.auth.dto.response.PasswordReissueResponse;
import com.skunivlikelion.homepage.domain.auth.dto.response.TokenResponse;
import com.skunivlikelion.homepage.global.common.BaseResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 멋쟁이사자처럼 홈페이지 인증 관련 Controller interface 입니다.
 *
 * @since 2026.01.24
 * @see com.skunivlikelion.homepage.domain.auth.service.AuthService
 * @author Keum Si Eon
 * @version latest: 1
 */
@RequestMapping("/api")
@Tag(name = "Auth", description = "사용자 인증 및 검증 관련 기능을 제공하는 API")
public interface AuthController {

  @Operation(
      summary = "[ 사용자 | 토큰 X | 이메일 인증 코드 전송 ]",
      description =
          """
            **Parameters**  \n
            email: 인증 코드를 받을 사람의 이메일 주소   \n
            10 ~ 15초 정도 소요. \n
            비동기로 처리되지만 완료될 때까지 대기 후 결과 반환   \n

            **Returns** \n
            인증 코드 전송 성공/실패 여부   \n
            """)
  @PostMapping("/v1/auth/email/verify/request")
  ResponseEntity<BaseResponse<Void>> requestVerification(
      @Valid @RequestBody EmailVerificationSendRequest request);

  @Operation(
      summary = "[ 사용자 | 토큰 X | 이메일 인증 코드 검증 ]",
      description =
          """
           **Parameters**  \n
           email: 인증을 받을 사람의 이메일 주소  \n
           code: 이메일로 발송된 인증 코드  \n
           두 값이 일치하면 인증 성공  \n

           **Returns**  \n
           이메일 인증 코드 검증 성공/실패 여부
           """)
  @PostMapping("/v1/auth/email/verify/confirm")
  ResponseEntity<BaseResponse<Void>> confirmVerification(
      @Valid @RequestBody EmailVerificationConfirmReqeust request);

  @Operation(
      summary = "[ 관리자 | 토큰 O | 이메일 검증 상태 확인 ]",
      description =
          """
           **Parameters**  \n
           email: 검증 상태를 확인할 이메일 주소  \n

           **Returns**  \n
           검증 성공 여부 문자열 \n
           (success는 성공 여부 관계 없이 true)
           """)
  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping("/v1/admin/auth/email/verify/status")
  ResponseEntity<BaseResponse<Void>> checkVerification(
      @Valid @RequestBody EmailVerificationStatusRequest request);

  @Operation(
      summary = "[ 사용자 | 토큰 X | 회원가입 ]",
      description =
          """
          **Parameters**  \n
          email: 사용자 이메일 주소  \n
          password: 사용자 비밀번호 \n
          name: 이름(본명) \n
          department: 학과 \n
          studentNumber: 학번  \n
          phoneNumber: 전화번호  \n

          **Returns**  \n
          회원가입 성공 여부
          """)
  @PostMapping("/v1/auth/register")
  ResponseEntity<BaseResponse<Void>> register(@Valid @RequestBody SignUpRequest request);

  @Operation(
      summary = "[ 사용자 | 토큰 X | 로그인 ]",
      description =
          """
          **Parameters**  \n
          email: 사용자 이메일 주소  \n
          password: 사용자 비밀번호 \n

          **Returns (쿠키에 전달 [개발에서는 응답값 활용])**  \n
          ACCESS_TOKEN: JWT 액세스 토큰 \n
          REFRESH_TOKEN: JWT 리프레시 토큰 \n
          """)
  @PostMapping("/v1/auth/login")
  ResponseEntity<BaseResponse<TokenResponse>> login(@Valid @RequestBody LoginRequest request);

  @Operation(
      summary = "[ 사용자 | 토큰 X | 비밀번호 재발급 ]",
      description =
          """
          **Parameters**  \n
          email: 인증을 받을 사람의 이메일 주소  \n
          code: 이메일로 발송된 인증 코드  \n
          두 값이 일치하면 인증 성공  \n

          **Returns**  \n
          email: 인증된 사람의 이메일 주소 \n
          newPassword: 임시 발급된 비밀번호  \n
          """)
  @PostMapping("/v1/auth/password/reissue")
  ResponseEntity<BaseResponse<PasswordReissueResponse>> reissuePassword(
      @Valid @RequestBody EmailVerificationConfirmReqeust request);

  @Operation(
      summary = "[ 사용자 | 토큰 O | 토큰 재발급 ]",
      description =
          """
          **Returns (쿠키에 전달 [개발에서는 응답값 활용])**  \n
          ACCESS_TOKEN: JWT 액세스 토큰 \n
          REFRESH_TOKEN: JWT 리프레시 토큰 \n
          """)
  @PostMapping("/v1/auth/refresh")
  ResponseEntity<BaseResponse<TokenResponse>> refresh(HttpServletRequest request);

  @Operation(
      summary = "[ 사용자 | 토큰 O | 로그아웃 ]",
      description =
          """
          **Returns (쿠키에 전달 [개발에서는 응답값 활용])**  \n
          ACCESS_TOKEN: 0초 후 만료되는 ACCESS_TOKEN \n
          REFRESH_TOKEN: 0초 후 만료되는 REFRESH_TOKEN \n
          """)
  @PostMapping("/v1/auth/logout")
  ResponseEntity<BaseResponse<Void>> logout(HttpServletRequest request);
}
