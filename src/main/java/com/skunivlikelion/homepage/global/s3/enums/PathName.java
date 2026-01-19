/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.global.s3.enums;

import io.swagger.v3.oas.annotations.media.Schema;

public enum PathName {
  @Schema(description = "프로필 사진")
  PROFILE,
  @Schema(description = "프로젝트 사진")
  PROJECT;
}
