/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.common.enums;

import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Track {
  COMMON("COMMON", 7),
  PO("PO", 1),
  FRONTEND("FRONTEND", 5),
  BACKEND("BACKEND", 6),
  PM("PM", 3),
  DESIGN("DESIGN", 4),
  PMDESIGN("PM&DESIGN", 2);

  private final String name;
  private final int priority;

  public static List<Track> getCurrentSemesterTracks(Long semester) {
    if (semester == 14) {
      return List.of(PO, FRONTEND, BACKEND);
    } else if (semester == 13) {
      return List.of(PM, DESIGN, FRONTEND, BACKEND);
    } else if (semester == 12 || semester == 11) {
      return List.of(PMDESIGN, FRONTEND, BACKEND);
    } else {
      return List.of(PO, FRONTEND, BACKEND);
    }
  }
}
