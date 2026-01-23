/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.user.controller;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.skunivlikelion.homepage.domain.common.enums.Track;
import com.skunivlikelion.homepage.domain.user.dto.request.*;
import com.skunivlikelion.homepage.domain.user.dto.response.*;
import com.skunivlikelion.homepage.domain.user.enums.Position;
import com.skunivlikelion.homepage.domain.user.service.UserService;

import backend.boilerplate.response.BaseResponse;
import lombok.RequiredArgsConstructor;

@Validated
@RestController
@RequiredArgsConstructor
public class UserControllerImpl implements UserController {

  private final UserService userService;

  @Override
  public ResponseEntity<BaseResponse<UserRoleResponse>> getMyRole() {
    return ResponseEntity.status(200)
        .body(BaseResponse.success(200, "사용자 역할 조회에 성공했습니다.", userService.getCurrentUserRole()));
  }

  @Override
  public ResponseEntity<BaseResponse<MyPageResponse>> getMyPage() {
    return ResponseEntity.status(200)
        .body(
            BaseResponse.success(
                200, "사용자 마이페이지 정보 조회에 성공했습니다.", userService.getCurrentUserPage()));
  }

  @Override
  public ResponseEntity<BaseResponse<MyInformationResponse>> getMyInformation() {
    return ResponseEntity.status(200)
        .body(
            BaseResponse.success(
                200, "사용자 정보 상세 조회에 성공했습니다.", userService.getCurrentUserInformation()));
  }

  @Override
  public ResponseEntity<BaseResponse<List<ClubMemberPageResponse>>> getClubMemberList(
      @PathVariable @Positive Long semester) {
    return ResponseEntity.status(200)
        .body(
            BaseResponse.success(
                200, "구성원 페이지 조회에 성공했습니다.", userService.getClubMemberList(semester)));
  }

  @Override
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<BaseResponse<UserManagementResponse>> getUserManagement(
      @RequestParam(value = "is-guest") Boolean isGuest,
      @RequestParam(required = false) String keyword) {
    return ResponseEntity.status(200)
        .body(
            BaseResponse.success(
                200,
                "사용자 관리 - 게스트 관리창 목록 조회에 성공했습니다.",
                userService.getUserManagement(isGuest, keyword)));
  }

  @Override
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<BaseResponse<List<ClubMemberInformationResponse>>>
      getClubMemberManagementList(
          @RequestParam(required = false) @Positive Long semester,
          @RequestParam(required = false) Position position,
          @RequestParam(required = false) Track track,
          @RequestParam(required = false) String keyword) {
    return ResponseEntity.status(200)
        .body(
            BaseResponse.success(
                200,
                "사용자 관리 - 구성원 관리창 목록 조회에 성공했습니다.",
                userService.getClubMemberManagementList(semester, position, track, keyword)));
  }

  @Override
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<BaseResponse<List<UserInformationResponse>>> addClubMembersFromGuests(
      @RequestBody ChangeMembershipRequest request) {
    return ResponseEntity.status(201)
        .body(
            BaseResponse.success(
                201, "게스트 -> 구성원 일괄 추가에 성공했습니다.", userService.addClubMembers(request)));
  }

  @Override
  @PreAuthorize("hasAnyRole('DEVELOPER', 'ADMIN')")
  public ResponseEntity<BaseResponse<CreateUserResponse>> createUser(
      @Valid @RequestBody CreateUserRequest request) {
    return ResponseEntity.status(201)
        .body(BaseResponse.success(201, "사용자 생성에 성공했습니다.", userService.createUser(request)));
  }

  @Override
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<BaseResponse<ClubMemberInformationResponse>> addClubMemberRecord(
      @PathVariable(value = "user-id") Long userId,
      @RequestParam @Positive Long semester,
      @RequestParam Position position,
      @RequestParam Track track) {
    return ResponseEntity.status(201)
        .body(
            BaseResponse.success(
                201,
                "구성원 이력 추가에 성공했습니다.",
                userService.addClubMemberRecord(userId, semester, position, track)));
  }

  @Override
  public ResponseEntity<BaseResponse<MyPageResponse>> updateProfileImage(
      @RequestPart(value = "profile-image") MultipartFile profileImage) {
    return ResponseEntity.status(200)
        .body(
            BaseResponse.success(
                200, "프로필 이미지 변경에 성공했습니다.", userService.updateProfileImage(profileImage)));
  }

  @Override
  public ResponseEntity<BaseResponse<Void>> updatePassword(
      @Valid @RequestBody UpdatePasswordRequest request) {
    userService.updatePassword(request);
    return ResponseEntity.status(200).body(BaseResponse.success(200, "비밀번호 변경에 성공했습니다.", null));
  }

  @Override
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<BaseResponse<ClubMemberInformationResponse>> updateClubMember(
      @PathVariable(value = "club-member-id") Long clubMemberId,
      @RequestParam @Positive Long semester,
      @RequestParam Position position,
      @RequestParam Track track) {
    return ResponseEntity.status(200)
        .body(
            BaseResponse.success(
                200,
                "구성원 이력 변경에 성공했습니다.",
                userService.updateClubMemberRecord(clubMemberId, semester, position, track)));
  }

  @Override
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<BaseResponse<Void>> addGuestsFromClubMembers(
      @Valid @RequestBody ChangeMembershipRequest request) {
    userService.addGuestsFromClubMembers(request);
    return ResponseEntity.status(200)
        .body(BaseResponse.success(204, "구성원 -> 게스트 일괄 이동에 성공했습니다.", null));
  }

  @Override
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<BaseResponse<Void>> deleteUsers(
      @Valid @RequestBody DeleteUsersRequest request) {
    userService.deleteUsers(request);
    return ResponseEntity.status(200).body(BaseResponse.success(204, "사용자 일괄 삭제에 성공했습니다.", null));
  }

  @Override
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<BaseResponse<Void>> deleteClubMembers(
      @Valid @RequestBody DeleteClubMembersRequest request) {
    userService.deleteClubMembers(request);
    return ResponseEntity.status(200).body(BaseResponse.success(204, "구성원 일괄 삭제에 성공했습니다.", null));
  }
}
