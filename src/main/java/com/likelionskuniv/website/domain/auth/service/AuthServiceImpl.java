/* 
 * Copyright (c) SKU LIKELION 
 */
package com.likelionskuniv.website.domain.auth.service;

import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.likelionskuniv.website.domain.auth.dto.request.EmailVerificationConfirmReqeust;
import com.likelionskuniv.website.domain.auth.dto.request.EmailVerificationStatusRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

  private final JavaMailSender emailSender;
  private final RedisTemplate<String, String> redisTemplate;

  @Override
  @Async("emailExecutor")
  public CompletableFuture<Boolean> sendVerificationEmail(String email) {
    try {
      String verificationCode = generateVerificationCode();
      String redisKey = "EmailVerification:" + email;

      MimeMessage mimeMessage = emailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

      helper.setFrom("keumsiun0503@gmail.com");
      helper.setTo(email);
      helper.setSubject("서경대학교 멋쟁이사자처럼 : 본인확인 인증코드");

      String htmlContent =
          """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                </head>
                <body style="margin: 0; padding: 0; background-color: #f4f4f4;">
                    <div style="max-width: 600px; margin: 0 auto; background-color: #ffffff; padding: 20px; font-family: Arial, sans-serif;">
                        <div style="text-align: center; padding: 20px;">
                            <h1 style="color: #64A672; margin: 0;">서경대학교<br>멋쟁이사자처럼</h1>
                            <p style="color: #666666; margin-top: 10px;">이메일 인증</p>
                        </div>

                        <div style="padding: 20px; background-color: #f8f9fa; border-radius: 5px; margin: 20px 0;">
                            <p style="color: #333333; margin-bottom: 20px;">
                                안녕하세요.<br>
                                서경대학교 멋쟁이사자처럼 본인확인을 위한 코드입니다.<br>
                                아래의 인증 코드를 입력해주세요.<br>
                                유효기간은 5분입니다.
                            </p>

                            <div style="background-color: #ffffff; padding: 15px; border-radius: 5px; text-align: center; border: 1px solid #dee2e6;">
                                <h2 style="color: #64A672; letter-spacing: 5px; margin: 0;">%s</h2>
                            </div>

                            <p style="color: #666666; font-size: 14px; margin-top: 20px; text-align: center;">
                                인증 코드는 5분간 유효합니다.
                            </p>
                        </div>

                        <div style="text-align: center; padding: 20px; color: #999999; font-size: 12px;">
                            <p>본 메일은 발신전용 메일입니다.</p>
                            <p>© 2026 서경대학교 멋쟁이사자처럼. All rights reserved.</p>
                        </div>
                    </div>
                </body>
                </html>
                """
              .formatted(verificationCode);
      helper.setText(htmlContent, true);

      emailSender.send(mimeMessage);
      log.info("[Auth] 인증 코드 전송 완료 - 수신자: {}", email);
      redisTemplate.opsForValue().set(redisKey, verificationCode, 5, TimeUnit.MINUTES);

      return CompletableFuture.completedFuture(true);
    } catch (MessagingException e) {
      log.error("[Auth] 인증 코드 전송 실패 - 수신자: {}, 에러 메시지: {}", email, e.getMessage());
      return CompletableFuture.completedFuture(false);
    } catch (Exception e) {
      log.error(
          "Redis 저장 과정 실패 - 수신자: {}, 에러 메시지: {}, 에러 타입: {}",
          email,
          e.getMessage(),
          e.getClass().getSimpleName());
      return CompletableFuture.completedFuture(false);
    }
  }

  @Override
  public boolean confirmVerificationCode(EmailVerificationConfirmReqeust reqeust) {
    String email = reqeust.getEmail();
    String code = reqeust.getCode();
    String redisKey = "EmailVerification:" + email;
    String savedCode = redisTemplate.opsForValue().get(redisKey);

    if (savedCode != null && savedCode.equals(code)) {
      redisTemplate.delete(redisKey);
      redisTemplate.opsForValue().set("VerifiedEmail:" + email, "true", 24, TimeUnit.HOURS);
      log.info("[Auth] 인증 코드 검증 성공 - 인증된 이메일: {}", email);
      return true;
    }
    log.warn("[Auth] 인증 코드 검증 실패 - 인증 실패 이메일: {}", email);
    return false;
  }

  @Override
  public boolean checkVerificationEmail(EmailVerificationStatusRequest request) {
    String email = request.getEmail();
    String redisKey = "VerifiedEmail:" + email;
    return Boolean.TRUE.toString().equals(redisTemplate.opsForValue().get(redisKey));
  }

  private String generateVerificationCode() {
    Random random = new Random();
    StringBuilder code = new StringBuilder();
    for (int i = 0; i < 6; i++) {
      code.append(random.nextInt(10));
    }
    return code.toString();
  }
}
