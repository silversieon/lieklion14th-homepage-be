/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.user.mapper;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.skunivlikelion.homepage.domain.common.enums.Track;
import com.skunivlikelion.homepage.domain.semester.entity.Semester;
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

  public List<ClubMemberPageResponse> toClubMemberPageResponses(
      List<Position> positions, List<Track> tracks, List<ClubMember> clubMemberList) {
    Map<Position, List<ClubMember>> byPosition =
        clubMemberList.stream().collect(Collectors.groupingBy(ClubMember::getPosition));
    List<ClubMemberPageResponse> result = new ArrayList<>();

    for (Position position : positions) {
      List<ClubMember> membersOfPosition = byPosition.getOrDefault(position, List.of());

      List<Track> tracksForThisPosition = resolveTracks(position, tracks, membersOfPosition);
      result.add(toClubMemberPageResponse(position, tracksForThisPosition, membersOfPosition));
    }

    return result;
  }

  private List<Track> resolveTracks(
      Position position, List<Track> tracks, List<ClubMember> membersOfPosition) {
    if (position == Position.LEAD || position == Position.COLEAD) {
      return membersOfPosition.stream()
          .map(ClubMember::getTrack)
          .filter(Objects::nonNull)
          .distinct()
          .toList();
    }

    return tracks == null ? List.of(Track.BACKEND) : tracks;
  }

  public ClubMemberPageResponse toClubMemberPageResponse(
      Position position, List<Track> trackList, List<ClubMember> clubMemberList) {

    Map<Track, List<ClubMemberPageResponse.ClubMemberSummary>> grouped = new EnumMap<>(Track.class);
    for (ClubMember cm : clubMemberList) {
      if (cm == null || cm.getTrack() == null) continue;
      grouped.computeIfAbsent(cm.getTrack(), k -> new ArrayList<>()).add(toClubMemberSummary(cm));
    }

    List<ClubMemberPageResponse.ClubMembersOfTracks> clubMembersOfTracks =
        (trackList == null ? List.<Track>of() : trackList)
            .stream()
                .map(
                    track ->
                        ClubMemberPageResponse.ClubMembersOfTracks.builder()
                            .track(track)
                            .clubMemberSummaryList(grouped.getOrDefault(track, List.of()))
                            .build())
                .toList();

    return ClubMemberPageResponse.builder()
        .position(position)
        .clubMembersOfTracks(clubMembersOfTracks)
        .build();
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
