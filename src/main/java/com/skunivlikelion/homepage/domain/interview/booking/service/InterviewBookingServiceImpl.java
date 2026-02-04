/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.interview.booking.service;

import static com.skunivlikelion.homepage.domain.interview.booking.util.InterviewBookingCursorUtil.Cursor;
import static com.skunivlikelion.homepage.domain.interview.booking.util.InterviewBookingCursorUtil.decodeCursor;
import static com.skunivlikelion.homepage.domain.interview.booking.util.InterviewBookingCursorUtil.encodeCursor;
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
import com.skunivlikelion.homepage.domain.interview.booking.dto.response.AdminInterviewBookingInfiniteResponse;
import com.skunivlikelion.homepage.domain.interview.booking.dto.response.InterviewBookingResponse;
import com.skunivlikelion.homepage.domain.interview.booking.dto.response.UserInterviewBookingResponse;
import com.skunivlikelion.homepage.domain.interview.booking.entity.InterviewBooking;
import com.skunivlikelion.homepage.domain.interview.booking.exception.InterviewBookingErrorCode;
import com.skunivlikelion.homepage.domain.interview.booking.mapper.InterviewBookingMapper;
import com.skunivlikelion.homepage.domain.interview.booking.repository.AdminInterviewSlotView;
import com.skunivlikelion.homepage.domain.interview.booking.repository.InterviewBookingRepository;
import com.skunivlikelion.homepage.domain.interview.booking.util.InterviewBookingMaskUtil;
import com.skunivlikelion.homepage.domain.interview.booking.validator.InterviewBookingValidator;
import com.skunivlikelion.homepage.domain.interview.schedule.entity.InterviewSchedule;
import com.skunivlikelion.homepage.domain.interview.schedule.repository.InterviewScheduleRepository;
import com.skunivlikelion.homepage.domain.user.entity.User;
import com.skunivlikelion.homepage.domain.user.repository.UserRepository;
import com.skunivlikelion.homepage.global.exception.CustomException;
import com.skunivlikelion.homepage.global.page.exception.PageErrorStatus;
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

    if (!record.isDocumentPassed()) {
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
  public AdminInterviewBookingInfiniteResponse getAdminBookings(
      Long semester,
      Track track,
      LocalDate dateFrom,
      LocalDate dateTo,
      String search,
      String cursor,
      Integer size) {

    int resolvedSize = (size == null) ? 30 : size;
    if (resolvedSize <= 0 || resolvedSize > 100) {
      throw new CustomException(PageErrorStatus.PAGE_SIZE_ERROR);
    }

    String normalized = (search == null || search.isBlank()) ? null : search.trim().toLowerCase();
    Cursor c = (cursor == null || cursor.isBlank()) ? null : decodeCursor(cursor);

    var pageable = org.springframework.data.domain.PageRequest.of(0, resolvedSize + 1);

    List<AdminInterviewSlotView> rows =
        interviewBookingRepository.findAdminBookedSlotsInfinite(
            semester,
            track,
            dateFrom,
            dateTo,
            normalized,
            c == null ? null : c.track(),
            c == null ? null : c.date(),
            c == null ? null : c.startTime(),
            c == null ? null : c.scheduleId(),
            pageable);

    boolean hasNext = rows.size() > resolvedSize;
    if (hasNext) {
      rows = rows.subList(0, resolvedSize);
    }

    List<String> tracks =
        interviewScheduleRepository.findDistinctTracksBySemester(semester).stream()
            .map(Enum::name)
            .toList();

    List<Long> userIds =
        rows.stream()
            .map(AdminInterviewSlotView::getUserId)
            .filter(Objects::nonNull)
            .distinct()
            .toList();

    Map<Long, User> userMap =
        userRepository.findAllById(userIds).stream().collect(Collectors.toMap(User::getId, u -> u));

    List<AdminInterviewBookingInfiniteResponse.Item> items =
        rows.stream()
            .map(
                v -> {
                  // booked 슬롯만 내려오도록 쿼리에서 필터링했으므로 true 고정
                  boolean booked = true;

                  User u = (v.getUserId() == null) ? null : userMap.get(v.getUserId());

                  String name;
                  String studentNumber;
                  String department = null;
                  String phone = null;

                  if (u != null) {
                    // 유저 존재: User 테이블 기반 (마스킹 X)
                    name = u.getName();
                    studentNumber = u.getStudentNumber();
                    department = u.getDepartment();
                    phone = u.getPhoneNumber();
                  } else {
                    // 유저 삭제: 스냅샷만 사용
                    name = v.getSnapshotName();
                    studentNumber = v.getSnapshotStudentNumber();
                  }

                  AdminInterviewBookingInfiniteResponse.BookingInfo info =
                      new AdminInterviewBookingInfiniteResponse.BookingInfo(
                          v.getBookingId(),
                          name,
                          department,
                          studentNumber,
                          phone,
                          v.getApplicationRecordId());

                  return new AdminInterviewBookingInfiniteResponse.Item(
                      v.getScheduleId(),
                      v.getTrack().name(),
                      v.getDate(),
                      v.getStartTime(),
                      v.getEndTime(),
                      booked,
                      info);
                })
            .toList();

    String nextCursor = null;
    if (hasNext && !items.isEmpty()) {
      var last = items.get(items.size() - 1);
      nextCursor =
          encodeCursor(
              Track.valueOf(last.track()), last.date(), last.startTime(), last.scheduleId());
    }

    return new AdminInterviewBookingInfiniteResponse(
        semester.intValue(), tracks, items, nextCursor, hasNext, resolvedSize);
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

    if (!record.isDocumentPassed()) {
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
