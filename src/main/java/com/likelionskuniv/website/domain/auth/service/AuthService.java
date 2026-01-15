/* 
 * Copyright (c) SKU LIKELION 
 */
package com.likelionskuniv.website.domain.auth.service;

import java.util.concurrent.CompletableFuture;

import com.likelionskuniv.website.domain.auth.dto.request.EmailVerificationConfirmReqeust;
import com.likelionskuniv.website.domain.auth.dto.request.EmailVerificationStatusRequest;

public interface AuthService {

  /**
   * [ 인증 코드 전송 메서드 ] 성공 여부에 따라 CompletableFuture 객체에 성공 여부를 담아 반환
   *
   * @param email 인증 코드를 받을 이메일 문자열
   * @return 성공 여부를 담은 CompletableFuture 객체
   */
  CompletableFuture<Boolean> sendVerificationEmail(String email);

  /**
   * [ 인증 코드 검증 메서드 ] 성공 여부에 따라 boolean 값을 반환
   *
   * @param reqeust 인증 코드를 받은 이메일, 인증 코드를 담은 요청 객체
   * @return 성공 여부
   */
  boolean confirmVerificationCode(EmailVerificationConfirmReqeust reqeust);

  /**
   * [ 이메일 인증 상태 확인 메서드 ] 성공 여부에 따라 boolean 값을 반환
   *
   * @param request 인증 코드 검증 상태를 확인할 이메일을 담은 요청 객체
   * @return 성공 여부
   */
  boolean checkVerificationEmail(EmailVerificationStatusRequest request);
}
