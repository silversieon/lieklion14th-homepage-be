/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.global.cache.enums;

public enum CacheName {
  QUESTIONS("questions");

  private final String value;

  CacheName(String value) {
    this.value = value;
  }

  public String value() {
    return value;
  }
}
