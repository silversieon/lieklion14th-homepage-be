/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.application.question.cache;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.skunivlikelion.homepage.domain.common.enums.Track;

public final class QuestionsBundle {

  private final Map<Track, List<CachedQuestion>> byTrack;
  private final Map<Long, CachedQuestion> byId;
  private final List<CachedQuestion> all;

  private QuestionsBundle(
      Map<Track, List<CachedQuestion>> byTrack,
      Map<Long, CachedQuestion> byId,
      List<CachedQuestion> all) {
    this.byTrack = byTrack;
    this.byId = byId;
    this.all = all;
  }

  public static QuestionsBundle of(List<CachedQuestion> allQuestions) {
    List<CachedQuestion> safe = (allQuestions == null) ? List.of() : allQuestions;

    EnumMap<Track, ArrayList<CachedQuestion>> trackMap = new EnumMap<>(Track.class);

    for (Track t : Track.values()) {
      trackMap.put(t, new ArrayList<>());
    }

    HashMap<Long, CachedQuestion> idMap = new HashMap<>(safe.size());

    for (CachedQuestion q : safe) {
      trackMap.get(q.track()).add(q);
      idMap.put(q.questionId(), q);
    }

    EnumMap<Track, List<CachedQuestion>> finalTrackMap = new EnumMap<>(Track.class);

    for (Map.Entry<Track, ArrayList<CachedQuestion>> e : trackMap.entrySet()) {
      finalTrackMap.put(e.getKey(), List.copyOf(e.getValue()));
    }

    return new QuestionsBundle(Map.copyOf(finalTrackMap), Map.copyOf(idMap), List.copyOf(safe));
  }

  public List<CachedQuestion> getQuestions(Track track) {
    if (track == null) return List.of();
    return byTrack.getOrDefault(track, List.of());
  }

  public CachedQuestion getById(Long questionId) {
    return byId.get(questionId);
  }

  public List<CachedQuestion> getAll() {
    return all;
  }
}
