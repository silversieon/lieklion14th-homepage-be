/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.application.record.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import com.skunivlikelion.homepage.domain.application.question.entity.ApplicationQuestion;
import com.skunivlikelion.homepage.global.common.BaseTimeEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@Table(
    uniqueConstraints = {@UniqueConstraint(columnNames = {"application_record_id", "question_id"})})
public class ApplicationAnswer extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "question_id", nullable = false)
  private ApplicationQuestion question;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "application_record_id", nullable = false)
  private ApplicationRecord applicationRecord;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String content;

  public void updateContent(String content) {
    this.content = content;
  }
}
