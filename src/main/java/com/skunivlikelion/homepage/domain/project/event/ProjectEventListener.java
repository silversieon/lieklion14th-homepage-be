/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.project.event;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.skunivlikelion.homepage.domain.project.dto.internal.ProjectImagesChangedEvent;
import com.skunivlikelion.homepage.domain.project.dto.internal.ProjectImagesDeletedEvent;
import com.skunivlikelion.homepage.domain.project.dto.internal.ProjectImagesUploadEvent;
import com.skunivlikelion.homepage.domain.project.service.ProjectImageService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProjectEventListener {

  private final ProjectImageService projectImageService;

  @Async("projectExecutor")
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handle(ProjectImagesUploadEvent event) {
    projectImageService.uploadProjectImages(event.projectId(), event.payloads());
  }

  @Async("projectExecutor")
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handle(ProjectImagesChangedEvent event) {
    projectImageService.deleteProjectImagesByUrl(event.deletedImageUrls());
    projectImageService.uploadProjectImages(event.projectId(), event.payloads());
  }

  @Async("projectExecutor")
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handle(ProjectImagesDeletedEvent event) {
    projectImageService.deleteProjectImagesByUrl(event.deletedImageUrls());
  }
}
