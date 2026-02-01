/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.interview.booking.util;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Base64;

import com.skunivlikelion.homepage.domain.common.enums.Track;
import com.skunivlikelion.homepage.domain.interview.booking.exception.InterviewBookingErrorCode;
import com.skunivlikelion.homepage.global.exception.CustomException;

public final class InterviewBookingCursorUtil {

  private InterviewBookingCursorUtil() {}

  // 커서 구조
  public record Cursor(Track track, LocalDate date, LocalTime startTime, Long scheduleId) {}

  public static String encodeCursor(
      Track track, LocalDate date, LocalTime startTime, Long scheduleId) {

    String raw = track.name() + "|" + date + "|" + startTime + "|" + scheduleId;

    return Base64.getUrlEncoder()
        .withoutPadding()
        .encodeToString(raw.getBytes(StandardCharsets.UTF_8));
  }

  public static Cursor decodeCursor(String cursor) {
    try {
      String raw = new String(Base64.getUrlDecoder().decode(cursor), StandardCharsets.UTF_8);

      String[] parts = raw.split("\\|");
      if (parts.length != 4) {
        throw new IllegalArgumentException("cursor format invalid");
      }

      Track track = Track.valueOf(parts[0]);
      LocalDate date = LocalDate.parse(parts[1]);
      LocalTime startTime = LocalTime.parse(parts[2]);
      Long scheduleId = Long.parseLong(parts[3]);

      return new Cursor(track, date, startTime, scheduleId);

    } catch (Exception e) {
      throw new CustomException(InterviewBookingErrorCode.INVALID_CURSOR);
    }
  }
}
