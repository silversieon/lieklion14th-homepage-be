/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.global.exception.model;

import org.springframework.http.HttpStatus;

public interface BaseErrorCode {

  String getCode();

  String getMessage();

  HttpStatus getStatus();
}
