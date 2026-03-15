/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.global.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 일반적인 JSON 응답을 반환하는 Controller 메서드에 사용하는 어노테이션입니다.<br>
 * 이 어노테이션이 적용된 메서드는 ResponseEntity 방식으로 응답을 반환하지 않습니다.<br>
 * 따라서 header 설정, 리다이렉트, 파일 다운로드 등 HTTP 응답을 세밀하게 제어해야 하는 Controller 메서드에는 사용을 권장하지 않습니다.<br>
 * <br>
 * 사용 방법: <br>
 * {@code @ApiBaseResponse(code = ..., message = "...")} <br>
 * <br>
 * - code: HttpStatus 코드(int) <br>
 * - message: 응답 성공 시에 반환할 문자열 <br>
 * 형태로 선언하면 응답이 BaseResponse 구조로 자동 래핑됩니다. <br>
 * <br>
 *
 * @see com.skunivlikelion.homepage.global.common.BaseResponseAdvice
 * @since 2026.03.06
 * @author Keum Si Eon
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiBaseResponse {

  int code() default 200;

  String message() default "요청이 처리되었습니다.";
}
