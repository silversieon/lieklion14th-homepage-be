/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.user.repository;

import com.skunivlikelion.homepage.domain.common.enums.Track;
import com.skunivlikelion.homepage.domain.user.enums.Position;

public interface ClubMemberCountRow {
  Position getPosition();

  Track getTrack();

  long getCount();
}
