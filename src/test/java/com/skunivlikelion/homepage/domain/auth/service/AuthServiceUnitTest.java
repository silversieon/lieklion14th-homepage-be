/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verifyNoInteractions;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.skunivlikelion.homepage.domain.auth.dto.request.EmailVerificationConfirmReqeust;
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
class AuthServiceUnitTest {

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

  @InjectMocks AuthServiceImpl authService;

  @Test
  @DisplayName("회원가입_이메일 인증 완료 시_정상 가입된다")
  void signUp_success() {
    // given
    SignUpRequest request =
        new SignUpRequest(
            "test@skuiv.ac.kr", "password", "홍길동", "소프트웨어학과", "20231234", "010-1234-5678");

    String redisKey = "VerifiedEmail:" + request.getEmail();

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
  @DisplayName("회원가입_이메일 인증 실패 시_가입 실패한다")
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

    String redisKey = "VerifiedEmail:" + request.getEmail();

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
  @DisplayName("로그인_성공 시_토큰을 반환한다")
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
  @DisplayName("로그인_실패 시_400응답 발생한다")
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
  @DisplayName("비밀번호 찾기_성공 시_새 비밀번호 발급 받는다")
  void reissuePassword_success() {
    // given
    User user =
        User.builder()
            .email("test@skuniv.ac.kr")
            .password("password")
            .name("홍길동")
            .department("소프트웨어학과")
            .studentNumber("2021123456")
            .phoneNumber("010-1234-5678")
            .build();
    String verificationCode = "123456";
    EmailVerificationConfirmReqeust request =
        EmailVerificationConfirmReqeust.builder()
            .email("test@skuniv.ac.kr")
            .code(verificationCode)
            .build();
    String redisKey = "EmailVerification:" + request.getEmail();

    when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);
    when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    when(valueOperations.get(redisKey)).thenReturn(verificationCode);
    when(userRepository.findByEmail(eq(request.getEmail()))).thenReturn(Optional.ofNullable(user));

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
  @DisplayName("비밀번호 찾기_이메일 없을 시_404응답 발생한다")
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
  @DisplayName("비밀번호 찾기_인증 코드 검증 결과 없을 시_400응답 발생한다")
  void reissuePassword_emailInvalid_fail() {
    // given
    String verificationCode = "123456";
    EmailVerificationConfirmReqeust request =
        EmailVerificationConfirmReqeust.builder()
            .email("test@skuniv.ac.kr")
            .code(verificationCode)
            .build();
    String redisKey = "EmailVerification:" + request.getEmail();

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
  @DisplayName("비밀번호 찾기_인증 코드 검증 결과 불일치 시_400응답 발생한다")
  void reissuePassword_codeInvalid_fail() {
    // given
    String verificationCode = "123456";
    EmailVerificationConfirmReqeust request =
        EmailVerificationConfirmReqeust.builder()
            .email("test@skuniv.ac.kr")
            .code(verificationCode)
            .build();
    String redisKey = "EmailVerification:" + request.getEmail();

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
  @DisplayName("토큰 리프레시_토큰 검증 성공 시_토큰 응답 반환한다")
  void refreshToken_success() {
    // given
    String email = "test@skuniv.ac.kr";
    User user =
        User.builder()
            .email(email)
            .password("password")
            .name("홍길동")
            .department("소프트웨어학과")
            .studentNumber("2021123456")
            .phoneNumber("010-1234-5678")
            .build();
    String refreshToken = "refresh-token";
    when(jwtProvider.validateTokenType(refreshToken, TokenType.REFRESH_TOKEN)).thenReturn(true);
    when(jwtProvider.validateRefreshToken(refreshToken)).thenReturn(true);

    UserDetails userDetails = new CustomUserDetails(user);
    when(jwtProvider.getEmailFromToken(refreshToken)).thenReturn(email);
    when(userDetailsService.loadUserByUsername(email)).thenReturn(userDetails);

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
    verify(userDetailsService).loadUserByUsername(email);
    verify(jwtProvider).deleteRefreshToken(refreshToken);
    verify(jwtProvider)
        .generateTokenResponse(
            argThat(auth -> auth instanceof UsernamePasswordAuthenticationToken));

    verifyNoMoreInteractions(jwtProvider, userDetailsService);
  }

  @Test
  @DisplayName("토큰 리프레시_토큰 타입 불일치 시_401응답 발생한다")
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
  @DisplayName("토큰 리프레시_토큰이 유효하지 않을 시_401응답 발생한다")
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
}
