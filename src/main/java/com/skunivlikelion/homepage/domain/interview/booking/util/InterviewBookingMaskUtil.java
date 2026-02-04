/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.interview.booking.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public final class InterviewBookingMaskUtil {

  private InterviewBookingMaskUtil() {}

  public static String sha256Hex(String input) {
    try {
      MessageDigest md = MessageDigest.getInstance("SHA-256");
      byte[] hash = md.digest(input.getBytes(StandardCharsets.UTF_8));
      StringBuilder sb = new StringBuilder();
      for (byte b : hash) {
        sb.append(String.format("%02x", b));
      }
      return sb.toString();
    } catch (Exception e) {
      throw new IllegalStateException("SHA-256 hashing failed", e);
    }
  }

  public static String maskEmail(String email) {
    if (email == null || !email.contains("@")) {
      return "****";
    }

    String[] parts = email.split("@", 2);
    String local = parts[0];
    String domain = parts[1];

    if (local.length() <= 2) {
      return local.charAt(0) + "*@" + domain;
    }

    String head = local.substring(0, 2);
    String tail = local.substring(Math.max(2, local.length() - 2));
    return head + "****" + tail + "@" + domain;
  }

  public static String maskName(String name) {
    if (name == null || name.isBlank()) {
      return "*";
    }

    String n = name.trim();
    int len = n.length();

    if (len == 1) {
      return "*";
    }
    if (len == 2) {
      return n.charAt(0) + "*";
    }

    return n.charAt(0) + "*".repeat(len - 2) + n.charAt(len - 1);
  }

  public static String maskStudentNumber(String studentNumber) {
    if (studentNumber == null || studentNumber.isBlank()) {
      return "****";
    }

    String s = studentNumber.trim();
    if (s.length() <= 4) {
      return "*".repeat(s.length());
    }

    String year = s.substring(0, 4);
    return year + "*".repeat(s.length() - 4);
  }
}
