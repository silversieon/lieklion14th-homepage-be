/* 
 * Copyright (c) SKU LIKELION 
 */
package com.likelionskuniv.website.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.likelionskuniv.website.domain.user.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {}
