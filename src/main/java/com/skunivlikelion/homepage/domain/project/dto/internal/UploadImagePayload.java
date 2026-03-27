/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.project.vo;

public record UploadImagePayload(String originalFilename, String contentType, byte[] bytes) {}
