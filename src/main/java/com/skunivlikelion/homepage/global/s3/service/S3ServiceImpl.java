/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.global.s3.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.UUID;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.skunivlikelion.homepage.global.config.property.AwsProperties;
import com.skunivlikelion.homepage.global.exception.CustomException;
import com.skunivlikelion.homepage.global.s3.enums.PathName;
import com.skunivlikelion.homepage.global.s3.exception.S3ErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3ServiceImpl implements S3Service {

  private final S3Client s3Client;
  private final AwsProperties awsProperties;

  private static final String PREFIX_S3_URL = "https://";
  private static final String INFIX_S3_URL = ".s3.";
  private static final String SUFFIX_S3_URL = ".amazonaws.com/";

  private static final long MAX_BYTES = 5L * 1024 * 1024;

  @Override
  public String createKeyName(PathName pathName, String fileType) {
    return getPrefix(pathName) + '/' + UUID.randomUUID() + "." + fileType;
  }

  @Override
  public String uploadFile(PathName pathName, MultipartFile file) {
    validateFile(file);
    String keyName = createKeyName(pathName, "webp");

    try {
      byte[] webpBytes = toWebpBytes(file);

      s3Client.putObject(
          PutObjectRequest.builder()
              .bucket(awsProperties.getS3().getBucket())
              .key(keyName)
              .contentType("image/webp")
              .contentLength((long) webpBytes.length)
              .build(),
          RequestBody.fromBytes(webpBytes));

      log.info("[S3] 파일 업로드 성공 - keyName: {}", keyName);
      return createBucketImageUrl(keyName);
    } catch (IOException e) {
      throw new CustomException(S3ErrorCode.FILE_TYPE_INVALID);
    } catch (Exception e) {
      throw new CustomException(S3ErrorCode.FILE_SERVER_ERROR);
    }
  }

  @Override
  public void deleteFile(String keyName) {
    filedExists(keyName);
    try {
      s3Client.deleteObject(
          DeleteObjectRequest.builder()
              .bucket(awsProperties.getS3().getBucket())
              .key(keyName)
              .build());
      log.info("[S3] 이미지 삭제에 성공했습니다 - keyName: {}", keyName);
    } catch (Exception e) {
      log.error("[S3] 이미지 삭제 중 오류 발생", e);
      throw new CustomException(S3ErrorCode.FILE_SERVER_ERROR);
    }
  }

  @Override
  public String extractKeyNameFromUrl(String imageUrl) {
    if (!imageUrl.startsWith(getBucketUrl())) {
      log.error("[S3] 이미지 URL 방식이 잘못 되었습니다. - imageUrl: {}", imageUrl);
      throw new CustomException(S3ErrorCode.FILE_URL_INVALID);
    }
    String keyName = imageUrl.substring(getBucketUrl().length());
    log.info("[S3] keyName 추출 성공 - keyName: {}", keyName);
    return keyName;
  }

  private void filedExists(String keyName) {
    try {
      s3Client.headObject(
          HeadObjectRequest.builder()
              .bucket(awsProperties.getS3().getBucket())
              .key(keyName)
              .build());
    } catch (NoSuchKeyException e) {
      log.error("[S3] 해당 keyName을 가진 파일을 찾을 수 없습니다. - keyName: {}", keyName);
      throw new CustomException(S3ErrorCode.FILE_NOT_FOUND);
    }
  }

  private void validateFile(MultipartFile file) {

    if (file.getSize() > MAX_BYTES) {
      throw new CustomException(S3ErrorCode.FILE_SIZE_INVALID);
    }

    String contentType = file.getContentType();
    if (contentType == null || !contentType.startsWith("image/")) {
      throw new CustomException(S3ErrorCode.FILE_TYPE_INVALID);
    }
  }

  public static byte[] toWebpBytes(MultipartFile file) throws IOException {
    BufferedImage src;
    try (InputStream in = file.getInputStream()) {
      src = ImageIO.read(in);
    }
    if (src == null) {
      throw new CustomException(S3ErrorCode.FILE_TYPE_INVALID);
    }

    Iterator<ImageWriter> writers = ImageIO.getImageWritersByMIMEType("image/webp");

    ImageWriter writer = writers.next();
    try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageOutputStream ios = ImageIO.createImageOutputStream(baos)) {

      writer.setOutput(ios);

      ImageWriteParam param = writer.getDefaultWriteParam();
      if (param.canWriteCompressed()) {
        param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
      }

      writer.write(null, new IIOImage(src, null, null), param);
      ios.flush();

      return baos.toByteArray();
    } finally {
      writer.dispose();
    }
  }

  private String createBucketImageUrl(String keyName) {
    return PREFIX_S3_URL
        + awsProperties.getS3().getBucket()
        + INFIX_S3_URL
        + awsProperties.getRegionStatic()
        + SUFFIX_S3_URL
        + keyName;
  }

  private String getBucketUrl() {
    return PREFIX_S3_URL
        + awsProperties.getS3().getBucket()
        + INFIX_S3_URL
        + awsProperties.getRegionStatic()
        + SUFFIX_S3_URL;
  }

  private String getPrefix(PathName pathName) {
    return switch (pathName) {
      case PROFILE -> awsProperties.getS3().getPath().getProfile();
      case PROJECT -> awsProperties.getS3().getPath().getProject();
    };
  }
}
