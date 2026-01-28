/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.auth.util;

import java.util.Random;

import org.springframework.stereotype.Component;

@Component
public class AuthGeneratorImpl implements AuthGenerator {

  private static final String TEMPORARY_PASSWORD_CHARS =
      "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";

  @Override
  public String generateVerificationCode() {
    Random random = new Random();
    StringBuilder code = new StringBuilder();
    for (int i = 0; i < 6; i++) {
      code.append(random.nextInt(10));
    }
    return code.toString();
  }

  @Override
  public String generateTemporaryPassword() {
    StringBuilder temporaryPassword = new StringBuilder();
    Random random = new Random();
    for (int i = 0; i < 10; i++) {
      temporaryPassword.append(
          TEMPORARY_PASSWORD_CHARS.charAt(random.nextInt(TEMPORARY_PASSWORD_CHARS.length())));
    }
    return temporaryPassword.toString();
  }
}
