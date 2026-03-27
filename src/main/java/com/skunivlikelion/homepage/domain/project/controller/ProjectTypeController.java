package com.skunivlikelion.homepage.domain.project.controller;

import com.skunivlikelion.homepage.domain.project.dto.request.ProjectTypeRequest;
import com.skunivlikelion.homepage.domain.project.dto.response.ProjectTypeResponse;
import com.skunivlikelion.homepage.global.common.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 멋쟁이사자처럼 홈페이지 프로젝트 타입 관련 Controller interface 입니다.
 *
 * @since 2026.03.27
 * @see com.skunivlikelion.homepage.domain.project.entity.ProjectType
 * @see com.skunivlikelion.homepage.domain.project.service.ProjectTypeService
 * @author Lim Da Hyun, Keum Si Eon
 * @version latest: 1
 */
@RequestMapping("/api")
@Tag(name = "ProjectType", description = "프로젝트 타입 관련 기능을 제공하는 API")
public interface ProjectTypeController {
    @Operation(
            summary = "[ 관리자 | 토큰 O | 프로젝트타입 생성 ]",
            description =
                    """
                      **Parameters**  \n
                      projectType : 프로젝트 타입   \n
          
                      **Returns**  \n
                      projectTypeId: 프로젝트 타입 식별자  \n
                      projectTypeName: 프로젝트 타입명   \n
                      """)
    @PostMapping(value = "/v1/admin/project-types")
    ResponseEntity<BaseResponse<ProjectTypeResponse>> createProjectType(
            @Valid @RequestBody ProjectTypeRequest request);

    @Operation(
            summary = "[ 관리자 | 토큰 O | projectType-id 를 통한 프로젝트타입 삭제 ]",
            description =
                    """
                      **Parameters** \n
                      project-type-id : 삭제할 프로젝트 타입 ID   \n
          
                      **Returns** \n
                      프로젝트 타입 삭제 성공/실패 여부 \n
                      """)
    @DeleteMapping("/v1/admin/project-types/{project-type-id}")
    ResponseEntity<BaseResponse<Void>> deleteProjectType(
            @PathVariable("project-type-id") Long projectTypeId);

    @Operation(
            summary = "[ 사용자 | 토큰 X | 등록된 프로젝트 타입 전체 오름차순 조회 ]",
            description = """
          **Returns**  \n
          등록된 모든 프로젝트 타입 오름차순 목록 \n
          """)
    @GetMapping("/v1/project-types")
    ResponseEntity<BaseResponse<List<ProjectTypeResponse>>> getAllProjectTypes();
}
