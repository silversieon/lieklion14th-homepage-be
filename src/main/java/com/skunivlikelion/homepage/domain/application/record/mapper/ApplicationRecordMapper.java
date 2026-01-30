/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.application.record.mapper;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.skunivlikelion.homepage.domain.application.form.entity.ApplicationForm;
import com.skunivlikelion.homepage.domain.application.question.entity.ApplicationQuestion;
import com.skunivlikelion.homepage.domain.application.record.dto.response.ApplicantUserInfo;
import com.skunivlikelion.homepage.domain.application.record.dto.response.ApplicationAnswerItem;
import com.skunivlikelion.homepage.domain.application.record.dto.response.ApplicationQuestionAnswerItem;
import com.skunivlikelion.homepage.domain.application.record.dto.response.ApplicationRecordMeta;
import com.skunivlikelion.homepage.domain.application.record.dto.response.ApplicationRecordResponse;
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

  public ApplicationRecordResponse toAnswersGetResponse(
      ApplicationRecord record,
      List<ApplicationQuestion> commonQuestions,
      List<ApplicationQuestion> trackQuestions,
      Map<Long, ApplicationAnswer> answerMap) {

    QaBundle qa = buildQaBundle(commonQuestions, trackQuestions, answerMap);

    return ApplicationRecordResponse.builder()
        .meta(toMeta(record))
        .userInfo(toApplicantUserInfo(record.getUser(), record.getTrack()))
        .commonQuestions(qa.common())
        .trackQuestions(qa.track())
        .build();
  }

  public ApplicantUserInfo toApplicantUserInfo(User user, Track trackOrNull) {
    return ApplicantUserInfo.builder()
        .name(user.getName())
        .phoneNumber(user.getPhoneNumber())
        .department(user.getDepartment())
        .studentNumber(user.getStudentNumber())
        .email(user.getEmail())
        .track(trackOrNull)
        .build();
  }

  public List<ApplicationAnswerItem> toAnswerItems(List<ApplicationAnswer> answers) {
    if (answers == null || answers.isEmpty()) {
      return List.of();
    }
    return answers.stream().map(this::toAnswerItem).toList();
  }

  public ApplicationRecordMeta toMeta(ApplicationRecord record) {
    return ApplicationRecordMeta.builder()
        .applicationRecordId(record.getId())
        .applicationFormId(record.getApplicationForm().getId())
        .semester(record.getApplicationForm().getSemester().getSemester())
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

  private ApplicationAnswerItem toAnswerItem(ApplicationAnswer answer) {
    Long questionId =
        (answer == null || answer.getQuestion() == null) ? null : answer.getQuestion().getId();
    String content = (answer == null || answer.getContent() == null) ? "" : answer.getContent();

    return ApplicationAnswerItem.builder().questionId(questionId).answer(content).build();
  }

  private record QaBundle(
      List<ApplicationQuestionAnswerItem> common, List<ApplicationQuestionAnswerItem> track) {}
}
