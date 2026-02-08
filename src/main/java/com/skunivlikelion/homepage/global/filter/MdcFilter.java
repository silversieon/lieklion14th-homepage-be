/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.global.filter;

import java.io.IOException;
import java.util.UUID;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class MdcFilter extends OncePerRequestFilter {

  private static final String TRACE_ID = "traceId";
  private static final String CLIENT_IP = "clientIp";

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    try {
      String traceId = UUID.randomUUID().toString().substring(0, 8);
      MDC.put(TRACE_ID, traceId);
      MDC.put(CLIENT_IP, request.getRemoteAddr());

      filterChain.doFilter(request, response);
    } finally {
      MDC.clear();
    }
  }
}
