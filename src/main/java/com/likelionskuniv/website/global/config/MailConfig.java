/* 
 * Copyright (c) SKU LIKELION 
 */
package com.likelionskuniv.website.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import com.likelionskuniv.website.global.config.property.SpringMailProperties;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class MailConfig {

  private final SpringMailProperties springMailProperties;

  @Bean
  public JavaMailSender javaMailSender() {
    JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
    mailSender.setHost(springMailProperties.getHost());
    mailSender.setPort(springMailProperties.getPort());
    mailSender.setUsername(springMailProperties.getUsername());
    mailSender.setPassword(springMailProperties.getPassword());
    mailSender.getJavaMailProperties().putAll(springMailProperties.getProperties());

    return mailSender;
  }
}
