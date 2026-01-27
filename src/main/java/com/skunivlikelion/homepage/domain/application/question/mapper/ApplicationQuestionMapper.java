/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.application.question.mapper;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.skunivlikelion.homepage.domain.application.form.entity.ApplicationForm;
import com.skunivlikelion.homepage.domain.application.question.dto.request.ApplicationQuestionUpsertRequest;
import com.skunivlikelion.homepage.domain.application.question.dto.response.ApplicationQuestionGetResponse;
import com.skunivlikelion.homepage.domain.application.question.dto.response.ApplicationQuestionUpsertResponse;
import com.skunivlikelion.homepage.domain.application.question.dto.response.ApplicationQuestionUpsertResponse.QuestionItemResponse;
import com.skunivlikelion.homepage.domain.application.question.dto.response.ApplicationQuestionUpsertResponse.TrackQuestionGroupResponse;
import com.skunivlikelion.homepage.domain.application.question.dto.response.ApplicationSummaryListResponse;
import com.skunivlikelion.homepage.domain.application.question.dto.response.ApplicationSummaryListResponse.ApplicationSummaryItem;
import com.skunivlikelion.homepage.domain.application.question.entity.ApplicationQuestion;
import com.skunivlikelion.homepage.domain.common.enums.Track;

@Component
public class ApplicationQuestionMapper {

  public List<ApplicationQuestion> toEntities(
      ApplicationForm form, ApplicationQuestionUpsertRequest request) {
    var groups = Optional.ofNullable(request.getGroups()).orElse(List.of());

    return groups.stream()
        .filter(Objects::nonNull)
        .flatMap(
            g -> {
              if (g.getTrack() == null) {
                return java.util.stream.Stream.empty();
              }

              var questions = Optional.ofNullable(g.getQuestions()).orElse(List.of());
              return questions.stream()
                  .map(q -> toEntity(form, g.getTrack(), q.getOrderNumber(), q.getContent()));
            })
        .toList();
  }

  private ApplicationQuestion toEntity(
      ApplicationForm form, Track track, Integer orderNumber, String content) {
    return ApplicationQuestion.builder()
        .applicationForm(form)
        .track(track)
        .orderNumber(orderNumber)
        .content(content)
        .build();
  }

  public ApplicationQuestionUpsertResponse toUpsertResponse(
      Long semester, List<ApplicationQuestion> saved) {
    Map<Track, List<QuestionItemResponse>> grouped =
        saved.stream()
            .collect(
                Collectors.groupingBy(
                    ApplicationQuestion::getTrack,
                    Collectors.mapping(
                        q ->
                            QuestionItemResponse.builder()
                                .questionId(q.getId())
                                .orderNumber(q.getOrderNumber())
                                .content(q.getContent())
                                .build(),
                        Collectors.toList())));

    List<TrackQuestionGroupResponse> groups =
        grouped.entrySet().stream()
            .sorted(Map.Entry.comparingByKey(Comparator.comparing(Enum::name)))
            .map(
                e ->
                    TrackQuestionGroupResponse.builder()
                        .track(e.getKey())
                        .questions(
                            e.getValue().stream()
                                .sorted(Comparator.comparing(QuestionItemResponse::getOrderNumber))
                                .toList())
                        .build())
            .toList();

    return ApplicationQuestionUpsertResponse.builder().semester(semester).groups(groups).build();
  }

  public ApplicationQuestionGetResponse toGetResponse(
      Long semester, Track track, List<ApplicationQuestion> questions) {
    return ApplicationQuestionGetResponse.builder()
        .semester(semester)
        .track(track)
        .questions(
            questions.stream()
                .sorted(Comparator.comparing(ApplicationQuestion::getOrderNumber))
                .map(
                    q ->
                        QuestionItemResponse.builder()
                            .questionId(q.getId())
                            .orderNumber(q.getOrderNumber())
                            .content(q.getContent())
                            .build())
                .toList())
        .build();
  }

  public ApplicationSummaryItem toApplicationSummaryItem(ApplicationForm form) {
    Long semester = form.getSemester().getSemester();
    return ApplicationSummaryItem.builder()
        .applicationFormId(form.getId())
        .semester(semester)
        .title(semester + "기 아기사자 모집 지원서")
        .closeAt(form.getCloseAt())
        .build();
  }

  public ApplicationSummaryListResponse toApplicationSummaryListResponse(
      LocalDateTime now, List<ApplicationForm> forms) {

    List<ApplicationSummaryItem> inProgress =
        forms.stream()
            .filter(f -> now.isBefore(f.getCloseAt()))
            .map(this::toApplicationSummaryItem)
            .toList();

    List<ApplicationSummaryItem> completed =
        forms.stream()
            .filter(f -> !now.isBefore(f.getCloseAt()))
            .map(this::toApplicationSummaryItem)
            .toList();

    return ApplicationSummaryListResponse.builder()
        .inProgress(inProgress)
        .completed(completed)
        .build();
  }
}
