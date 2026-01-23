/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.user.dto.request;

import java.util.List;

import jakarta.validation.constraints.Positive;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
@Schema(title = "DeleteClubMembersRequest: 구성원 이력 삭제 벌크 요청 DTO")
public class DeleteClubMembersRequest {

  @Schema(description = "구성원 이력을 삭제할 구성원 식별자 리스트")
  private List<@Positive Long> clubMemberIds;
}
