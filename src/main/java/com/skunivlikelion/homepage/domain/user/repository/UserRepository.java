/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.skunivlikelion.homepage.domain.user.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findFirstByEmailOrStudentNumberOrPhoneNumber(
      String email, String studentNumber, String phoneNumber);

  User findByEmail(String email);

  boolean existsByEmail(String email);
}
