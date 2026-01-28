/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.user.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.skunivlikelion.homepage.domain.user.dto.request.CreateUserRequest;
import com.skunivlikelion.homepage.domain.user.dto.response.*;
import com.skunivlikelion.homepage.domain.user.entity.ClubMember;
import com.skunivlikelion.homepage.domain.user.entity.User;
import com.skunivlikelion.homepage.global.page.response.InfiniteResponse;

@Component
public class UserMapper {

  public User toUser(CreateUserRequest request, String encodedPassword) {
    return User.builder()
        .email(request.getEmail())
        .password(encodedPassword)
        .name(request.getName())
        .department(request.getDepartment())
        .studentNumber(request.getStudentNumber())
        .phoneNumber(request.getPhoneNumber())
        .build();
  }

  public UserRoleResponse toUserRoleResponse(User user) {
    return UserRoleResponse.builder().userRole(user.getUserRole()).build();
  }

  public MyPageResponse toMyPageResponse(User user) {
    return MyPageResponse.builder()
        .name(user.getName())
        .email(user.getEmail())
        .profileImageUrl(user.getProfileImageUrl())
        .submitted(true) // application_record 나오면 수정 필요
        .build();
  }

  public UserManagementResponse toUserManagementResponse(
      boolean isGuest, InfiniteResponse<UserInformationResponse> response) {
    return UserManagementResponse.builder().guest(isGuest).userInformationList(response).build();
  }

  public UserInformationResponse toUserInformationResponse(User user) {
    return UserInformationResponse.builder()
        .userId(user.getId())
        .name(user.getName())
        .department(user.getDepartment())
        .studentNumber(user.getStudentNumber())
        .build();
  }

  public List<ClubMemberInformationResponse> toClubMemberInformationList(
      List<ClubMember> clubMembers) {
    return clubMembers.stream().map(this::toClubMemberInformationResponse).toList();
  }

  public ClubMemberInformationResponse toClubMemberInformationResponse(ClubMember clubMember) {
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

  public List<UserInformationResponse> toUserInformationList(List<User> users) {
    return users.stream().map(this::toUserInformationResponse).toList();
  }

  public CreateUserResponse toCreateUserResponse(User user) {
    return CreateUserResponse.builder()
        .userId(user.getId())
        .email(user.getEmail())
        .name(user.getName())
        .department(user.getDepartment())
        .studentNumber(user.getStudentNumber())
        .phoneNumber(user.getPhoneNumber())
        .build();
  }
}
