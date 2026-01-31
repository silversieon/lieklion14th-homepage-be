/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.user.mapper;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.skunivlikelion.homepage.domain.common.enums.Track;
import com.skunivlikelion.homepage.domain.semester.entity.Semester;
import com.skunivlikelion.homepage.domain.user.dto.response.ClubMemberCursorResponse;
import com.skunivlikelion.homepage.domain.user.dto.response.ClubMemberInformationResponse;
import com.skunivlikelion.homepage.domain.user.dto.response.ClubMemberPageResponse;
import com.skunivlikelion.homepage.domain.user.entity.ClubMember;
import com.skunivlikelion.homepage.domain.user.entity.User;
import com.skunivlikelion.homepage.domain.user.enums.Position;

@Component
public class ClubMemberMapper {

  public List<ClubMember> toTempClubMembers(List<User> users, Semester semester) {
    return users.stream().map(user -> toTempClubMember(user, semester)).toList();
  }

  public ClubMember toClubMember(User user, Semester semester, Position position, Track track) {
    return ClubMember.builder()
        .semester(semester)
        .track(track)
        .position(position)
        .user(user)
        .build();
  }

  public ClubMember toTempClubMember(User user, Semester semester) {
    return ClubMember.builder()
        .semester(semester)
        .track(Track.COMMON)
        .position(Position.BABYLION)
        .user(user)
        .build();
  }

  public <T> ClubMemberCursorResponse<T> toClubMemberCursorResponse(
      T clubMemberPageResponses,
      Position nextPositionCursor,
      Track nextTrackCursor,
      boolean hasNext) {
    return ClubMemberCursorResponse.<T>builder()
        .content(clubMemberPageResponses)
        .nextPositionCursor(nextPositionCursor)
        .nextTrackCursor(nextTrackCursor)
        .hasNext(hasNext)
        .build();
  }

  public List<ClubMemberPageResponse> toClubMemberPageResponses(
      List<ClubMember> clubMembers, List<Position> positions, List<Track> tracks) {

    Map<Position, Map<Track, List<ClubMember>>> grouped =
        clubMembers.stream()
            .collect(
                Collectors.groupingBy(
                    ClubMember::getPosition, Collectors.groupingBy(ClubMember::getTrack)));

    List<ClubMemberPageResponse> result = new ArrayList<>();

    for (Position position : positions) {
      Map<Track, List<ClubMember>> byTrack = grouped.getOrDefault(position, Map.of());

      for (Track track : tracks) {
        List<ClubMember> members = byTrack.getOrDefault(track, List.of());

        if (members.isEmpty()) continue;

        result.add(
            ClubMemberPageResponse.builder()
                .position(position)
                .track(track)
                .clubMembers(members.stream().map(this::toClubMemberSummary).toList())
                .build());
      }
    }

    return result;
  }

  public ClubMemberPageResponse.ClubMemberSummary toClubMemberSummary(ClubMember clubMember) {
    String studentNumber = clubMember.getUser().getStudentNumber();

    return ClubMemberPageResponse.ClubMemberSummary.builder()
        .name(clubMember.getUser().getName())
        .department(clubMember.getUser().getDepartment())
        .shortStudentNumber(extractShortStudentNumber(studentNumber))
        .profileImageUrl(clubMember.getUser().getProfileImageUrl())
        .build();
  }

  public ClubMemberInformationResponse toClubMemberInformation(ClubMember clubMember) {
    return ClubMemberInformationResponse.builder()
        .userId(clubMember.getUser().getId())
        .clubMemberId(clubMember.getId())
        .semester(clubMember.getSemester().getSemester())
        .position(clubMember.getPosition())
        .name(clubMember.getUser().getName())
        .track(clubMember.getTrack())
        .department(clubMember.getUser().getDepartment())
        .studentNumber(clubMember.getUser().getStudentNumber())
        .build();
  }

  private String extractShortStudentNumber(String studentNumber) {
    if (studentNumber.length() == 2) {
      return studentNumber;
    }
    return studentNumber.substring(2, 4);
  }
}
