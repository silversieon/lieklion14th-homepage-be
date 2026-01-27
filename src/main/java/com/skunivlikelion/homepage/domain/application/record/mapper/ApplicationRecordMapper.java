/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.application.record.mapper;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.skunivlikelion.homepage.domain.application.form.entity.ApplicationForm;
import com.skunivlikelion.homepage.domain.application.question.entity.ApplicationQuestion;
import com.skunivlikelion.homepage.domain.application.record.dto.response.AdminApplicantUserInfo;
import com.skunivlikelion.homepage.domain.application.record.dto.response.AdminApplicationDetailResponse;
import com.skunivlikelion.homepage.domain.application.record.dto.response.ApplicationAnswersGetResponse;
import com.skunivlikelion.homepage.domain.application.record.dto.response.ApplicationDraftSaveResponse;
import com.skunivlikelion.homepage.domain.application.record.dto.response.ApplicationQuestionAnswerItem;
import com.skunivlikelion.homepage.domain.application.record.dto.response.ApplicationRecordMeta;
import com.skunivlikelion.homepage.domain.application.record.dto.response.ApplicationSubmitResponse;
import com.skunivlikelion.homepage.domain.application.record.entity.ApplicationAnswer;
import com.skunivlikelion.homepage.domain.application.record.entity.ApplicationRecord;
import com.skunivlikelion.homepage.domain.common.enums.Track;
import com.skunivlikelion.homepage.domain.user.entity.User;

@Component
public class ApplicationRecordMapper {

  public ApplicationRecord toNewDraftRecord(ApplicationForm form, User user, Track track) {
    return ApplicationRecord.builder().applicationForm(form).user(user).track(track).build();
  }

  public ApplicationAnswer toNewEmptyAnswer(
      ApplicationRecord record, ApplicationQuestion question) {
    return ApplicationAnswer.builder()
        .applicationRecord(record)
        .question(question)
        .content("")
        .build();
  }

  public ApplicationDraftSaveResponse toDraftResponse(ApplicationRecord record) {
    return ApplicationDraftSaveResponse.builder().meta(toMeta(record)).build();
  }

  public ApplicationSubmitResponse toSubmitResponse(ApplicationRecord record) {
    return ApplicationSubmitResponse.builder().meta(toMeta(record)).build();
  }

  public ApplicationAnswersGetResponse toAnswersGetResponse(
      ApplicationRecord record,
      List<ApplicationQuestion> commonQuestions,
      List<ApplicationQuestion> trackQuestions,
      Map<Long, ApplicationAnswer> answerMap) {

    QaBundle qa = buildQaBundle(commonQuestions, trackQuestions, answerMap);

    return ApplicationAnswersGetResponse.builder()
        .meta(toMeta(record))
        .commonQuestions(qa.common())
        .trackQuestions(qa.track())
        .build();
  }

  public AdminApplicationDetailResponse toAdminApplicationDetailResponse(
      ApplicationRecord record,
      List<ApplicationQuestion> commonQuestions,
      List<ApplicationQuestion> trackQuestions,
      Map<Long, ApplicationAnswer> answerMap) {

    QaBundle qa = buildQaBundle(commonQuestions, trackQuestions, answerMap);

    return AdminApplicationDetailResponse.builder()
        .meta(toMeta(record))
        .userInfo(toAdminApplicantUserInfo(record))
        .commonQuestions(qa.common())
        .trackQuestions(qa.track())
        .build();
  }

  private ApplicationRecordMeta toMeta(ApplicationRecord record) {
    return ApplicationRecordMeta.builder()
        .applicationRecordId(record.getId())
        .applicationFormId(record.getApplicationForm().getId())
        .track(record.getTrack())
        .isSubmitted(record.isSubmitted())
        .submittedAt(record.getSubmittedAt())
        .build();
  }

  private QaBundle buildQaBundle(
      List<ApplicationQuestion> commonQuestions,
      List<ApplicationQuestion> trackQuestions,
      Map<Long, ApplicationAnswer> answerMap) {

    List<ApplicationQuestionAnswerItem> common =
        buildQuestionAnswerItems(commonQuestions, answerMap);
    List<ApplicationQuestionAnswerItem> track = buildQuestionAnswerItems(trackQuestions, answerMap);

    return new QaBundle(common, track);
  }

  private List<ApplicationQuestionAnswerItem> buildQuestionAnswerItems(
      List<ApplicationQuestion> questions, Map<Long, ApplicationAnswer> answerMap) {

    return questions.stream().map(q -> toQuestionAnswerItem(q, answerMap.get(q.getId()))).toList();
  }

  private ApplicationQuestionAnswerItem toQuestionAnswerItem(
      ApplicationQuestion question, ApplicationAnswer answerOrNull) {

    return ApplicationQuestionAnswerItem.builder()
        .questionId(question.getId())
        .orderNumber(question.getOrderNumber())
        .question(question.getContent())
        .answer(answerOrNull == null ? "" : answerOrNull.getContent())
        .build();
  }

  private AdminApplicantUserInfo toAdminApplicantUserInfo(ApplicationRecord record) {
    return AdminApplicantUserInfo.builder()
        .name(record.getUser().getName())
        .phoneNumber(record.getUser().getPhoneNumber())
        .department(record.getUser().getDepartment())
        .studentNumber(record.getUser().getStudentNumber())
        .email(record.getUser().getEmail())
        .supportPart(record.getTrack())
        .build();
  }

  private record QaBundle(
      List<ApplicationQuestionAnswerItem> common, List<ApplicationQuestionAnswerItem> track) {}
}
