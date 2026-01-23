/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.user.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.skunivlikelion.homepage.domain.common.enums.Track;
import com.skunivlikelion.homepage.domain.user.entity.ClubMember;
import com.skunivlikelion.homepage.domain.user.enums.Position;

public interface ClubMemberRepository extends JpaRepository<ClubMember, Long> {
  @EntityGraph(attributePaths = "user")
  List<ClubMember> findAllBySemesterAndPositionInAndTrackIn(
      Long semester, List<Position> positions, List<Track> tracks);

  @EntityGraph(attributePaths = "user")
  @Query(
      """
    SELECT cm
    FROM ClubMember cm
      WHERE (:position IS NULL OR cm.position = :position)
      AND (:track IS NULL OR cm.track = :track)
      AND (
        :keyword IS NULL
        OR cm.user.name LIKE CONCAT('%', :keyword, '%')
        OR cm.user.department LIKE CONCAT('%', :keyword, '%')
      )
    """)
  List<ClubMember> searchClubMembersByPositionAndTrackAndKeywordIn(
      @Param("position") Position position,
      @Param("track") Track track,
      @Param("keyword") String keyword);

  @EntityGraph(attributePaths = "user")
  @Query(
      """
SELECT cm
FROM ClubMember cm
WHERE cm.semester = :semesterId
  AND (:position IS NULL OR cm.position = :position)
  AND (:track IS NULL OR cm.track = :track)
  AND (
    :keyword IS NULL
    OR cm.user.name LIKE CONCAT('%', :keyword, '%')
    OR cm.user.department LIKE CONCAT('%', :keyword, '%')
  )
""")
  List<ClubMember> searchClubMembersBySemesterAndPositionAndTrackAndKeywordIn(
      @Param("semesterId") Long semesterId,
      @Param("position") Position position,
      @Param("track") Track track,
      @Param("keyword") String keyword);

  @Query("""
SELECT COUNT(DISTINCT cm.user.id)
FROM ClubMember cm
WHERE cm.user.id IN :userIds
""")
  long countDistinctUserIdsInClubMember(@Param("userIds") List<Long> userIds);

  void deleteAllByUser_IdIn(List<Long> userIds);

  long countByIdIn(List<Long> Ids);

  boolean existsByUser_IdIn(Collection<Long> userIds);

  boolean existsByUser_Id(Long userId);
}
