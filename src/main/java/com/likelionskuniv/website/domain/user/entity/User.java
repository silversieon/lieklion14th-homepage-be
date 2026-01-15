/* 
 * Copyright (c) SKU LIKELION 
 */
package com.likelionskuniv.website.domain.user.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.likelionskuniv.website.domain.user.enums.UserRole;

import backend.boilerplate.common.BaseTimeEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@Table(name = "users")
public class User extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String email;

  @JsonIgnore
  @Column(nullable = false)
  private String password;

  @Builder.Default
  @Enumerated(EnumType.STRING)
  private UserRole userRole = UserRole.GUEST;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private String department;

  @Column(nullable = false)
  private String studentNumber;

  @Column(nullable = false)
  private String phoneNumber;

  private String profileImageUrl;

  private String interview;

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private List<ClubMember> clubMembers = new ArrayList<>();
}
