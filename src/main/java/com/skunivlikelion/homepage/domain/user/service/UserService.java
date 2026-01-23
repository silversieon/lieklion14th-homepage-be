/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.user.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.skunivlikelion.homepage.domain.common.enums.Track;
import com.skunivlikelion.homepage.domain.user.dto.request.*;
import com.skunivlikelion.homepage.domain.user.dto.response.*;
import com.skunivlikelion.homepage.domain.user.enums.Position;

/**
 * 멋쟁이사자처럼 홈페이지 사용자 관련 Service interface 입니다.
 *
 * @see com.skunivlikelion.homepage.domain.user.entity.User
 * @see com.skunivlikelion.homepage.domain.user.entity.ClubMember
 * @see com.skunivlikelion.homepage.domain.user.controller.UserController
 * @since 2026.01.22
 * @author Keum Si Eon
 */
public interface UserService {

  /**
   * [ 현재 사용자의 역할 조회 메서드 ]
   *
   * @return 역할 값을 담은 UserRoleResponse 객체
   */
  UserRoleResponse getCurrentUserRole();

  /**
   * [ 현재 사용자의 마이페이지 정보 조회 메서드 ]
   *
   * @return 마이페이지 기본 정보를 담은 MyPageResponse 객체
   */
  MyPageResponse getCurrentUserPage();

  /**
   * [ 현재 사용자의 정보 상세 조회 메서드 ] 지원서 작성, 조회 부분에서 사용
   *
   * @return 상세 정보를 담은 MyInformationResponse 객체
   */
  MyInformationResponse getCurrentUserInformation();

  /**
   * [ 기수별 구성원 조회 메서드 ]
   *
   * @param semester 구성원을 조회할 기수
   * @return 해당 기수의 구성원 전체 리스트
   * @implNote "아기사자" 목록은 해당 기수의 최종 결과 발표일이 지난 후에 반환됩니다 currentSemesterTracks 변수는 현재 기수의 트랙 종류이므로
   *     변경사항이 있다면 바꿔야 합니다.
   */
  List<ClubMemberPageResponse> getClubMemberList(Long semester);

  /**
   * [ 사용자들의 기본 정보 조회 메서드 ] 사용자 관리 화면 - 게스트 관리 목록 조회
   *
   * @param isGuest 조회할 사용자 목록의 게스트 여부
   * @param keyword 검색어 필터 (이름, 학과 가능)
   * @return 사용자 기본 정보가 담긴 UserManagementResponse 리스트
   */
  UserManagementResponse getUserManagement(boolean isGuest, String keyword);

  /**
   * [ 구성원들의 상세 정보 조회 메서드 ] 사용자 관리 화면 - 구성원 관리 목록 조회
   *
   * @param semester 기수 필터
   * @param position 역할 필터
   * @param track 트랙 필터
   * @param keyword 검색어 필터 (이름, 학과 가능)
   * @return 구성원 상세 정보가 담긴 ClubMemberInformationResponse 리스트
   */
  List<ClubMemberInformationResponse> getClubMemberManagementList(
      Long semester, Position position, Track track, String keyword);

  /**
   * [ 구성원 일괄 추가 메서드 ] 사용자 관리 화면 - 게스트에서 구성원 일괄 추가
   *
   * @param request 구성원에 추가할 사용자 식별자 리스트
   * @return 추가한 구성원 정보를 담은 UserInformationResponse 리스트
   */
  List<UserInformationResponse> addClubMembers(ChangeMembershipRequest request);

  /**
   * [ 임의의 사용자 추가 메서드 ]
   *
   * @param request 추가할 사용자 정보
   * @return 추가한 사용자의 정보를 담은 CreateUserResponse 객체
   */
  CreateUserResponse createUser(CreateUserRequest request);

  /**
   * [ 기존 구성원의 새 이력 추가 메서드 ]
   *
   * @param userId 사용자 식별자
   * @param semester 기수
   * @param position 역할
   * @param track 트랙
   * @return 구성원 상세 정보를 담은 ClubMemberInformationResponse 객체
   */
  ClubMemberInformationResponse addClubMemberRecord(
      Long userId, Long semester, Position position, Track track);

  /**
   * [ 사용자 프로필 이미지 변경 메서드 ]
   *
   * @param profileImage 프로필 이미지 파일
   * @return 사용자의 마이페이지 기본 정보를 담은 MyPageResponse 객체
   */
  MyPageResponse updateProfileImage(MultipartFile profileImage);

  /**
   * [ 사용자 비밀번호 변경 메서드 ]
   *
   * @param request 현재 비밀번호, 새 비밀번호, 새 비밀번호 확인 값을 담은 객체
   */
  void updatePassword(UpdatePasswordRequest request);

  /**
   * [ 구성원 이력 변경 메서드]
   *
   * @param clubMemberId 구성원 식별자
   * @param semester 기수
   * @param position 역할
   * @param track 트랙
   * @return 변경된 구성원 정보를 담은 ClubMemberInformationResponse 객체
   */
  ClubMemberInformationResponse updateClubMemberRecord(
      Long clubMemberId, Long semester, Position position, Track track);

  /**
   * [ 게스트 일괄 이동 메서드 ] 사용자 관리 화면 - 구성원에서 게스트로 이동 (구성원 이력 삭제)
   *
   * @param request 구성원에서 게스트로 이동 시킬 사용자 식별자 리스트
   */
  void addGuestsFromClubMembers(ChangeMembershipRequest request);

  /**
   * [ 사용자 일괄 삭제 메서드 ]
   *
   * @param request 삭제할 사용자 식별자 리스트
   */
  void deleteUsers(DeleteUsersRequest request);

  /**
   * [ 구성원 일괄 삭제 메서드 ]
   *
   * @param request 삭제할 구성원 식별자 리스트
   * @implNote 특정 사용자의 구성원 이력을 모두 삭제해도 사용자가 게스트에는 남음
   */
  void deleteClubMembers(DeleteClubMembersRequest request);
}
