/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.global.config.property;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.mail.autoconfigure.MailProperties;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@ConfigurationProperties("spring.mail")
public class SpringMailProperties extends MailProperties {}
