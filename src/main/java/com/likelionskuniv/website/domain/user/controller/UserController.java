/* 
 * Copyright (c) SKU LIKELION 
 */
package com.likelionskuniv.website.domain.user.controller;

import org.springframework.web.bind.annotation.RequestMapping;

import io.swagger.v3.oas.annotations.tags.Tag;

@RequestMapping("/api/users")
@Tag(name = "사용자", description = "사용자 관리 API")
public interface UserController {}
