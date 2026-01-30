/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.interview.booking.mapper;

import org.springframework.stereotype.Component;

import com.skunivlikelion.homepage.domain.interview.booking.dto.response.InterviewBookingResponse;
import com.skunivlikelion.homepage.domain.interview.booking.entity.InterviewBooking;

@Component
public class InterviewBookingMapper {

  public InterviewBookingResponse toResponse(InterviewBooking booking) {
    return InterviewBookingResponse.builder()
        .bookingId(booking.getId())
        .scheduleId(booking.getInterviewSchedule().getId())
        .build();
  }
}
