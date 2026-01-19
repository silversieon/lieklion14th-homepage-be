/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Track {
  COMMON("COMMON"),
  PO("PO"),
  FRONTEND("FRONTEND"),
  BACKEND("BACKEND"),
  PM("PM"),
  DESIGN("DESIGN"),
  PMDESIGN("PM&DESIGN");

  private final String name;
}
