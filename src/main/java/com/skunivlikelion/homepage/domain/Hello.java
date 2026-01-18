/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Hello {

  @GetMapping("/hello")
  public String hello() {
    return "Hello World";
  }
}
