/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.auth.util;

/**
 * 멋쟁이사자처럼 홈페이지 인증 관련 Generator interface 입니다.
 *
 * @see com.skunivlikelion.homepage.domain.auth.service.AuthService
 * @since 2026.01.25
 * @author Keum Si Eon
 */
public interface AuthGenerator {

  /**
   * [ 인증 코드 생성 메서드 ] 계정 인증을 위한 코드를 생성합니다.
   *
   * @return 인증 코드 문자열
   */
  String generateVerificationCode();

  /**
   * [ 임시 비밀번호 발급 메서드 ] 비밀번호 찾기를 위한 임시 비밀번호를 생성합니다.
   *
   * @return 임시 비밀번호 문자열
   */
  String generateTemporaryPassword();
}
