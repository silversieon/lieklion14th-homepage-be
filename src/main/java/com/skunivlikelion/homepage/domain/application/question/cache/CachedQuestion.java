/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.application.question.cache;

import java.io.Serial;
import java.io.Serializable;

import com.skunivlikelion.homepage.domain.common.enums.Track;

public record CachedQuestion(Long questionId, Track track, Integer orderNumber, String content)
    implements Serializable {
  @Serial private static final long serialVersionUID = 1L;
}
