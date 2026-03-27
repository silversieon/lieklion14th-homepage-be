/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.project.dto.internal;

import java.util.List;

public record ProjectImagesChangedEvent(
    Long projectId, List<String> deletedImageUrls, List<UploadImagePayload> payloads) {}
