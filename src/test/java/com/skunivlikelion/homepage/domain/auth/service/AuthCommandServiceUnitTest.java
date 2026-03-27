/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verifyNoInteractions;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

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
import com.skunivlikelion.homepage.global.exception.CustomException;
import com.skunivlikelion.homepage.global.security.CustomUserDetails;
import com.skunivlikelion.homepage.global.security.jwt.JwtProvider;
import com.skunivlikelion.homepage.global.security.jwt.TokenType;

@ExtendWith(MockitoExtension.class)
public class AuthCommandServiceUnitTest {

  @Mock UserRepository userRepository;
  @Mock RedisTemplate<String, String> redisTemplate;
  @Mock ValueOperations<String, String> valueOperations;
  @Mock PasswordEncoder passwordEncoder;
  @Mock JwtProvider jwtProvider;
  @Mock AuthenticationManager authenticationManager;
  @Mock Authentication authentication;
  @Mock AuthGenerator authGenerator;
  @Mock AuthMapper authMapper;
  @Mock UserDetailsService userDetailsService;
  @Mock JavaMailSender emailSender;

  @InjectMocks AuthServiceImpl authService;

  private static User user1;
  private static User user2;
  private static User user3;
  private static final String EMAIL_VERIFICATION_CODE = "EmailVerification:";
  private static final String VERIFIED_EMAIL_CODE = "VerifiedEmail:";
  private static final Integer EMAIL_TIMEOUT = 320;
  private static final TimeUnit EMAIL_TIMEOUT_UNIT = TimeUnit.SECONDS;

  @BeforeEach
  void setUp() {
    initUser();
  }

  @Test
  @DisplayName("인증 코드 전송 - 이메일 입력 시 인증 코드 전송 (성공)")
  void emailVerificationSend_success() throws MessagingException {
    // given
    String email = "test@skuniv.ac.kr";
    String redisKey = EMAIL_VERIFICATION_CODE + email;
    String verificationCode = "123456";

    when(authGenerator.generateVerificationCode()).thenReturn(verificationCode);
    MimeMessage mimeMessage = mock(MimeMessage.class);
    when(emailSender.createMimeMessage()).thenReturn(mimeMessage);
    MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
    helper.setFrom("skunivlikelion@gmail.com");
    helper.setTo(email);
    helper.setSubject("서경대학교 멋쟁이사자처럼 : 본인확인 인증코드");

    when(redisTemplate.opsForValue()).thenReturn(valueOperations);

    // when
    CompletableFuture<Boolean> result = authService.sendVerificationEmail(email);

    // then
    assertThat(result.join()).isTrue();
    verify(authGenerator).generateVerificationCode();
    verify(emailSender).createMimeMessage();
    verify(emailSender).send(mimeMessage);

    verify(redisTemplate).opsForValue();
    verify(valueOperations).set(redisKey, verificationCode, EMAIL_TIMEOUT, EMAIL_TIMEOUT_UNIT);
  }

  @Test
  @DisplayName("인증 코드 전송 - IOException 발생 시 false 반환 (실패)")
  void emailVerificationSend_IOException_fail() throws IOException {
    // given
    String email = "test@skuniv.ac.kr";
    String verificationCode = "123456";

    when(authGenerator.generateVerificationCode()).thenReturn(verificationCode);

    MimeMessage mimeMessage = mock(MimeMessage.class);
    when(emailSender.createMimeMessage()).thenReturn(mimeMessage);
    AuthServiceImpl spyAuthService = spy(authService);
    doThrow(new IOException("html 읽기 실패")).when(spyAuthService).getHtmlContent();

    // when
    CompletableFuture<Boolean> result = spyAuthService.sendVerificationEmail(email);

    // then
    assertThat(result.join()).isFalse();

    verify(emailSender).createMimeMessage();
    verify(emailSender, never()).send(any(MimeMessage.class));
    verify(valueOperations, never()).set(anyString(), anyString(), anyLong(), any(TimeUnit.class));
  }

  @Test
  @DisplayName("인증 코드 전송 - Exception 발생 시 false 반환 (실패)")
  void emailVerificationSend_Exception_fail() {
    // given
    String email = "test@skuniv.ac.kr";
    String redisKey = EMAIL_VERIFICATION_CODE + email;
    String verificationCode = "123456";

    when(authGenerator.generateVerificationCode()).thenReturn(verificationCode);
    MimeMessage mimeMessage = mock(MimeMessage.class);
    when(emailSender.createMimeMessage()).thenReturn(mimeMessage);
    when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    doThrow(new RuntimeException("Redis 저장 실패"))
        .when(valueOperations)
        .set(redisKey, verificationCode, EMAIL_TIMEOUT, EMAIL_TIMEOUT_UNIT);

    // when
    CompletableFuture<Boolean> result = authService.sendVerificationEmail(email);

    // then
    assertThat(result.join()).isFalse();

    verify(emailSender).createMimeMessage();
    verify(emailSender, never()).send(any(MimeMessage.class));
    verify(redisTemplate).opsForValue();
  }

  @Test
  @DisplayName("인증 코드 검증 - 일치하는 코드 입력 시 이메일 인증 완료 (성공)")
  void confirmVerificationCode_success() {
    // given
    String email = "test@skuniv.ac.kr";
    String verificationCode = "123456";
    String savedCode = "123456";
    EmailVerificationConfirmReqeust request =
        EmailVerificationConfirmReqeust.builder().email(email).code(verificationCode).build();

    String redisKey = EMAIL_VERIFICATION_CODE + email;
    when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    when(valueOperations.get(redisKey)).thenReturn(savedCode);

    // when
    boolean result = authService.confirmVerificationCode(request);

    // then
    assertThat(result).isTrue();
    verify(redisTemplate).delete(redisKey);
    verify(valueOperations).set(VERIFIED_EMAIL_CODE + email, "true", 24, TimeUnit.HOURS);
  }

  @Test
  @DisplayName("인증 코드 검증 - 일치하지 않는 코드 입력 시 이메일 인증 실패 (실패)")
  void confirmVerificationCode_fail() {
    // given
    String email = "test@skuniv.ac.kr";
    String verificationCode = "123456";
    String savedCode = "123457";
    EmailVerificationConfirmReqeust request =
        EmailVerificationConfirmReqeust.builder().email(email).code(verificationCode).build();

    String redisKey = EMAIL_VERIFICATION_CODE + email;
    when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    when(valueOperations.get(redisKey)).thenReturn(savedCode);

    // when
    boolean result = authService.confirmVerificationCode(request);

    // then
    assertThat(result).isFalse();
    verifyNoMoreInteractions(redisTemplate);
  }

  @Test
  @DisplayName("인증 코드 검증 - 해당 이메일에 인증 코드가 전송되지 않았을 경우 (실패)")
  void confirmVerificationCode_null_fail() {
    // given
    String email = "test@skuniv.ac.kr";
    String verificationCode = "123456";
    EmailVerificationConfirmReqeust request =
        EmailVerificationConfirmReqeust.builder().email(email).code(verificationCode).build();

    String redisKey = EMAIL_VERIFICATION_CODE + email;
    when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    when(valueOperations.get(redisKey)).thenReturn(null);

    // when
    boolean result = authService.confirmVerificationCode(request);

    // then
    assertThat(result).isFalse();
    verifyNoMoreInteractions(redisTemplate);
  }

  @Test
  @DisplayName("이메일 인증 여부 확인 - 인증이 완료된 이메일 입력 시 true 반환 (성공)")
  void checkVerificationEmail_success_true() {
    // given
    String email = "test@skuniv.ac.kr";
    EmailVerificationStatusRequest request =
        EmailVerificationStatusRequest.builder().email(email).build();
    String redisKey = VERIFIED_EMAIL_CODE + email;

    when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    when(valueOperations.get(redisKey)).thenReturn("true");
    // when
    boolean result = authService.checkVerificationEmail(request);

    // then
    assertThat(result).isTrue();
  }

  @Test
  @DisplayName("이메일 인증 여부 확인 - 인증이 완료되지 않은 이메일 입력 시 false 반환 (성공)")
  void checkVerificationEmail_success_false() {
    // given
    String email = "test@skuniv.ac.kr";
    EmailVerificationStatusRequest request =
        EmailVerificationStatusRequest.builder().email(email).build();
    String redisKey = VERIFIED_EMAIL_CODE + email;

    when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    when(valueOperations.get(redisKey)).thenReturn("not true");
    // when
    boolean result = authService.checkVerificationEmail(request);

    // then
    assertThat(result).isFalse();
  }

  @Test
  @DisplayName("회원가입 - 이메일 인증 완료 시 정상 가입 (성공)")
  void signUp_success() {
    // given
    SignUpRequest request =
        new SignUpRequest(
            "test@skuiv.ac.kr", "password", "홍길동", "소프트웨어학과", "20231234", "010-1234-5678");

    String redisKey = VERIFIED_EMAIL_CODE + request.getEmail();

    when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    when(valueOperations.get(redisKey)).thenReturn("verified");
    when(passwordEncoder.encode(request.getPassword())).thenReturn("encoded_pw");
    when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

    // when
    authService.signUp(request);

    // then
    verify(userRepository).save(any(User.class));
    verify(redisTemplate).delete(redisKey);
  }

  @Test
  @DisplayName("회원가입 - 이메일 인증 실패 시 가입 실패 (실패)")
  void signUp_fail() {
    // given
    SignUpRequest request =
        SignUpRequest.builder()
            .email("test@skuniv.ac.kr")
            .password("password")
            .name("홍길동")
            .department("소프트웨어학과")
            .studentNumber("2021123456")
            .phoneNumber("010-1234-5678")
            .build();

    String redisKey = VERIFIED_EMAIL_CODE + request.getEmail();

    when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    when(valueOperations.get(redisKey)).thenReturn(null);

    // when
    assertThatThrownBy(() -> authService.signUp(request))
        .isInstanceOf(CustomException.class)
        .satisfies(
            ex -> {
              CustomException ce = (CustomException) ex;
              assertThat(ce.getErrorCode()).isEqualTo(AuthErrorCode.NOT_VERIFICATED_EMAIL);
            });

    // then
    verify(userRepository, never()).save(any(User.class));
    verify(redisTemplate, never()).delete(anyString());
    verify(passwordEncoder, never()).encode(anyString());
  }

  @Test
  @DisplayName("로그인 - 성공 시 토큰을 반환 (성공)")
  void login_success() {
    // given
    LoginRequest request =
        LoginRequest.builder().email("test@skuniv.ac.kr").password("password").build();

    TokenResponse tokenResponse =
        TokenResponse.builder().accessToken("access-token").refreshToken("refresh-token").build();

    when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
        .thenReturn(authentication);
    when(jwtProvider.generateTokenResponse(authentication)).thenReturn(tokenResponse);

    // when
    TokenResponse result = authService.login(request);

    // then
    assertThat(result).isEqualTo(tokenResponse);

    verify(authenticationManager)
        .authenticate(
            argThat(
                auth ->
                    auth instanceof UsernamePasswordAuthenticationToken
                        && request.getEmail().equals(auth.getPrincipal())
                        && request.getPassword().equals(auth.getCredentials())));
    verify(jwtProvider).generateTokenResponse(authentication);
    verifyNoMoreInteractions(jwtProvider, authenticationManager);
  }

  @Test
  @DisplayName("로그인 - 실패 시 400응답 발생 (실패)")
  void login_fail() {
    // given
    LoginRequest request =
        LoginRequest.builder().email("test@skuniv.ac.kr").password("password").build();

    when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
        .thenThrow(new BadCredentialsException("bad credentials"));

    // when
    assertThatThrownBy(() -> authService.login(request))
        .isInstanceOf(CustomException.class)
        .satisfies(
            ex -> {
              CustomException ce = (CustomException) ex;
              assertThat(ce.getErrorCode()).isEqualTo(AuthErrorCode.LOGIN_FAIL);
            });

    // then
    verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    verifyNoInteractions(jwtProvider);
    verifyNoMoreInteractions(authenticationManager, jwtProvider);
  }

  @Test
  @DisplayName("비밀번호 찾기 - 성공 시 새 비밀번호 발급 (성공)")
  void reissuePassword_success() {
    // given
    String verificationCode = "123456";
    EmailVerificationConfirmReqeust request =
        EmailVerificationConfirmReqeust.builder()
            .email("test@skuniv.ac.kr")
            .code(verificationCode)
            .build();
    String redisKey = EMAIL_VERIFICATION_CODE + request.getEmail();

    when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);
    when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    when(valueOperations.get(redisKey)).thenReturn(verificationCode);
    when(userRepository.findByEmail(eq(request.getEmail()))).thenReturn(Optional.ofNullable(user1));

    String temporaryPassword = "temporary1234";
    when(authGenerator.generateTemporaryPassword()).thenReturn(temporaryPassword);
    when(passwordEncoder.encode(temporaryPassword)).thenReturn("encryptedPassword");

    PasswordReissueResponse expectedResponse =
        PasswordReissueResponse.builder()
            .email(request.getEmail())
            .temporaryPassword(temporaryPassword)
            .build();
    when(authMapper.toPasswordResetResponse(request.getEmail(), temporaryPassword))
        .thenReturn(expectedResponse);

    // when
    PasswordReissueResponse result = authService.reissuePassword(request);

    // then
    assertThat(result).isEqualTo(expectedResponse);
    assertThat(result.getEmail()).isEqualTo(request.getEmail());
    assertThat(result.getTemporaryPassword()).isEqualTo(temporaryPassword);

    verify(authMapper).toPasswordResetResponse(request.getEmail(), temporaryPassword);
    verify(userRepository).existsByEmail(request.getEmail());
    verify(valueOperations).get(redisKey);
    verify(userRepository).findByEmail(eq(request.getEmail()));
    verify(passwordEncoder).encode(temporaryPassword);
    verify(redisTemplate).delete(redisKey);
  }

  @Test
  @DisplayName("비밀번호 찾기 - 이메일 없을 시 404응답 발생 (실패)")
  void reissuePassword_emailNotFound_fail() {
    // given
    String verificationCode = "123456";
    EmailVerificationConfirmReqeust request =
        EmailVerificationConfirmReqeust.builder()
            .email("test@skuniv.ac.kr")
            .code(verificationCode)
            .build();

    when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);

    // when
    assertThatThrownBy(() -> authService.reissuePassword(request))
        .isInstanceOf(CustomException.class)
        .satisfies(
            ex -> {
              CustomException ce = (CustomException) ex;
              assertThat(ce.getErrorCode()).isEqualTo(AuthErrorCode.NOT_FOUND_EMAIL);
            });

    // then
    verifyNoInteractions(redisTemplate);
    verify(userRepository, never()).findByEmail(anyString());
    verify(authGenerator, never()).generateTemporaryPassword();
    verify(passwordEncoder, never()).encode(anyString());
    verifyNoInteractions(authMapper);
  }

  @Test
  @DisplayName("비밀번호 찾기 - 인증 코드 검증 결과 없을 시 400응답 발생 (실패)")
  void reissuePassword_emailInvalid_fail() {
    // given
    String verificationCode = "123456";
    EmailVerificationConfirmReqeust request =
        EmailVerificationConfirmReqeust.builder()
            .email("test@skuniv.ac.kr")
            .code(verificationCode)
            .build();
    String redisKey = EMAIL_VERIFICATION_CODE + request.getEmail();

    when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);
    when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    when(valueOperations.get(redisKey)).thenReturn(null);

    // when
    assertThatThrownBy(() -> authService.reissuePassword(request))
        .isInstanceOf(CustomException.class)
        .satisfies(
            ex -> {
              CustomException ce = (CustomException) ex;
              assertThat(ce.getErrorCode()).isEqualTo(AuthErrorCode.NOT_VERIFICATED_EMAIL);
            });

    // then
    verify(userRepository, never()).findByEmail(anyString());
    verify(authGenerator, never()).generateTemporaryPassword();
    verify(passwordEncoder, never()).encode(anyString());
    verify(redisTemplate, never()).delete(anyString());
    verifyNoInteractions(authMapper);
  }

  @Test
  @DisplayName("비밀번호 찾기 - 인증 코드 검증 결과 불일치 시 400응답 발생 (실패)")
  void reissuePassword_codeInvalid_fail() {
    // given
    String verificationCode = "123456";
    EmailVerificationConfirmReqeust request =
        EmailVerificationConfirmReqeust.builder()
            .email("test@skuniv.ac.kr")
            .code(verificationCode)
            .build();
    String redisKey = EMAIL_VERIFICATION_CODE + request.getEmail();

    when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);
    when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    when(valueOperations.get(redisKey)).thenReturn("789123");

    // when
    assertThatThrownBy(() -> authService.reissuePassword(request))
        .isInstanceOf(CustomException.class)
        .satisfies(
            ex -> {
              CustomException ce = (CustomException) ex;
              assertThat(ce.getErrorCode()).isEqualTo(AuthErrorCode.NOT_VERIFICATED_EMAIL);
            });

    // then
    verify(userRepository, never()).findByEmail(anyString());
    verify(authGenerator, never()).generateTemporaryPassword();
    verify(passwordEncoder, never()).encode(anyString());
    verify(redisTemplate, never()).delete(anyString());
    verifyNoInteractions(authMapper);
  }

  @Test
  @DisplayName("토큰 리프레시 - 토큰 검증 성공 시 토큰 응답 반환 (성공)")
  void refreshToken_success() {
    // given
    String refreshToken = "refresh-token";
    when(jwtProvider.validateTokenType(refreshToken, TokenType.REFRESH_TOKEN)).thenReturn(true);
    when(jwtProvider.validateRefreshToken(refreshToken)).thenReturn(true);

    UserDetails userDetails = new CustomUserDetails(user1);
    when(jwtProvider.getEmailFromToken(refreshToken)).thenReturn(user1.getEmail());
    when(userDetailsService.loadUserByUsername(user1.getEmail())).thenReturn(userDetails);

    TokenResponse expectedResponse =
        TokenResponse.builder()
            .accessToken("new-access-token")
            .refreshToken("new-refresh-token")
            .build();
    when(jwtProvider.generateTokenResponse(any(Authentication.class))).thenReturn(expectedResponse);
    // when
    TokenResponse result = authService.refresh(refreshToken);

    // then
    assertThat(result.getAccessToken()).isEqualTo(expectedResponse.getAccessToken());
    assertThat(result.getRefreshToken()).isEqualTo(expectedResponse.getRefreshToken());

    verify(jwtProvider).validateTokenType(refreshToken, TokenType.REFRESH_TOKEN);
    verify(jwtProvider).validateRefreshToken(refreshToken);
    verify(jwtProvider).getEmailFromToken(refreshToken);
    verify(userDetailsService).loadUserByUsername(user1.getEmail());
    verify(jwtProvider).deleteRefreshToken(refreshToken);
    verify(jwtProvider)
        .generateTokenResponse(
            argThat(auth -> auth instanceof UsernamePasswordAuthenticationToken));

    verifyNoMoreInteractions(jwtProvider, userDetailsService);
  }

  @Test
  @DisplayName("토큰 리프레시 - 토큰 타입 불일치 시 401응답 발생 (실패)")
  void refreshToken_tokenTypeInvalid_fail() {
    // given
    String refreshToken = "refresh-token";

    when(jwtProvider.validateTokenType(refreshToken, TokenType.REFRESH_TOKEN)).thenReturn(false);
    // when
    assertThatThrownBy(() -> authService.refresh(refreshToken))
        .isInstanceOf(CustomException.class)
        .satisfies(
            ex -> {
              CustomException ce = (CustomException) ex;
              assertThat(ce.getErrorCode()).isEqualTo(AuthErrorCode.UNAUTHORIZED_TOKEN);
            });

    // then
    verifyNoMoreInteractions(jwtProvider);
    verifyNoInteractions(userDetailsService);
  }

  @Test
  @DisplayName("토큰 리프레시 - 토큰이 유효하지 않을 시 401응답 발생 (실패)")
  void refreshToken_tokenInvalid_fail() {
    // given
    String refreshToken = "refresh-token";

    when(jwtProvider.validateTokenType(refreshToken, TokenType.REFRESH_TOKEN)).thenReturn(true);
    when(jwtProvider.validateRefreshToken(refreshToken)).thenReturn(false);
    // when
    assertThatThrownBy(() -> authService.refresh(refreshToken))
        .isInstanceOf(CustomException.class)
        .satisfies(
            ex -> {
              CustomException ce = (CustomException) ex;
              assertThat(ce.getErrorCode()).isEqualTo(AuthErrorCode.UNAUTHORIZED_TOKEN);
            });

    // then
    verifyNoMoreInteractions(jwtProvider);
    verifyNoInteractions(userDetailsService);
  }

  @Test
  @DisplayName("사용자 정보 중복 확인 - 중복되지 않은 사용자 정보 입력 (성공)")
  void validateUniqueValues_success() {
    // given
    String email = "test@skuniv.ac.kr";
    String studentNumber = "2021123456";
    String phoneNumber = "010-1234-5678";

    when(userRepository.findFirstByEmailOrStudentNumberOrPhoneNumber(
            email, studentNumber, phoneNumber))
        .thenReturn(Optional.empty());

    // when
    assertThatCode(() -> authService.validateUniqueValues(email, studentNumber, phoneNumber))
        .doesNotThrowAnyException();

    // then
    verify(userRepository)
        .findFirstByEmailOrStudentNumberOrPhoneNumber(email, studentNumber, phoneNumber);
  }

  @Test
  @DisplayName("사용자 정보 중복 확인 - 이메일 중복 사용자 정보 입력 (실패)")
  void validateUniqueValues_emailDuplicate_fail() {
    // given
    String email = "test@skuniv.ac.kr";
    String studentNumber = "2021123456";
    String phoneNumber = "010-1234-5678";

    when(userRepository.findFirstByEmailOrStudentNumberOrPhoneNumber(
            email, studentNumber, phoneNumber))
        .thenReturn(Optional.of(user1));

    // when
    assertThatThrownBy(() -> authService.validateUniqueValues(email, studentNumber, phoneNumber))
        .isInstanceOf(CustomException.class)
        .satisfies(
            ex -> {
              CustomException ce = (CustomException) ex;
              assertThat(ce.getErrorCode()).isEqualTo(AuthErrorCode.ALREADY_EXIST_EMAIL);
            });

    // then
    verify(userRepository)
        .findFirstByEmailOrStudentNumberOrPhoneNumber(email, studentNumber, phoneNumber);
  }

  @Test
  @DisplayName("사용자 정보 중복 확인 - 학번 중복 사용자 정보 입력 (실패)")
  void validateUniqueValues_studentNumberDuplicate_fail() {
    // given
    String email = "test@skuniv.ac.kr";
    String studentNumber = "2021123456";
    String phoneNumber = "010-1234-5678";

    when(userRepository.findFirstByEmailOrStudentNumberOrPhoneNumber(
            email, studentNumber, phoneNumber))
        .thenReturn(Optional.of(user2));

    // when
    assertThatThrownBy(() -> authService.validateUniqueValues(email, studentNumber, phoneNumber))
        .isInstanceOf(CustomException.class)
        .satisfies(
            ex -> {
              CustomException ce = (CustomException) ex;
              assertThat(ce.getErrorCode()).isEqualTo(AuthErrorCode.ALREADY_EXIST_STUDENTNUMBER);
            });

    // then
    verify(userRepository)
        .findFirstByEmailOrStudentNumberOrPhoneNumber(email, studentNumber, phoneNumber);
  }

  @Test
  @DisplayName("사용자 정보 중복 확인 - 전화번호 중복 사용자 정보 입력 (실패)")
  void validateUniqueValues_phoneNumberDuplicate_fail() {
    // given
    String email = "test@skuniv.ac.kr";
    String studentNumber = "2021123456";
    String phoneNumber = "010-1234-5678";

    when(userRepository.findFirstByEmailOrStudentNumberOrPhoneNumber(
            email, studentNumber, phoneNumber))
        .thenReturn(Optional.of(user3));

    // when
    assertThatThrownBy(() -> authService.validateUniqueValues(email, studentNumber, phoneNumber))
        .isInstanceOf(CustomException.class)
        .satisfies(
            ex -> {
              CustomException ce = (CustomException) ex;
              assertThat(ce.getErrorCode()).isEqualTo(AuthErrorCode.ALREADY_EXIST_PHONENUMBER);
            });

    // then
    verify(userRepository)
        .findFirstByEmailOrStudentNumberOrPhoneNumber(email, studentNumber, phoneNumber);
  }

  @Test
  @DisplayName("로그아웃 - 유효한 리프레시 토큰 입력 시 로그아웃 (성공)")
  void logout_success() {
    // given
    String email = "test@skuniv.ac.kr";
    String refreshToken = "refresh-token";

    when(jwtProvider.validateTokenType(refreshToken, TokenType.REFRESH_TOKEN)).thenReturn(true);
    when(jwtProvider.validateRefreshToken(refreshToken)).thenReturn(true);
    when(jwtProvider.getEmailFromToken(refreshToken)).thenReturn(email);

    // when
    authService.logout(refreshToken);

    // then
    verify(jwtProvider).deleteRefreshToken(refreshToken);
  }

  @Test
  @DisplayName("로그아웃 - 허용되지 않은 토큰 타입 입력 시 로그아웃 실패 (실패)")
  void logout_illegalTokenType_fail() {
    // given
    String refreshToken = "refresh-token";

    when(jwtProvider.validateTokenType(refreshToken, TokenType.REFRESH_TOKEN)).thenReturn(false);

    // when
    assertThatThrownBy(() -> authService.logout(refreshToken))
        .isInstanceOf(CustomException.class)
        .satisfies(
            ex -> {
              CustomException ce = (CustomException) ex;
              assertThat(ce.getErrorCode()).isEqualTo(AuthErrorCode.UNAUTHORIZED_TOKEN);
            });

    // then
    verifyNoMoreInteractions(jwtProvider);
  }

  @Test
  @DisplayName("로그아웃 - 유효하지 않은 리프레시 토큰 입력 시 로그아웃 실패 (실패)")
  void logout_invalidRefreshToken_fail() {
    // given
    String refreshToken = "refresh-token";

    when(jwtProvider.validateTokenType(refreshToken, TokenType.REFRESH_TOKEN)).thenReturn(true);
    when(jwtProvider.validateRefreshToken(refreshToken)).thenReturn(false);

    // when
    assertThatThrownBy(() -> authService.logout(refreshToken))
        .isInstanceOf(CustomException.class)
        .satisfies(
            ex -> {
              CustomException ce = (CustomException) ex;
              assertThat(ce.getErrorCode()).isEqualTo(AuthErrorCode.UNAUTHORIZED_TOKEN);
            });

    // then
    verifyNoMoreInteractions(jwtProvider);
  }

  @Test
  @DisplayName("임의의 이메일 검증 - 알맞은 이메일 입력 (성공)")
  void verifyOptionEmail_success() {
    // given
    String email = "test@skuniv.ac.kr";

    when(redisTemplate.opsForValue()).thenReturn(valueOperations);

    // when
    authService.verifyOptionEmail(email);

    // then
    verify(valueOperations).set(VERIFIED_EMAIL_CODE + email, "true", 24, TimeUnit.HOURS);
  }

  private void initUser() {
    user1 =
        User.builder()
            .email("test@skuniv.ac.kr")
            .password("password")
            .name("홍길동")
            .department("소프트웨어학과")
            .studentNumber("2021000000")
            .phoneNumber("010-0000-0000")
            .build();

    user2 =
        User.builder()
            .email("test2@skuniv.ac.kr")
            .password("password")
            .name("홍길동")
            .department("소프트웨어학과")
            .studentNumber("2021123456")
            .phoneNumber("010-0000-0001")
            .build();
    user3 =
        User.builder()
            .email("test3@skuniv.ac.kr")
            .password("password")
            .name("홍길동")
            .department("소프트웨어학과")
            .studentNumber("2021000000")
            .phoneNumber("010-1234-5678")
            .build();
  }
}
