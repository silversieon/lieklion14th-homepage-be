/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.application.result.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MyDocumentResultResponse {

  private final boolean isDocumentPassed;
}
