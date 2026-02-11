/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.interview.booking.service;

import static com.skunivlikelion.homepage.domain.interview.booking.util.InterviewBookingMaskUtil.maskName;
import static com.skunivlikelion.homepage.domain.interview.booking.util.InterviewBookingMaskUtil.maskStudentNumber;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.skunivlikelion.homepage.domain.application.form.service.ApplicationFormService;
import com.skunivlikelion.homepage.domain.application.record.entity.ApplicationRecord;
import com.skunivlikelion.homepage.domain.application.record.repository.ApplicationRecordRepository;
import com.skunivlikelion.homepage.domain.common.enums.Track;
import com.skunivlikelion.homepage.domain.interview.booking.dto.request.InterviewBookingCreateRequest;
import com.skunivlikelion.homepage.domain.interview.booking.dto.response.AdminInterviewBookingResponse;
import com.skunivlikelion.homepage.domain.interview.booking.dto.response.InterviewBookingResponse;
import com.skunivlikelion.homepage.domain.interview.booking.dto.response.UserInterviewBookingResponse;
import com.skunivlikelion.homepage.domain.interview.booking.entity.InterviewBooking;
import com.skunivlikelion.homepage.domain.interview.booking.exception.InterviewBookingErrorCode;
import com.skunivlikelion.homepage.domain.interview.booking.mapper.InterviewBookingMapper;
import com.skunivlikelion.homepage.domain.interview.booking.repository.InterviewBookingRepository;
import com.skunivlikelion.homepage.domain.interview.booking.util.InterviewBookingMaskUtil;
import com.skunivlikelion.homepage.domain.interview.booking.validator.InterviewBookingValidator;
import com.skunivlikelion.homepage.domain.interview.schedule.entity.InterviewSchedule;
import com.skunivlikelion.homepage.domain.interview.schedule.repository.InterviewScheduleRepository;
import com.skunivlikelion.homepage.domain.user.entity.User;
import com.skunivlikelion.homepage.domain.user.repository.UserRepository;
import com.skunivlikelion.homepage.global.exception.CustomException;
import com.skunivlikelion.homepage.global.security.CurrentUserProvider;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class InterviewBookingServiceImpl implements InterviewBookingService {

  private final InterviewBookingRepository interviewBookingRepository;
  private final InterviewScheduleRepository interviewScheduleRepository;
  private final ApplicationRecordRepository applicationRecordRepository;
  private final CurrentUserProvider currentUserProvider;
  private final InterviewBookingMapper interviewBookingMapper;
  private final ApplicationFormService applicationFormService;
  private final UserRepository userRepository;
  private final InterviewBookingValidator interviewBookingValidator;

  @Override
  public InterviewBookingResponse createBooking(InterviewBookingCreateRequest request) {
    if (request == null || request.getScheduleId() == null) {
      throw new CustomException(InterviewBookingErrorCode.INVALID_REQUEST);
    }

    User user = currentUserProvider.getCurrentUser();
    Long userId = user.getId();
    Long scheduleId = request.getScheduleId();

    String email = user.getEmail();
    String applicantKey = InterviewBookingMaskUtil.sha256Hex(email.toLowerCase());
    String maskedEmail = InterviewBookingMaskUtil.maskEmail(email);

    log.info("[InterviewBooking] 예약 요청 - userId={}, scheduleId={}", userId, scheduleId);

    InterviewSchedule schedule =
        interviewScheduleRepository
            .findByIdForUpdate(scheduleId)
            .orElseThrow(() -> new CustomException(InterviewBookingErrorCode.NOT_FOUND_SCHEDULE));

    interviewBookingValidator.validateBookingWindow(schedule.getSemester());
    interviewBookingValidator.validateSlotNotStarted(schedule);

    ApplicationRecord record =
        applicationRecordRepository
            .findSubmittedBySemesterAndUserId(schedule.getSemester(), userId)
            .orElseThrow(
                () -> new CustomException(InterviewBookingErrorCode.NOT_FOUND_APPLICATION_RECORD));

    if (!record.getIsDocumentPassed()) {
      log.warn(
          "[InterviewBooking] 예약 실패 - 서류 미합격 - userId={}, recordId={}", userId, record.getId());
      throw new CustomException(InterviewBookingErrorCode.NOT_PASSED_DOCUMENT);
    }

    if (record.getTrack() != schedule.getTrack()) {
      log.warn(
          "[InterviewBooking] 예약 실패 - 트랙 불일치 - userId={}, recordTrack={}, scheduleTrack={}",
          userId,
          record.getTrack(),
          schedule.getTrack());
      throw new CustomException(InterviewBookingErrorCode.TRACK_MISMATCH);
    }

    if (interviewBookingRepository.existsBySemesterIdAndApplicantKey(
        schedule.getSemester(), applicantKey)) {
      throw new CustomException(InterviewBookingErrorCode.ALREADY_BOOKED_USER);
    }

    if (interviewBookingRepository.existsByInterviewSchedule_Id(scheduleId)) {
      throw new CustomException(InterviewBookingErrorCode.ALREADY_BOOKED_SCHEDULE);
    }

    try {
      InterviewBooking saved =
          interviewBookingRepository.save(
              InterviewBooking.builder()
                  .bookedAt(LocalDateTime.now())
                  .interviewSchedule(schedule)
                  .userId(userId)

                  // Snapshot
                  .userNameMasked(maskName(user.getName()))
                  .userStudentNumberMasked(maskStudentNumber(user.getStudentNumber()))
                  .userEmailMasked(maskedEmail)

                  // 정책/검색
                  .semesterId(schedule.getSemester())
                  .track(schedule.getTrack())
                  .applicantKey(applicantKey)
                  .applicationRecordId(record.getId())
                  .build());

      log.info("[InterviewBooking] 예약 완료 - bookingId={}", saved.getId());
      return interviewBookingMapper.toResponse(saved);

    } catch (DataIntegrityViolationException e) {
      String msg = e.getMostSpecificCause().getMessage();
      log.error("[InterviewBooking] rootCause={}", msg, e);

      if (msg != null && msg.contains("uk_interview_booking_schedule")) {
        throw new CustomException(InterviewBookingErrorCode.ALREADY_BOOKED_SCHEDULE);
      }
      if (msg != null && msg.contains("uk_interview_booking_semester_applicant")) {
        throw new CustomException(InterviewBookingErrorCode.ALREADY_BOOKED_USER);
      }
      throw e;
    }
  }

  @Override
  @Transactional(readOnly = true)
  public AdminInterviewBookingResponse getAdminBookings(
      Long semester, LocalDate date, Track track, String search) {

    if (semester == null || date == null) {
      log.warn(
          "[InterviewBooking] 관리자 예약 조회 실패 - 필수 파라미터 누락 - semester={}, date={}", semester, date);
      throw new CustomException(InterviewBookingErrorCode.INVALID_REQUEST);
    }

    String normalized = (search == null || search.isBlank()) ? null : search.trim().toLowerCase();

    log.info(
        "[InterviewBooking] 관리자 예약 조회 요청 - semester={}, date={}, track={}, search={}",
        semester,
        date,
        track,
        (normalized == null ? null : "***"));

    List<com.skunivlikelion.homepage.domain.interview.booking.repository.AdminInterviewSlotView>
        rows =
            interviewScheduleRepository.findAdminBookingSchedulesBySemesterAndDate(
                semester, date, track);

    log.info(
        "[InterviewBooking] 관리자 예약 조회 - 슬롯 조회 완료 - semester={}, date={}, track={}, slotCount={}",
        semester,
        date,
        track,
        rows.size());

    if (rows.isEmpty()) {
      List<String> allTracks =
          Track.getCurrentSemesterTracks(semester).stream().map(Enum::name).toList();

      log.info(
          "[InterviewBooking] 관리자 예약 조회 결과 - 해당 날짜/트랙에 슬롯 없음 - semester={}, date={}, track={}, tracks={}",
          semester,
          date,
          track,
          allTracks);

      List<AdminInterviewBookingResponse.TrackGroup> empty =
          allTracks.stream()
              .map(t -> new AdminInterviewBookingResponse.TrackGroup(t, List.of()))
              .toList();

      return new AdminInterviewBookingResponse(semester.intValue(), empty);
    }

    List<Long> userIds =
        rows.stream()
            .map(
                com.skunivlikelion.homepage.domain.interview.booking.repository
                        .AdminInterviewSlotView
                    ::getUserId)
            .filter(Objects::nonNull)
            .distinct()
            .toList();

    Map<Long, User> userMap =
        userRepository.findAllById(userIds).stream().collect(Collectors.toMap(User::getId, u -> u));

    log.info(
        "[InterviewBooking] 관리자 예약 조회 - 예약자 정보 조회 완료 - slotUserIdsCount={}, userEntityLoadedCount={}",
        userIds.size(),
        userMap.size());

    List<String> allTracks =
        Track.getCurrentSemesterTracks(semester).stream().map(Enum::name).toList();

    Map<String, List<AdminInterviewBookingResponse.TimeSlot>> timesByTrack =
        rows.stream()
            .collect(
                Collectors.groupingBy(
                    v -> v.getTrack().name(),
                    Collectors.mapping(
                        v -> {
                          boolean booked = (v.getBookingId() != null);

                          if (!booked) {
                            if (normalized != null) {
                              return null; // 검색어가 있으면 예약 없는 슬롯 제외
                            }
                            return new AdminInterviewBookingResponse.TimeSlot(
                                v.getScheduleId(), v.getStartTime(), v.getEndTime(), false, null);
                          }

                          User u = (v.getUserId() == null) ? null : userMap.get(v.getUserId());

                          String name;
                          String studentNumber;
                          String department = null;
                          String phone = null;

                          if (u != null) {
                            name = u.getName();
                            studentNumber = u.getStudentNumber();
                            department = u.getDepartment();
                            phone = u.getPhoneNumber();
                          } else {
                            name = v.getSnapshotName();
                            studentNumber = v.getSnapshotStudentNumber();
                          }

                          boolean matched =
                              (normalized == null)
                                  || (name != null && name.toLowerCase().contains(normalized))
                                  || (studentNumber != null
                                      && studentNumber.toLowerCase().contains(normalized));

                          if (!matched) {
                            return null;
                          }

                          var info =
                              new AdminInterviewBookingResponse.BookingInfo(
                                  v.getBookingId(),
                                  name,
                                  department,
                                  studentNumber,
                                  phone,
                                  v.getApplicationRecordId());

                          return new AdminInterviewBookingResponse.TimeSlot(
                              v.getScheduleId(), v.getStartTime(), v.getEndTime(), true, info);
                        },
                        Collectors.toList())));

    timesByTrack.replaceAll((k, list) -> list.stream().filter(Objects::nonNull).toList());

    List<AdminInterviewBookingResponse.TrackGroup> trackGroups =
        allTracks.stream()
            .map(
                trackName -> {
                  List<AdminInterviewBookingResponse.TimeSlot> times =
                      timesByTrack.getOrDefault(trackName, List.of());

                  List<AdminInterviewBookingResponse.DateGroup> dates =
                      times.isEmpty()
                          ? List.of()
                          : List.of(new AdminInterviewBookingResponse.DateGroup(date, times));

                  return new AdminInterviewBookingResponse.TrackGroup(trackName, dates);
                })
            .toList();

    long totalSlotsReturned =
        trackGroups.stream()
            .flatMap(tg -> tg.dates().stream())
            .flatMap(dg -> dg.times().stream())
            .count();

    long bookedSlotsReturned =
        trackGroups.stream()
            .flatMap(tg -> tg.dates().stream())
            .flatMap(dg -> dg.times().stream())
            .filter(AdminInterviewBookingResponse.TimeSlot::booked)
            .count();

    log.info(
        "[InterviewBooking] 관리자 예약 조회 응답 - semester={}, date={}, trackFilter={}, searchApplied={}, tracksCount={}, returnedSlots={}, returnedBookedSlots={}",
        semester,
        date,
        track,
        (normalized != null),
        trackGroups.size(),
        totalSlotsReturned,
        bookedSlotsReturned);

    return new AdminInterviewBookingResponse(semester.intValue(), trackGroups);
  }

  @Override
  @Transactional(readOnly = true)
  public UserInterviewBookingResponse getMyBooking(Long semester) {

    User user = currentUserProvider.getCurrentUser();

    // applicantKey: 이메일 기반 해시
    String applicantKey = InterviewBookingMaskUtil.sha256Hex(user.getEmail().toLowerCase());

    Long resolvedSemester;
    if (semester != null) {
      resolvedSemester = semester;
      log.info(
          "[InterviewBooking] 사용자 예약 조회 - semester 직접 지정 - userId={}, semester={}",
          user.getId(),
          resolvedSemester);
    } else {
      resolvedSemester = applicationFormService.getCurrentApplicationSemester();
      log.info(
          "[InterviewBooking] 사용자 예약 조회 - 현재 공고 semester 자동 적용 - userId={}, semester={}",
          user.getId(),
          resolvedSemester);
    }

    InterviewBooking booking =
        interviewBookingRepository
            .findBySemesterIdAndApplicantKey(resolvedSemester, applicantKey)
            .orElseThrow(
                () -> {
                  log.warn(
                      "[InterviewBooking] 예약 조회 실패 - 예약 없음 - userId={}, semester={}",
                      user.getId(),
                      resolvedSemester);
                  return new CustomException(InterviewBookingErrorCode.NOT_FOUND_BOOKING);
                });

    InterviewSchedule s = booking.getInterviewSchedule();

    log.info(
        "[InterviewBooking] 예약 조회 성공 - userId={}, bookingId={}, scheduleId={}",
        user.getId(),
        booking.getId(),
        s.getId());

    return new UserInterviewBookingResponse(
        resolvedSemester.intValue(),
        new UserInterviewBookingResponse.Booking(
            booking.getId(),
            booking.getTrack(),
            s.getId(),
            s.getDate(),
            s.getStartTime(),
            s.getEndTime()));
  }

  @Override
  @Transactional
  public UserInterviewBookingResponse updateMyBooking(Long semester, Long newScheduleId) {

    if (newScheduleId == null) {
      throw new CustomException(InterviewBookingErrorCode.NOT_FOUND_SCHEDULE);
    }

    User user = currentUserProvider.getCurrentUser();
    Long userId = user.getId();

    String applicantKey = InterviewBookingMaskUtil.sha256Hex(user.getEmail().toLowerCase());

    Long resolvedSemester =
        (semester != null) ? semester : applicationFormService.getCurrentApplicationSemester();

    interviewBookingValidator.validateBookingWindow(resolvedSemester);

    InterviewBooking booking =
        interviewBookingRepository
            .findBySemesterIdAndApplicantKeyForUpdate(resolvedSemester, applicantKey)
            .orElseThrow(
                () -> {
                  log.warn(
                      "[InterviewBooking] 변경 실패 - 기존 예약 없음 - userId={}, semester={}",
                      userId,
                      resolvedSemester);
                  return new CustomException(InterviewBookingErrorCode.NOT_FOUND_BOOKING);
                });

    interviewBookingValidator.validateSlotNotStarted(booking.getInterviewSchedule());

    log.info(
        "[InterviewBooking] 예약 변경 요청 - userId={}, semester={}, newScheduleId={}",
        userId,
        resolvedSemester,
        newScheduleId);

    Long currentScheduleId = booking.getInterviewSchedule().getId();

    if (currentScheduleId.equals(newScheduleId)) {
      log.warn(
          "[InterviewBooking] 변경 실패 - 동일 슬롯 - userId={}, scheduleId={}", userId, newScheduleId);
      throw new CustomException(InterviewBookingErrorCode.SAME_SCHEDULE);
    }

    InterviewSchedule newSchedule =
        interviewScheduleRepository
            .findByIdForUpdate(newScheduleId)
            .orElseThrow(
                () -> {
                  log.warn("[InterviewBooking] 변경 실패 - 일정 없음 - newScheduleId={}", newScheduleId);
                  return new CustomException(InterviewBookingErrorCode.NOT_FOUND_SCHEDULE);
                });

    if (!newSchedule.getSemester().equals(resolvedSemester)) {
      throw new CustomException(InterviewBookingErrorCode.NOT_FOUND_SCHEDULE);
    }

    interviewBookingValidator.validateSlotNotStarted(newSchedule);

    if (interviewBookingRepository.existsByInterviewSchedule_Id(newScheduleId)) {
      log.warn("[InterviewBooking] 변경 실패 - 슬롯 중복 예약 - newScheduleId={}", newScheduleId);
      throw new CustomException(InterviewBookingErrorCode.ALREADY_BOOKED_SCHEDULE);
    }

    ApplicationRecord record =
        applicationRecordRepository
            .findSubmittedBySemesterAndUserId(resolvedSemester, userId)
            .orElseThrow(
                () -> new CustomException(InterviewBookingErrorCode.NOT_FOUND_APPLICATION_RECORD));

    if (!record.getIsDocumentPassed()) {
      log.warn(
          "[InterviewBooking] 변경 실패 - 서류 미합격 - userId={}, recordId={}", userId, record.getId());
      throw new CustomException(InterviewBookingErrorCode.NOT_PASSED_DOCUMENT);
    }

    if (record.getTrack() != newSchedule.getTrack()) {
      log.warn(
          "[InterviewBooking] 변경 실패 - 트랙 불일치 - userId={}, recordTrack={}, scheduleTrack={}",
          userId,
          record.getTrack(),
          newSchedule.getTrack());
      throw new CustomException(InterviewBookingErrorCode.TRACK_MISMATCH);
    }

    booking.changeSchedule(newSchedule);
    booking.changeTrack(newSchedule.getTrack());
    booking.changeSemesterId(resolvedSemester);

    log.info(
        "[InterviewBooking] 변경 완료 - userId={}, bookingId={}, {} -> {}",
        userId,
        booking.getId(),
        currentScheduleId,
        newScheduleId);

    InterviewSchedule s = booking.getInterviewSchedule();

    return new UserInterviewBookingResponse(
        resolvedSemester.intValue(),
        new UserInterviewBookingResponse.Booking(
            booking.getId(),
            booking.getTrack(),
            s.getId(),
            s.getDate(),
            s.getStartTime(),
            s.getEndTime()));
  }

  @Override
  public void deleteAdminBooking(Long bookingId) {

    log.info("[InterviewBooking] 관리자 예약 삭제 요청 - bookingId={}", bookingId);

    InterviewBooking booking =
        interviewBookingRepository
            .findByIdForUpdate(bookingId)
            .orElseThrow(
                () -> {
                  log.warn("[InterviewBooking] 관리자 예약 삭제 실패 - 예약 없음 - bookingId={}", bookingId);
                  return new CustomException(InterviewBookingErrorCode.BOOKING_NOT_FOUND);
                });

    InterviewSchedule schedule = booking.getInterviewSchedule();

    LocalDateTime startAt = LocalDateTime.of(schedule.getDate(), schedule.getStartTime());
    LocalDateTime now = LocalDateTime.now();

    if (!now.isBefore(startAt)) {
      log.warn(
          "[InterviewBooking] 관리자 예약 삭제 실패 - 이미 시작된 일정 - bookingId={}, scheduleId={}, startAt={}, now={}",
          bookingId,
          schedule.getId(),
          startAt,
          now);
      throw new CustomException(InterviewBookingErrorCode.PAST_SCHEDULE);
    }

    interviewBookingRepository.delete(booking);

    log.info(
        "[InterviewBooking] 관리자 예약 삭제 완료 - bookingId={}, scheduleId={}, userId={}",
        bookingId,
        schedule.getId(),
        booking.getUserId());
  }

  @Override
  @Transactional(readOnly = true)
  public boolean existInterviewBookingByUserAndSemester(User user, Long semester) {

    String applicantKey = InterviewBookingMaskUtil.sha256Hex(user.getEmail().toLowerCase());

    Optional<InterviewBooking> booking =
        interviewBookingRepository.findBySemesterIdAndApplicantKey(semester, applicantKey);

    return booking.isPresent();
  }
}
