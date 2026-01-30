/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.application.record.dto.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.skunivlikelion.homepage.domain.common.enums.Track;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "ApplicationRecordMeta: 지원서 공통 메타 정보 DTO")
public class ApplicationRecordMeta {

  @Schema(description = "지원서 식별자", example = "1")
  private Long applicationRecordId;

  @Schema(description = "모집 공고 식별자", example = "1")
  private Long applicationFormId;

  @Schema(description = "기수", example = "13")
  private Long semester;

  @Schema(description = "지원 트랙", example = "BACKEND")
  private Track track;

  @Schema(description = "제출 여부", example = "false")
  private boolean isSubmitted;

  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  @Schema(description = "제출 일시 (미제출시 null)")
  private LocalDateTime submittedAt;
}
