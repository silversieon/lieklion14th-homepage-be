/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.user.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.skunivlikelion.homepage.domain.application.form.entity.ApplicationForm;
import com.skunivlikelion.homepage.domain.application.form.exception.ApplicationFormErrorCode;
import com.skunivlikelion.homepage.domain.application.form.repository.ApplicationFormRepository;
import com.skunivlikelion.homepage.domain.auth.service.AuthService;
import com.skunivlikelion.homepage.domain.common.enums.Track;
import com.skunivlikelion.homepage.domain.semester.service.SemesterService;
import com.skunivlikelion.homepage.domain.user.dto.request.*;
import com.skunivlikelion.homepage.domain.user.dto.response.*;
import com.skunivlikelion.homepage.domain.user.entity.ClubMember;
import com.skunivlikelion.homepage.domain.user.entity.User;
import com.skunivlikelion.homepage.domain.user.enums.Position;
import com.skunivlikelion.homepage.domain.user.exception.UserErrorCode;
import com.skunivlikelion.homepage.domain.user.mapper.ClubMemberMapper;
import com.skunivlikelion.homepage.domain.user.mapper.UserMapper;
import com.skunivlikelion.homepage.domain.user.repository.ClubMemberRepository;
import com.skunivlikelion.homepage.domain.user.repository.UserRepository;
import com.skunivlikelion.homepage.global.s3.enums.PathName;
import com.skunivlikelion.homepage.global.s3.service.S3Service;
import com.skunivlikelion.homepage.global.security.CurrentUserProvider;

import backend.boilerplate.exception.CustomException;
import backend.boilerplate.exception.GlobalErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

  private final UserMapper userMapper;
  private final CurrentUserProvider currentUserProvider;
  private final UserRepository userRepository;
  private final ClubMemberRepository clubMemberRepository;
  private final ClubMemberMapper clubMemberMapper;
  private final ApplicationFormRepository applicationFormRepository;
  private final SemesterService semesterService;
  private final AuthService authService;
  private final PasswordEncoder passwordEncoder;
  private final S3Service s3Service;

  @Override
  @Transactional(readOnly = true)
  public UserRoleResponse getCurrentUserRole() {
    User currentUser = currentUserProvider.getCurrentUser();
    log.info(
        "[User] 내 역할 조회 발생 - 사용자 식별자: {}, 이름: {}, 이메일: {}",
        currentUser.getId(),
        currentUser.getName(),
        currentUser.getEmail());
    return userMapper.toUserRoleResponse(currentUser);
  }

  @Override
  @Transactional(readOnly = true)
  public MyPageResponse getCurrentUserPage() {
    User currentUser = currentUserProvider.getCurrentUser();
    log.info(
        "[User] 내 정보 조회 발생 - 사용자 식별자: {}, 이름: {}, 이메일: {}",
        currentUser.getId(),
        currentUser.getName(),
        currentUser.getEmail());
    return userMapper.toMyPageResponse(currentUser);
  }

  @Override
  @Transactional(readOnly = true)
  public MyInformationResponse getCurrentUserInformation() {
    User currentUser = currentUserProvider.getCurrentUser();
    log.info(
        "[User] 내 상세 정보 조회 발생 - 사용자 식별자: {}, 이름: {}, 이메일: {}",
        currentUser.getId(),
        currentUser.getName(),
        currentUser.getEmail());
    return userMapper.toMyInformationResponse(currentUser);
  }

  @Override
  @Transactional(readOnly = true)
  public List<ClubMemberPageResponse> getClubMemberList(Long semester) {
    semesterService.checkSemesterExist(semester);

    LocalDateTime now = LocalDateTime.now();
    ApplicationForm applicationForm =
        applicationFormRepository
            .findBySemester_Semester(semester)
            .orElseThrow(
                () -> {
                  log.info("[User] 해당 기수의 지원 공고 없음, 지원 공고 필요 - semester: {}", semester);
                  return new CustomException(ApplicationFormErrorCode.NOT_FOUND_APPLICATION_FORM);
                });
    boolean canExposeBabyLion = now.isAfter(applicationForm.getFinalResultAt().plusDays(3));

    List<Position> positionsToFetch =
        canExposeBabyLion
            ? List.of(Position.LEAD, Position.COLEAD, Position.COREMEMBER, Position.BABYLION)
            : List.of(Position.LEAD, Position.COLEAD, Position.COREMEMBER);

    List<Track> tracksToFetch = Track.getCurrentSemesterTracks(semester);

    List<ClubMember> clubMembers =
        clubMemberRepository.findAllBySemesterAndPositionInAndTrackIn(
            semester, positionsToFetch, tracksToFetch);

    if (clubMembers.isEmpty()) {
      log.info("[User] 해당 기수의 구성원을 찾을 수 없음 - semester: {}", semester);
      throw new CustomException(UserErrorCode.USER_NOT_FOUND);
    }

    log.info("[User] 기수별 구성원 화면 조회 발생");
    return clubMemberMapper.toClubMemberPageResponses(positionsToFetch, tracksToFetch, clubMembers);
  }

  @Override
  @Transactional(readOnly = true)
  public UserManagementResponse getUserManagement(boolean isGuest, String keyword) {
    if (isGuest) {
      log.info("[User] 게스트 관리 - 게스트 목록 조회 발생");
      return userMapper.toUserManagementResponse(true, userRepository.findGuestUsers(keyword));
    } else {
      log.info("[User] 게스트 관리 - 구성원 목록 조회 발생");
      return userMapper.toUserManagementResponse(
          false, userRepository.findClubMemberUsers(keyword));
    }
  }

  @Override
  @Transactional(readOnly = true)
  public List<ClubMemberInformationResponse> getClubMemberManagementList(
      Long semester, Position position, Track track, String keyword) {
    String safeKeyword = (keyword == null || keyword.isBlank()) ? null : keyword;
    List<ClubMember> clubMembers;
    if (semester == null) {
      clubMembers =
          clubMemberRepository.searchClubMembersByPositionAndTrackAndKeywordIn(
              position, track, safeKeyword);
    } else {
      semesterService.checkSemesterExist(semester);
      clubMembers =
          clubMemberRepository.searchClubMembersBySemesterAndPositionAndTrackAndKeywordIn(
              semester, position, track, safeKeyword);
    }

    log.info("[User] 구성원 관리 - 구성원 목록 상세 조회 발생");
    return clubMembers.stream().map(clubMemberMapper::toClubMemberInformation).toList();
  }

  @Override
  @Transactional
  public List<UserInformationResponse> addClubMembers(ChangeMembershipRequest request) {
    List<User> users = userRepository.findAllByIdIn(request.getUserIds());
    if (users.size() != request.getUserIds().size()) {
      log.warn("[User] 게스트 -> 구성원에서 존재하지 않는 사용자 식별자 값 확인");
      throw new CustomException(UserErrorCode.USER_NOT_FOUND);
    }
    if (clubMemberRepository.existsByUser_IdIn(request.getUserIds())) {
      log.warn("[User] 구성원 이력이 존재하는 게스트 사용자 확인");
      throw new CustomException(GlobalErrorCode.INVALID_INPUT_VALUE);
    }

    Long currentSemester = semesterService.getAllSemesters().getFirst().getSemester();
    List<ClubMember> clubMembers = clubMemberMapper.toTempClubMembers(users, currentSemester);
    clubMemberRepository.saveAll(clubMembers);

    log.info("[User] 게스트 -> 구성원 추가 성공");
    return userMapper.toUserInformationList(users);
  }

  @Override
  @Transactional
  public CreateUserResponse createUser(CreateUserRequest request) {
    authService.validateUniqueValues(
        request.getEmail(), request.getStudentNumber(), request.getPhoneNumber());

    String encodedPassword = passwordEncoder.encode(request.getPassword());
    User user = userMapper.toUser(request, encodedPassword);
    User savedUser = userRepository.save(user);

    log.info("[User] 임의의 사용자 생성 성공 - userId: {}", savedUser.getId());
    return userMapper.toCreateUserResponse(savedUser);
  }

  @Override
  @Transactional
  public ClubMemberInformationResponse addClubMemberRecord(
      Long userId, Long semester, Position position, Track track) {
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(
                () -> {
                  log.warn("[User] 사용자를 찾을 수 없음 - userId: {}", userId);
                  return new CustomException(UserErrorCode.USER_NOT_FOUND);
                });
    semesterService.checkSemesterExist(semester);
    if (!clubMemberRepository.existsByUser_Id(userId)) {
      log.warn("[User] 구성원에 존재하지 않는 사용자에 대한 이력 추가 요청 발생");
      throw new CustomException(GlobalErrorCode.INVALID_INPUT_VALUE);
    }

    ClubMember savedClubMember =
        clubMemberRepository.save(clubMemberMapper.toClubMember(user, semester, position, track));

    log.info(
        "[User] 구성원 새 이력 추가 성공 - clubMemberId: {}, userId: {}, semester: {}, position: {}, track: {}",
        savedClubMember.getId(),
        userId,
        semester,
        position,
        track);
    return clubMemberMapper.toClubMemberInformation(savedClubMember);
  }

  @Override
  @Transactional
  public MyPageResponse updateProfileImage(MultipartFile profileImage) {
    User user = currentUserProvider.getCurrentUser();
    String currentImageUrl = user.getProfileImageUrl();

    String uploadImageUrl = s3Service.uploadFile(PathName.PROFILE, profileImage);
    user.updateProfileImage(uploadImageUrl);

    if (currentImageUrl != null) {
      s3Service.deleteFile(s3Service.extractKeyNameFromUrl(currentImageUrl));
    }

    log.info("[User] 프로필 이미지 업로드 성공 - userId: {}", user.getId());
    return userMapper.toMyPageResponse(user);
  }

  @Override
  @Transactional
  public void updatePassword(UpdatePasswordRequest request) {
    User user = currentUserProvider.getCurrentUser();
    if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
      log.info("[User] 비밀번호 변경에 현재 비밀번호와 불일치");
      throw new CustomException(UserErrorCode.CURRENT_PASSWORD_MISMATCH);
    }
    if (!request.getNewPassword().equals(request.getNewPasswordConfirmation())) {
      log.info("[User] 비밀번호 변경에 새 비밀번호와 새 비밀번호 확인 불일치");
      throw new CustomException(UserErrorCode.NEW_PASSWORD_MISMATCH);
    }

    String encodedNewPassword = passwordEncoder.encode(request.getNewPassword());
    user.updatePassword(encodedNewPassword);

    log.info("[User] 비밀번호 변경에 성공 - userId: {}", user.getId());
  }

  @Override
  @Transactional
  public ClubMemberInformationResponse updateClubMemberRecord(
      Long clubMemberId, Long semester, Position position, Track track) {
    ClubMember clubMember =
        clubMemberRepository
            .findById(clubMemberId)
            .orElseThrow(() -> new CustomException(UserErrorCode.CLUBMEMBER_NOT_FOUND));
    semesterService.checkSemesterExist(semester);
    clubMember.updateClubMemberRecord(semester, position, track);

    log.info("[User] 구성원 이력 변경에 성공 - clubMemberId: {}", clubMemberId);
    return clubMemberMapper.toClubMemberInformation(clubMember);
  }

  @Override
  @Transactional
  public void addGuestsFromClubMembers(ChangeMembershipRequest request) {
    if (userRepository.countByIdIn(request.getUserIds()) != request.getUserIds().size()) {
      log.warn("[User] 구성원 -> 게스트에서 존재하지 않는 사용자 식별자 값 확인");
      throw new CustomException(UserErrorCode.USER_NOT_FOUND);
    }
    if (clubMemberRepository.countDistinctUserIdsInClubMember(request.getUserIds())
        != request.getUserIds().size()) {
      log.warn("[User] 구성원 이력이 존재하지 않은 사용자 식별자 값 확인");
      throw new CustomException(UserErrorCode.CLUBMEMBER_NOT_FOUND);
    }

    log.info("[User] 해당 사용자들의 구성원 이력 일괄 삭제 완료");
    clubMemberRepository.deleteAllByUser_IdIn(request.getUserIds());
  }

  @Override
  @Transactional
  public void deleteUsers(DeleteUsersRequest request) {
    if (userRepository.countByIdIn(request.getUserIds()) != request.getUserIds().size()) {
      log.warn("[User] 사용자 삭제에서 존재하지 않는 사용자 식별자 값 확인");
      throw new CustomException(UserErrorCode.USER_NOT_FOUND);
    }

    log.info("[User] 해당 사용자들 일괄 삭제 완료");
    userRepository.deleteAllByIdInBatch(request.getUserIds());
  }

  @Override
  @Transactional
  public void deleteClubMembers(DeleteClubMembersRequest request) {
    if (clubMemberRepository.countByIdIn(request.getClubMemberIds())
        != request.getClubMemberIds().size()) {
      log.warn("[User] 구성원 삭제에서 존재하지 않는 구성원 식별자 값 확인");
      throw new CustomException(UserErrorCode.CLUBMEMBER_NOT_FOUND);
    }

    log.info("[User] 해당 구성원들 일괄 삭제 완료(게스트로 분류됨)");
    clubMemberRepository.deleteAllByIdInBatch(request.getClubMemberIds());
  }
}
