package com.skunivlikelion.homepage.domain.project.controller;

import com.skunivlikelion.homepage.domain.project.dto.request.ProjectTypeRequest;
import com.skunivlikelion.homepage.domain.project.dto.response.ProjectTypeResponse;
import com.skunivlikelion.homepage.domain.project.service.ProjectTypeService;
import com.skunivlikelion.homepage.global.common.BaseResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ProjectTypeControllerImpl implements ProjectTypeController {

    private final ProjectTypeService projectTypeService;

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<ProjectTypeResponse>> createProjectType(
            @Valid @RequestBody ProjectTypeRequest request) {
        ProjectTypeResponse response = projectTypeService.createProjectType(request);
        return ResponseEntity.status(201)
                .body(BaseResponse.success(201, "프로젝트 타입 생성에 성공했습니다.", response));
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<Void>> deleteProjectType(
            @PathVariable("project-type-id") Long projectTypeId) {
        projectTypeService.deleteProjectType(projectTypeId);
        return ResponseEntity.status(200).body(BaseResponse.success(200, "프로젝트 타입 삭제에 성공했습니다.", null));
    }

    @Override
    public ResponseEntity<BaseResponse<List<ProjectTypeResponse>>> getAllProjectTypes() {
        List<ProjectTypeResponse> response = projectTypeService.getAllProjectTypes();
        return ResponseEntity.status(200)
                .body(BaseResponse.success(200, "프로젝트 타입 목록 조회에 성공했습니다.", response));
    }
}
