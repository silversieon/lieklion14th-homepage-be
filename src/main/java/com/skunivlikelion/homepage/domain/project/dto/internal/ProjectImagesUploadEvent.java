/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.project.dto.internal;

import java.util.List;

public record ProjectImagesUploadEvent(Long projectId, List<UploadImagePayload> payloads) {}
