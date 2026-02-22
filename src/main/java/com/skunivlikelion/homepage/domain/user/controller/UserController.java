/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.user.controller;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import com.skunivlikelion.homepage.domain.common.enums.Track;
import com.skunivlikelion.homepage.domain.user.dto.request.*;
import com.skunivlikelion.homepage.domain.user.dto.response.*;
import com.skunivlikelion.homepage.domain.user.enums.Position;
import com.skunivlikelion.homepage.global.common.BaseResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 멋쟁이사자처럼 홈페이지 사용자 관련 Controller interface 입니다.
 *
 * @since 2026.01.22
 * @see com.skunivlikelion.homepage.domain.user.entity.User
 * @see com.skunivlikelion.homepage.domain.user.entity.ClubMember
 * @see com.skunivlikelion.homepage.domain.user.service.UserService
 * @author Keum Si Eon
 * @version latest: 1
 */
@RequestMapping("/api")
@Tag(name = "User", description = "사용자 관리 API")
public interface UserController {

  @Operation(
      summary = "[ 사용자 | 토큰 O | 사용자 권한 조회 ]",
      description =
          """
            **Returns** \n
            userRole: 사용자 권한 (USER, DEVELOPER, ADMIN)    \n
            """)
  @GetMapping("/v1/users/role")
  ResponseEntity<BaseResponse<UserRoleResponse>> getMyRole();

  @Operation(
      summary = "[ 사용자 | 토큰 O | 마이페이지 정보 조회 ]",
      description =
          """
            **Returns** \n
            name: 사용자 이름    \n
            email: 사용자 이메일  \n
            profileImageUrl: 프로필 이미지 URL \n
            documentActive: 지원서 버튼 활성화 여부 \n
            documentSubmitted: 지원서 제출 여부  \n
            interviewScheduleChangeable: 면접 일정 변경 가능 여부 \n
            interviewScheduleConfirmed: 면접 일정 확인 가능 여부  \n
            finalResultConfirmation: 최종 결과 확인 가능 여부 \n
            """)
  @GetMapping("/v1/users/me")
  ResponseEntity<BaseResponse<MyPageResponse>> getMyPage();

  @Operation(
      summary = "[ 사용자 | 토큰 X | 기수별 구성원 조회 ]",
      description =
          """
            **Returns** \n
            position: 역할군    \n
            track: 트랙 \n
            clubMembers: 해당 역할군, 트랙별 구성원 리스트   \n
            - name:   구성원 이름   \n
            - profieImageUrl: 프로필 이미지 URL \n
            - department: 구성원 학과  \n
            - shortStudentNumber: 구성원 학번(앞부분) \n

            hasNext: 다음 요청 가능 여부  \n
            nextPositionCursor: 다음 요청할 역할 위치  \n
            nextTrackCursor: 다음 요청할 트랙 위치 \n
            """)
  @GetMapping("/v1/users/club-members/{semester}")
  ResponseEntity<BaseResponse<ClubMemberCursorResponse<List<ClubMemberPageResponse>>>>
      getClubMemberList(
          @PathVariable @Positive Long semester,
          @RequestParam(value = "next-position-cursor") Position nextPositionCursor,
          @RequestParam(value = "next-track-cursor", required = false) Track nextTrackCursor);

  @Operation(
      summary = "[ 관리자 | 토큰 O | 사용자 관리 - 게스트 관리창 목록 조회 ]",
      description =
          """
            **Parameters**  \n
            is-guest: 게스트 여부  \n
            keyword: 검색어 (이름, 학과 가능)  \n

            **Returns** \n
            guest: 게스트 여부    \n
            userInformationList: 사용자 정보 목록   \n
            - userId: 사용자 식별자
            - name:   사용자 이름   \n
            - department: 사용자 학과  \n
            - studentNumber: 사용자 학번 \n
            """)
  @GetMapping("/v1/admin/users")
  ResponseEntity<BaseResponse<UserManagementResponse>> getUserManagement(
      @RequestParam(value = "is-guest") Boolean isGuest,
      @RequestParam(value = "last-user-id", required = false) Long lastUserId,
      @RequestParam(value = "size") Integer size,
      @RequestParam(required = false) String keyword);

  @Operation(
      summary = "[ 관리자 | 토큰 O | 사용자 관리 - 구성원 관리창 목록 조회 ]",
      description =
          """
            **Parameters**  \n
            position: 역할 필터  \n
            track: 트랙 필터  \n
            keyword: 검색어 (이름, 학과 가능)  \n

            **Returns** \n
            userId: 사용자 식별자 \n
            clubMemberId: 구성원 식별자 \n
            semester: 구성원 기수   \n
            position: 구성원 역할  \n
            name: 구성원 이름  \n
            track: 구성원 트랙 \n
            department: 사용자 학과  \n
            studentNumber: 사용자 학번 \n
            """)
  @GetMapping("/v1/admin/club-members")
  ResponseEntity<BaseResponse<List<ClubMemberInformationResponse>>> getClubMemberManagementList(
      @RequestParam(required = false) @Positive Long semester,
      @RequestParam(required = false) Position position,
      @RequestParam(required = false) Track track,
      @RequestParam(required = false) String keyword);

  @Operation(
      summary = "[ 사용자 | 토큰 O | 사용자의 지원 상태 조회 ]",
      description =
          """
            **Parameters**  \n
            documentSubmitted: 지원서 제출 여부  \n
            interviewScheduleConfirmed: 면접 일정 제출 여부  \n
          """)
  @GetMapping("/v1/users/application-status")
  ResponseEntity<BaseResponse<UserApplicationStatusResponse>> getUserApplicationStatus();

  @Operation(
      summary = "[ 관리자 | 토큰 O | 게스트에서 구성원 일괄 추가 (게스트 -> 구성원) ]",
      description =
          """
            **Parameters**  \n
            userIds: 사용자 식별자 리스트  \n

            **Returns** \n
            - userId: 사용자 식별자 \n
            - name:   사용자 이름   \n
            - department: 사용자 학과  \n
            - studentNumber: 사용자 학번 \n
            """)
  @PostMapping("/v1/admin/club-members/bulk")
  ResponseEntity<BaseResponse<List<UserInformationResponse>>> addClubMembersFromGuests(
      @Valid @RequestBody ChangeMembershipRequest request);

  @Operation(
      summary = "[ 관리자 | 토큰 O | 특정 구성원의 다른 이력 추가 ]",
      description =
          """
            **Parameters**  \n
            position: 구성원 역할  \n
            track: 구성원 트랙 \n

            **Returns** \n
            userId: 사용자 식별자
            clubMemberId: 구성원 식별자 \n
            semester: 구성원 기수   \n
            position: 구성원 역할  \n
            name: 구성원 이름  \n
            track: 구성원 트랙 \n
            """)
  @PostMapping("/v1/admin/users/{user-id}/club-members")
  ResponseEntity<BaseResponse<ClubMemberInformationResponse>> addClubMemberRecord(
      @PathVariable(value = "user-id") Long userId,
      @RequestParam @Positive Long semester,
      @RequestParam Position position,
      @RequestParam Track track);

  @Operation(
      summary = "[ 개발자, 관리자 | 토큰 O | 임의의 사용자 회원가입 (데이터 시딩, QA용) ]",
      description =
          """
                    **Parameters**  \n
                    email: 사용자 이메일  \n
                    password: 사용자 패스워드(정규식 무시 가능)  \n
                    name: 사용자 이름  \n
                    department: 사용자 학과  \n
                    phoneNumber: 사용자 휴대전화 번호  \n

                    **Returns** \n
                    userId: 사용자 식별자
                    password: 사용자 패스워드(정규식 무시 가능)  \n
                    name: 사용자 이름  \n
                    department: 사용자 학과  \n
                    phoneNumber: 사용자 휴대전화 번호  \n
                    """)
  @PostMapping("/v1/dev/users")
  ResponseEntity<BaseResponse<CreateUserResponse>> createUser(
      @Valid @RequestBody CreateUserRequest request);

  @Operation(
      summary = "[ 개발자, 관리자 | 토큰 O | 임의의 사용자 정보 변경 ]",
      description =
          """
          **Parameters**  \n
          name: 사용자 이름  \n
          department: 사용자 학과  \n
          studentNumber: 사용자 학번 \n
          phoneNumber: 사용자 휴대전화 번호  \n

          **Returns** \n
          userId: 사용자 식별자 \n
          name: 사용자 이름  \n
          department: 사용자 학과  \n
          studentNumber: 사용자 학번 \n
          phoneNumber: 사용자 휴대전화 번호  \n
          """)
  @PutMapping("/v1/dev/users/{user-id}")
  ResponseEntity<BaseResponse<UserInformationResponse>> updateUserInformation(
      @PathVariable(value = "user-id") Long userId,
      @RequestBody UpdateUserInformationRequest request);

  @Operation(
      summary = "[ 사용자 | 토큰 O | 마이페이지 이미지 변경 ]",
      description =
          """
            **Returns** \n
            name: 사용자 이름    \n
            email: 사용자 이메일  \n
            profileImageUrl: 프로필 이미지 URL \n
            documentActive: 지원서 버튼 활성화 여부 \n
            documentSubmitted: 지원서 제출 여부  \n
            interviewScheduleChangeable: 면접 일정 변경 가능 여부 \n
            interviewScheduleConfirmed: 면접 일정 확인 가능 여부  \n
            finalResultConfirmation: 최종 결과 확인 가능 여부 \n
            """)
  @PatchMapping(value = "/v1/users/me/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  ResponseEntity<BaseResponse<MyPageResponse>> updateProfileImage(
      @Parameter(description = "프로필 이미지") @RequestPart(value = "profile-image")
          MultipartFile profileImage);

  @Operation(
      summary = "[ 사용자 | 토큰 O | 비밀번호 변경 ]",
      description =
          """
            **Parameters**  \n
            currentPassword: 현재 비밀번호  \n
            newPassword: 새 비밀번호 \n
            newPasswordConfirmation: 새 비밀번호 확인 \n
            """)
  @PatchMapping(value = "/v1/users/me/password")
  ResponseEntity<BaseResponse<Void>> updatePassword(
      @Valid @RequestBody UpdatePasswordRequest request);

  @Operation(
      summary = "[ 관리자 | 토큰 O | 특정 구성원 이력 변경 ]",
      description =
          """
                    **Parameters**  \n
                    clubMemberId: 구성원 식별자 \n
                    semester: 구성원 기수   \n
                    position: 구성원 역할  \n
                    track: 구성원 트랙 \n
                    """)
  @PatchMapping(value = "/v1/admin/club-members/{club-member-id}")
  ResponseEntity<BaseResponse<ClubMemberInformationResponse>> updateClubMember(
      @PathVariable(value = "club-member-id") Long clubMemberId,
      @RequestParam @Positive Long semester,
      @RequestParam Position position,
      @RequestParam Track track);

  @Operation(
      summary = "[ 관리자 | 토큰 O | 여러 사용자의 구성원 이력 일괄 삭제 (구성원 -> 게스트) ]",
      description =
          """
            **Parameters**  \n
            userIds: 사용자 식별자 리스트  \n
            """)
  @DeleteMapping(value = "/v1/admin/users/club-members/bulk")
  ResponseEntity<BaseResponse<Void>> addGuestsFromClubMembers(
      @Valid @RequestBody ChangeMembershipRequest request);

  @Operation(
      summary = "[ 관리자 | 토큰 O | 여러 사용자 일괄 삭제 ]",
      description =
          """
            **Parameters**  \n
            userIds: 사용자 식별자 리스트  \n
            """)
  @DeleteMapping(value = "/v1/admin/users/bulk")
  ResponseEntity<BaseResponse<Void>> deleteUsers(@Valid @RequestBody DeleteUsersRequest request);

  @Operation(
      summary = "[ 관리자 | 토큰 O | 여러 구성원의 이력 일괄 삭제 ]",
      description =
          """
            **Parameters**  \n
            clubMemberIds: 구성원 식별자 리스트  \n
            """)
  @DeleteMapping(value = "/v1/admin/club-members/bulk")
  ResponseEntity<BaseResponse<Void>> deleteClubMembers(
      @Valid @RequestBody DeleteClubMembersRequest request);
}
