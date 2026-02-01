/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.user.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.skunivlikelion.homepage.domain.application.form.dto.response.ApplicationFormResponse;
import com.skunivlikelion.homepage.domain.application.form.entity.ApplicationForm;
import com.skunivlikelion.homepage.domain.application.form.exception.ApplicationFormErrorCode;
import com.skunivlikelion.homepage.domain.application.form.repository.ApplicationFormRepository;
import com.skunivlikelion.homepage.domain.application.form.service.ApplicationFormService;
import com.skunivlikelion.homepage.domain.application.record.entity.ApplicationRecord;
import com.skunivlikelion.homepage.domain.application.record.repository.ApplicationRecordRepository;
import com.skunivlikelion.homepage.domain.auth.service.AuthService;
import com.skunivlikelion.homepage.domain.common.enums.Track;
import com.skunivlikelion.homepage.domain.interview.booking.repository.InterviewBookingRepository;
import com.skunivlikelion.homepage.domain.interview.booking.service.InterviewBookingService;
import com.skunivlikelion.homepage.domain.semester.entity.Semester;
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
import com.skunivlikelion.homepage.global.exception.CustomException;
import com.skunivlikelion.homepage.global.exception.GlobalErrorCode;
import com.skunivlikelion.homepage.global.page.mapper.InfiniteMapper;
import com.skunivlikelion.homepage.global.page.response.CreateUserInfiniteResponse;
import com.skunivlikelion.homepage.global.s3.enums.PathName;
import com.skunivlikelion.homepage.global.s3.service.S3Service;
import com.skunivlikelion.homepage.global.security.CurrentUserProvider;

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
  private final InfiniteMapper infiniteMapper;
  private final ApplicationFormService applicationFormService;
  private final ApplicationRecordRepository applicationRecordRepository;
  private final InterviewBookingService interviewBookingService;
  private final InterviewBookingRepository interviewBookingRepository;

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

    ApplicationFormResponse currentApplicationForm =
        applicationFormService.getCurrentApplicationFormResponse();
    Optional<ApplicationRecord> applicationRecord =
        applicationRecordRepository.findLatestByFormIdAndUserId(
            currentApplicationForm.getId(), currentUser.getId());
    boolean documentSubmitted = false;
    if (applicationRecord.isPresent()) documentSubmitted = applicationRecord.get().isSubmitted();

    LocalDateTime now = LocalDateTime.now();
    boolean interviewScheduleChangable;
    if (currentApplicationForm.getApplicationResultAt().isAfter(now)
        || currentApplicationForm.getInterviewScheduleConfirmedAt().isBefore(now)) {
      interviewScheduleChangable = false;
    } else {
      interviewScheduleChangable = true;
    }

    Long currentSemester = currentApplicationForm.getSemester();
    boolean interviewScheduleSubmitted =
        interviewBookingService.existInterviewBookingByUserAndSemester(
            currentUser, currentSemester);
    log.info(
        "[User] 내 정보 조회 발생 - 사용자 식별자: {}, 이름: {}, 이메일: {}",
        currentUser.getId(),
        currentUser.getName(),
        currentUser.getEmail());
    return userMapper.toMyPageResponse(
        currentUser, documentSubmitted, interviewScheduleChangable, interviewScheduleSubmitted);
  }

  @Override
  @Transactional(readOnly = true)
  public ClubMemberCursorResponse<List<ClubMemberPageResponse>> getClubMemberList(
      Long semester, Position nextPositionCursor, Track nextTrackCursor) {
    if (nextPositionCursor == null || !(nextPositionCursor instanceof Position)) {
      throw new CustomException(GlobalErrorCode.INVALID_INPUT_VALUE);
    }
    Long safeSemester = semesterService.getSemester(semester).getSemester();

    ApplicationForm applicationForm =
        applicationFormRepository
            .findBySemester_Semester(safeSemester)
            .orElseThrow(
                () -> {
                  log.info("[User] 해당 기수의 지원 공고 없음, 지원 공고 필요 - semester: {}", safeSemester);
                  return new CustomException(ApplicationFormErrorCode.NOT_FOUND_APPLICATION_FORM);
                });

    LocalDateTime now = LocalDateTime.now();
    boolean canExposeBabyLion = now.isAfter(applicationForm.getFinalResultAt().plusDays(3));

    List<Track> tracksToFetchAvailable = Track.getCurrentSemesterTracks(safeSemester);

    List<Position> positionsToFetch;
    List<Track> tracksToFetch = new ArrayList<>();
    List<ClubMemberPageResponse> clubMemberPageResponses = new ArrayList<>();

    Position newPositionCursor = null;
    Track newTrackCursor = null;
    boolean hasNext = false;
    if (nextPositionCursor == Position.LEAD || nextPositionCursor == Position.COLEAD) {
      positionsToFetch = List.of(Position.LEAD, Position.COLEAD);
      tracksToFetch.addAll(tracksToFetchAvailable);

      List<ClubMember> clubMembers =
          clubMemberRepository.findAllBySemester_SemesterAndPositionInAndTrackIn(
              safeSemester, positionsToFetch, tracksToFetch);
      clubMemberPageResponses.addAll(
          clubMemberMapper.toClubMemberPageResponses(clubMembers, positionsToFetch, tracksToFetch));
      newPositionCursor = Position.COREMEMBER;
      newTrackCursor = tracksToFetchAvailable.getFirst();
      hasNext = true;
    } else if (nextPositionCursor == Position.COREMEMBER) {
      positionsToFetch = List.of(Position.LEAD, Position.COLEAD);
      tracksToFetch.addAll(tracksToFetchAvailable);

      List<ClubMember> clubMembers;
      clubMembers =
          clubMemberRepository.findAllBySemester_SemesterAndPositionInAndTrackIn(
              safeSemester, positionsToFetch, tracksToFetch);
      clubMemberPageResponses.addAll(
          clubMemberMapper.toClubMemberPageResponses(clubMembers, positionsToFetch, tracksToFetch));

      tracksToFetch.clear();
      clubMembers.clear();

      positionsToFetch = List.of(Position.COREMEMBER);
      for (int i = 0; i < tracksToFetchAvailable.size(); i++) {
        Track track = tracksToFetchAvailable.get(i);
        tracksToFetch.add(track);
        if (track == nextTrackCursor) {
          if (i == tracksToFetchAvailable.size() - 1) {
            if (canExposeBabyLion) {
              newPositionCursor = Position.BABYLION;
              newTrackCursor = tracksToFetchAvailable.getFirst();
              hasNext = true;
            }
          } else {
            newPositionCursor = Position.COREMEMBER;
            newTrackCursor = tracksToFetchAvailable.get(++i);
            hasNext = true;
          }
          break;
        }
      }
      clubMembers =
          clubMemberRepository.findAllBySemester_SemesterAndPositionInAndTrackIn(
              safeSemester, positionsToFetch, tracksToFetch);
      clubMemberPageResponses.addAll(
          clubMemberMapper.toClubMemberPageResponses(clubMembers, positionsToFetch, tracksToFetch));
    } else {
      if (!canExposeBabyLion) throw new CustomException(GlobalErrorCode.INVALID_INPUT_VALUE);

      positionsToFetch = List.of(Position.LEAD, Position.COLEAD, Position.COREMEMBER);
      tracksToFetch.addAll(tracksToFetchAvailable);

      List<ClubMember> clubMembers;
      clubMembers =
          clubMemberRepository.findAllBySemester_SemesterAndPositionInAndTrackIn(
              safeSemester, positionsToFetch, tracksToFetch);
      clubMemberPageResponses.addAll(
          clubMemberMapper.toClubMemberPageResponses(clubMembers, positionsToFetch, tracksToFetch));

      tracksToFetch.clear();
      clubMembers.clear();

      positionsToFetch = List.of(Position.BABYLION);
      for (int i = 0; i < tracksToFetchAvailable.size(); i++) {
        Track track = tracksToFetchAvailable.get(i);
        tracksToFetch.add(track);
        if (track == nextTrackCursor) {
          if (i != tracksToFetchAvailable.size() - 1) {
            newPositionCursor = Position.BABYLION;
            newTrackCursor = tracksToFetchAvailable.get(++i);
            hasNext = true;
            break;
          }
        }
      }
      clubMembers =
          clubMemberRepository.findAllBySemester_SemesterAndPositionInAndTrackIn(
              safeSemester, positionsToFetch, tracksToFetch);
      clubMemberPageResponses.addAll(
          clubMemberMapper.toClubMemberPageResponses(clubMembers, positionsToFetch, tracksToFetch));
    }

    if (clubMemberPageResponses.isEmpty()) {
      log.info("[User] 해당 기수의 구성원을 찾을 수 없음 - semester: {}", safeSemester);
      throw new CustomException(UserErrorCode.USER_NOT_FOUND);
    }
    log.info("[User] 기수별 구성원 화면 조회 발생");
    return clubMemberMapper.toClubMemberCursorResponse(
        clubMemberPageResponses, newPositionCursor, newTrackCursor, hasNext);
  }

  @Override
  @Transactional(readOnly = true)
  public UserManagementResponse getUserManagement(
      boolean isGuest, Long lastUserId, Integer size, String keyword) {
    Pageable pageable = PageRequest.of(0, size + 1, Sort.by(Direction.DESC, "id"));
    if (isGuest) {
      List<User> guestUsers = userRepository.findGuestUsers(pageable, lastUserId, keyword);
      CreateUserInfiniteResponse createUserInfiniteResponse =
          infiniteMapper.toCreateUserInfiniteResponse(guestUsers, size);
      log.info(
          "[User] 게스트 관리 | 게스트 목록 조회 발생 - lastUserId: {}, size: {}, keyword: {}",
          lastUserId,
          size,
          keyword);
      return userMapper.toUserManagementResponse(
          true,
          infiniteMapper.toUserInformationInfiniteResponse(
              userMapper.toUserInformationList(createUserInfiniteResponse.getUsers()),
              createUserInfiniteResponse.getLastCursor(),
              createUserInfiniteResponse.isHasNext(),
              size));
    } else {
      List<User> clubMemberUsers =
          userRepository.findClubMemberUsers(pageable, lastUserId, keyword);
      CreateUserInfiniteResponse createUserInfiniteResponse =
          infiniteMapper.toCreateUserInfiniteResponse(clubMemberUsers, size);
      log.info(
          "[User] 게스트 관리 | 구성원 목록 조회 발생 - lastUserId: {}, size: {}, keyword: {}",
          lastUserId,
          size,
          keyword);
      return userMapper.toUserManagementResponse(
          false,
          infiniteMapper.toUserInformationInfiniteResponse(
              userMapper.toUserInformationList(createUserInfiniteResponse.getUsers()),
              createUserInfiniteResponse.getLastCursor(),
              createUserInfiniteResponse.isHasNext(),
              size));
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
      Long safeSemester = semesterService.getSemester(semester).getSemester();
      clubMembers =
          clubMemberRepository.searchClubMembersBySemesterAndPositionAndTrackAndKeywordIn(
              safeSemester, position, track, safeKeyword);
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

    Semester currentSemester = semesterService.getLatestSemester();
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
    Semester safeSemester = semesterService.getSemester(semester);
    if (!clubMemberRepository.existsByUser_Id(userId)) {
      log.warn("[User] 구성원에 존재하지 않는 사용자에 대한 이력 추가 요청 발생");
      throw new CustomException(GlobalErrorCode.INVALID_INPUT_VALUE);
    }

    ClubMember savedClubMember =
        clubMemberRepository.save(
            clubMemberMapper.toClubMember(user, safeSemester, position, track));

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
    Long currentUserId = currentUserProvider.getCurrentUserId();
    User currentUser =
        userRepository
            .findById(currentUserId)
            .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

    String currentImageUrl = currentUser.getProfileImageUrl();
    String uploadImageUrl = s3Service.uploadFile(PathName.PROFILE, profileImage);
    currentUser.updateProfileImage(uploadImageUrl);

    if (currentImageUrl != null) {
      s3Service.deleteFile(s3Service.extractKeyNameFromUrl(currentImageUrl));
    }

    ApplicationFormResponse currentApplicationForm =
        applicationFormService.getCurrentApplicationFormResponse();
    Optional<ApplicationRecord> applicationRecord =
        applicationRecordRepository.findLatestByFormIdAndUserId(
            currentApplicationForm.getId(), currentUser.getId());
    boolean documentSubmitted = false;
    if (applicationRecord.isPresent()) documentSubmitted = applicationRecord.get().isSubmitted();

    LocalDateTime now = LocalDateTime.now();
    boolean interviewScheduleChangable;
    if (currentApplicationForm.getApplicationResultAt().isAfter(now)
        || currentApplicationForm.getInterviewScheduleConfirmedAt().isBefore(now)) {
      interviewScheduleChangable = false;
    } else {
      interviewScheduleChangable = true;
    }

    Long currentSemester = currentApplicationForm.getSemester();
    boolean interviewScheduleSubmitted =
        interviewBookingService.existInterviewBookingByUserAndSemester(
            currentUser, currentSemester);

    log.info("[User] 프로필 이미지 업로드 성공 - userId: {}", currentUser.getId());
    return userMapper.toMyPageResponse(
        currentUser, documentSubmitted, interviewScheduleChangable, interviewScheduleSubmitted);
  }

  @Override
  @Transactional
  public void updatePassword(UpdatePasswordRequest request) {
    Long userId = currentUserProvider.getCurrentUserId();
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));
    if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
      log.info("[User] 비밀번호 변경에 현재 비밀번호와 불일치");
      throw new CustomException(UserErrorCode.CURRENT_PASSWORD_MISMATCH);
    }
    if (!request.getNewPassword().equals(request.getNewPasswordConfirmation())) {
      log.info("[User] 비밀번호 변경에 새 비밀번호와 새 비밀번호 확인 불일치");
      throw new CustomException(UserErrorCode.NEW_PASSWORD_MISMATCH);
    }
    if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
      log.info("[User] 현재 비밀번호와 동일한 비밀번호 입력");
      throw new CustomException(UserErrorCode.CONFLICT_NEW_PASSWORD);
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
    Semester safeSemester = semesterService.getSemester(semester);
    clubMember.updateClubMemberRecord(safeSemester, position, track);

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
