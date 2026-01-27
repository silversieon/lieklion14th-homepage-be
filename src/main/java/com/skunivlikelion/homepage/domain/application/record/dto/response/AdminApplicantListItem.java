/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.application.record.dto.response;

import com.skunivlikelion.homepage.domain.common.enums.Track;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(title = "AdminApplicantListItem: 관리자 지원자 목록 아이템 DTO")
public record AdminApplicantListItem(
    @Schema(description = "지원서 식별자", example = "1") Long applicationRecordId,
    @Schema(description = "이름", example = "김나경") String name,
    @Schema(description = "학과", example = "공공인재학부") String department,
    @Schema(description = "학번", example = "2022213003") String studentNumber,
    @Schema(description = "지원 트랙(지원 파트)", example = "BACKEND") Track track,
    @Schema(description = "서류 합격 여부", example = "false") Boolean isDocumentPassed,
    @Schema(description = "면접 합격 여부", example = "false") Boolean isInterviewPassed) {}
