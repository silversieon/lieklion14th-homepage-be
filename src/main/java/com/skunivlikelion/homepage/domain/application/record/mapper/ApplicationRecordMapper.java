/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.application.record.mapper;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.skunivlikelion.homepage.domain.application.form.entity.ApplicationForm;
import com.skunivlikelion.homepage.domain.application.question.cache.CachedQuestion;
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

  public ApplicationRecordResponse toAnswersGetResponseByCache(
      ApplicationRecord record,
      List<CachedQuestion> commonQuestions,
      List<CachedQuestion> trackQuestions,
      Map<Long, ApplicationAnswer> answerMap) {

    QaBundle qa = buildQaBundleByCache(commonQuestions, trackQuestions, answerMap);

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

  private QaBundle buildQaBundleByCache(
      List<CachedQuestion> commonQuestions,
      List<CachedQuestion> trackQuestions,
      Map<Long, ApplicationAnswer> answerMap) {

    List<ApplicationQuestionAnswerItem> common =
        buildQuestionAnswerItemsByCache(commonQuestions, answerMap);
    List<ApplicationQuestionAnswerItem> track =
        buildQuestionAnswerItemsByCache(trackQuestions, answerMap);

    return new QaBundle(common, track);
  }

  private List<ApplicationQuestionAnswerItem> buildQuestionAnswerItemsByCache(
      List<CachedQuestion> questions, Map<Long, ApplicationAnswer> answerMap) {

    if (questions == null || questions.isEmpty()) {
      return List.of();
    }
    return questions.stream()
        .map(q -> toQuestionAnswerItemByCache(q, answerMap.get(q.questionId())))
        .toList();
  }

  private ApplicationQuestionAnswerItem toQuestionAnswerItemByCache(
      CachedQuestion question, ApplicationAnswer answerOrNull) {

    return ApplicationQuestionAnswerItem.builder()
        .questionId(question.questionId())
        .orderNumber(question.orderNumber())
        .question(question.content())
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
