/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.global.common;

import org.jspecify.annotations.Nullable;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import com.skunivlikelion.homepage.global.annotation.ApiBaseResponse;

/**
 * 공통 응답 처리를 편리하게 해주는 RestControllerAdvice 클래스입니다. <br>
 * <br>
 * 동작 방식: <br>
 * 1. ApiBaseResponse 어노테이션 선언 여부를 {@code supports} 메서드에서 확인합니다. <br>
 * 2. 어노테이션이 선언된 RestController 메서드만 {@code beforeBodyWrite} 메서드 로직을 적용하여 반환합니다. <br>
 * <br>
 *
 * @see ApiBaseResponse
 * @since 2026.03.06
 * @author Keum Si Eon
 */
@RestControllerAdvice
public class BaseResponseAdvice implements ResponseBodyAdvice<Object> {

  @Override
  public boolean supports(
      MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
    return returnType.hasMethodAnnotation(ApiBaseResponse.class)
        || returnType.getContainingClass().isAnnotationPresent(ApiBaseResponse.class);
  }

  @Override
  public @Nullable Object beforeBodyWrite(
      @Nullable Object body,
      MethodParameter returnType,
      MediaType selectedContentType,
      Class<? extends HttpMessageConverter<?>> selectedConverterType,
      ServerHttpRequest request,
      ServerHttpResponse response) {
    if (body instanceof BaseResponse) return body;

    ApiBaseResponse annotation = returnType.getMethodAnnotation(ApiBaseResponse.class);
    if (annotation == null) {
      annotation = returnType.getContainingClass().getAnnotation(ApiBaseResponse.class);
    }

    int code = annotation.code();
    String message = annotation.message();

    response.setStatusCode(HttpStatusCode.valueOf(code));
    return BaseResponse.success(code, message, body);
  }
}
