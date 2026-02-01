/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.project.dto.request;

import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class UpdateProjectMultipartBody {

  @Schema(implementation = ProjectUpdateRequest.class)
  private ProjectUpdateRequest request;

  @ArraySchema(schema = @Schema(type = "string", format = "binary"))
  private MultipartFile[] projectImages;
}
