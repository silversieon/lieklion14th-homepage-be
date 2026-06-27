/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.auth.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import jakarta.mail.internet.MimeMessage;

import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.skunivlikelion.homepage.domain.auth.dto.request.EmailVerificationConfirmReqeust;
import com.skunivlikelion.homepage.domain.auth.dto.request.EmailVerificationStatusRequest;
import com.skunivlikelion.homepage.domain.auth.dto.request.LoginRequest;
import com.skunivlikelion.homepage.domain.auth.dto.request.SignUpRequest;
import com.skunivlikelion.homepage.domain.auth.dto.response.PasswordReissueResponse;
import com.skunivlikelion.homepage.domain.auth.dto.response.TokenResponse;
import com.skunivlikelion.homepage.domain.auth.exception.AuthErrorCode;
import com.skunivlikelion.homepage.domain.auth.mapper.AuthMapper;
import com.skunivlikelion.homepage.domain.auth.util.AuthGenerator;
import com.skunivlikelion.homepage.domain.user.entity.User;
import com.skunivlikelion.homepage.domain.user.repository.UserRepository;
import com.skunivlikelion.homepage.global.annotation.TimeTrace;
import com.skunivlikelion.homepage.global.exception.CustomException;
import com.skunivlikelion.homepage.global.security.jwt.JwtProvider;
import com.skunivlikelion.homepage.global.security.jwt.TokenType;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

  private final JavaMailSender emailSender;
  private final RedisTemplate<String, String> redisTemplate;
  private final PasswordEncoder passwordEncoder;
  private final UserRepository userRepository;
  private final JwtProvider jwtProvider;
  private final AuthMapper authMapper;
  private final AuthGenerator authGenerator;
  private final AuthenticationManager authenticationManager;
  private final UserDetailsService userDetailsService;

  private static final String EMAIL_VERIFICATION_CODE = "EmailVerification:";
  private static final String VERIFIED_EMAIL_CODE = "VerifiedEmail:";
  private static final Integer EMAIL_TIMEOUT = 320;
  private static final TimeUnit EMAIL_TIMEOUT_UNIT = TimeUnit.SECONDS;

  @Override
  @TimeTrace(
      methodName = "인증 코드 전송",
      env = {"local", "dev", "prod"})
  @Async("emailExecutor")
  public void sendVerificationEmail(String email) {
    try {
      String verificationCode = authGenerator.generateVerificationCode();
      String redisKey = EMAIL_VERIFICATION_CODE + email;

      MimeMessage mimeMessage = emailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

      helper.setFrom("skunivlikelion@gmail.com");
      helper.setTo(email);
      helper.setSubject("서경대학교 멋쟁이사자처럼 : 본인확인 인증코드");

      String htmlContent = getHtmlContent();
      htmlContent = htmlContent.replace("${verificationCode}", verificationCode);
      helper.setText(htmlContent, true);

      redisTemplate
          .opsForValue()
          .set(redisKey, verificationCode, EMAIL_TIMEOUT, EMAIL_TIMEOUT_UNIT);

      log.info("[Auth] 인증 코드 전송 완료 - 수신자: {}", email);
      emailSender.send(mimeMessage);
    } catch (IOException e) {
      log.error("[Auth] html 파일 읽기 실패 - 에러 메시지: {}", e.getMessage());
    } catch (Exception e) {
      log.error(
          "[Auth] Redis 저장 실패 - 수신자: {}, 에러 메시지: {}, 에러 타입: {}",
          email,
          e.getMessage(),
          e.getClass().getSimpleName());
    }
  }

  @Override
  public boolean confirmVerificationCode(EmailVerificationConfirmReqeust reqeust) {
    String email = reqeust.getEmail();
    String code = reqeust.getCode();
    String redisKey = EMAIL_VERIFICATION_CODE + email;
    String savedCode = redisTemplate.opsForValue().get(redisKey);

    if (savedCode != null && savedCode.equals(code)) {
      redisTemplate.delete(redisKey);
      redisTemplate.opsForValue().set(VERIFIED_EMAIL_CODE + email, "true", 24, TimeUnit.HOURS);
      log.info("[Auth] 인증 코드 검증 성공 - 인증된 이메일: {}", email);
      return true;
    }
    log.info("[Auth] 인증 코드 검증 실패 - 인증 실패 이메일: {}", email);
    return false;
  }

  @Override
  public boolean checkVerificationEmail(EmailVerificationStatusRequest request) {
    String email = request.getEmail();
    String redisKey = VERIFIED_EMAIL_CODE + email;
    return Boolean.TRUE.toString().equals(redisTemplate.opsForValue().get(redisKey));
  }

  @Override
  @Transactional
  public void signUp(SignUpRequest request) {
    String redisKey = VERIFIED_EMAIL_CODE + request.getEmail();
    if (redisTemplate.opsForValue().get(redisKey) == null) {
      log.error("[Auth] 검증되지 않은 이메일 입력 - 이메일: {}", request.getEmail());
      throw new CustomException(AuthErrorCode.NOT_VERIFICATED_EMAIL);
    }
    validateUniqueValues(request.getEmail(), request.getStudentNumber(), request.getPhoneNumber());

    String encodedPassword = passwordEncoder.encode(request.getPassword());
    User user =
        User.builder()
            .email(request.getEmail())
            .password(encodedPassword)
            .name(request.getName())
            .department(request.getDepartment())
            .studentNumber(request.getStudentNumber())
            .phoneNumber(request.getPhoneNumber())
            .build();
    User savedUser = userRepository.save(user);
    redisTemplate.delete(redisKey);
    log.info("[Auth] 신규 사용자 회원가입 - 이메일: {}, 이름: {}", savedUser.getEmail(), savedUser.getName());
  }

  @Override
  @Transactional(readOnly = true)
  public TokenResponse login(LoginRequest request) {
    try {
      UsernamePasswordAuthenticationToken authenticationToken =
          new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());
      Authentication authentication = authenticationManager.authenticate(authenticationToken);
      TokenResponse tokenResponse = jwtProvider.generateTokenResponse(authentication);

      log.info("[Auth] 사용자 로그인 성공 - 이메일: {}", request.getEmail());
      return tokenResponse;
    } catch (BadCredentialsException | UsernameNotFoundException e) {
      log.info("[Auth] 로그인 실패 - 이메일: {}", request.getEmail());
      throw new CustomException(AuthErrorCode.LOGIN_FAIL);
    }
  }

  @Override
  @Transactional
  public PasswordReissueResponse reissuePassword(EmailVerificationConfirmReqeust reqeust) {
    String email = reqeust.getEmail();
    if (!userRepository.existsByEmail(email)) {
      log.info("[Auth] 존재하지 않는 이메일 입력 - 이메일: {}", email);
      throw new CustomException(AuthErrorCode.NOT_FOUND_EMAIL);
    }

    String code = reqeust.getCode();
    String redisKey = EMAIL_VERIFICATION_CODE + email;
    String savedCode = redisTemplate.opsForValue().get(redisKey);
    if (savedCode == null || !savedCode.equals(code)) {
      log.info("[Auth] 인증 코드 검증 실패 - 인증 실패 이메일: {}", email);
      throw new CustomException(AuthErrorCode.NOT_VERIFICATED_EMAIL);
    }

    User user =
        userRepository
            .findByEmail(email)
            .orElseThrow(() -> new CustomException(AuthErrorCode.NOT_FOUND_EMAIL));
    String temporaryPassword = authGenerator.generateTemporaryPassword();
    user.reissuePassword(passwordEncoder.encode(temporaryPassword));
    redisTemplate.delete(redisKey);

    log.info("[Auth] 임시 비밀번호 발급 성공 - 재발급된 이메일: {}", email);
    return authMapper.toPasswordResetResponse(email, temporaryPassword);
  }

  @Override
  @Transactional(readOnly = true)
  public TokenResponse refresh(String refreshToken) {
    validateRefreshToken(refreshToken);
    String email = jwtProvider.getEmailFromToken(refreshToken);

    UserDetails userDetails = userDetailsService.loadUserByUsername(email);
    Authentication authentication =
        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    jwtProvider.deleteRefreshToken(refreshToken);
    return jwtProvider.generateTokenResponse(authentication);
  }

  @Override
  public void validateUniqueValues(String email, String studentNumber, String phoneNumber) {
    Optional<User> duplicatedObj =
        userRepository.findFirstByEmailOrStudentNumberOrPhoneNumber(
            email, studentNumber, phoneNumber);
    if (duplicatedObj.isPresent()) {
      User duplicatedUser = duplicatedObj.get();
      log.info(
          "[Auth] 중복된 값을 입력한 회원가입 - 입력 이메일: {}, 입력 학번: {}, 입력 전화번호: {}",
          email,
          studentNumber,
          phoneNumber);
      if (email.equals(duplicatedUser.getEmail()))
        throw new CustomException(AuthErrorCode.ALREADY_EXIST_EMAIL);
      if (studentNumber.equals(duplicatedUser.getStudentNumber()))
        throw new CustomException(AuthErrorCode.ALREADY_EXIST_STUDENTNUMBER);
      if (phoneNumber.equals(duplicatedUser.getPhoneNumber()))
        throw new CustomException(AuthErrorCode.ALREADY_EXIST_PHONENUMBER);
    }
  }

  @Override
  public void logout(String refreshToken) {
    validateRefreshToken(refreshToken);
    String email = jwtProvider.getEmailFromToken(refreshToken);
    jwtProvider.deleteRefreshToken(refreshToken);
    log.info("[Auth] 사용자 로그아웃 - 이메일: {}", email);
  }

  @Override
  public void verifyOptionEmail(String email) {
    redisTemplate.opsForValue().set(VERIFIED_EMAIL_CODE + email, "true", 24, TimeUnit.HOURS);
    log.info("[Auth] 임의의 이메일 검증 성공 - 인증된 이메일: {}", email);
  }

  protected String getHtmlContent() throws IOException {
    ClassPathResource resource = new ClassPathResource("templates/MailForm.html");
    byte[] encoded = Files.readAllBytes(Paths.get(resource.getURI()));
    return new String(encoded, StandardCharsets.UTF_8);
  }

  private void validateRefreshToken(String refreshToken) {
    if (!jwtProvider.validateTokenType(refreshToken, TokenType.REFRESH_TOKEN)
        || !jwtProvider.validateRefreshToken(refreshToken)) {
      log.info("[Auth] 유효하지 않은 리프레시 토큰을 통한 리프레시 요청");
      throw new CustomException(AuthErrorCode.UNAUTHORIZED_TOKEN);
    }
  }
}
