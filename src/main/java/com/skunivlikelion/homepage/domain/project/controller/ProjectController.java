/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.project.controller;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.skunivlikelion.homepage.domain.project.dto.request.CreateProjectMultipartBody;
import com.skunivlikelion.homepage.domain.project.dto.request.ProjectCreateRequest;
import com.skunivlikelion.homepage.domain.project.dto.request.ProjectTypeRequest;
import com.skunivlikelion.homepage.domain.project.dto.request.ProjectUpdateRequest;
import com.skunivlikelion.homepage.domain.project.dto.request.UpdateProjectMultipartBody;
import com.skunivlikelion.homepage.domain.project.dto.response.*;
import com.skunivlikelion.homepage.global.common.BaseResponse;
import com.skunivlikelion.homepage.global.page.response.InfiniteResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Encoding;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 멋쟁이사자처럼 홈페이지 프로젝트 관련 Controller interface 입니다.
 *
 * @since 2026.02.01
 * @see com.skunivlikelion.homepage.domain.project.entity.Project
 * @see com.skunivlikelion.homepage.domain.project.entity.ProjectImage
 * @see com.skunivlikelion.homepage.domain.project.entity.ProjectMember
 * @see com.skunivlikelion.homepage.domain.project.service.ProjectService
 * @author Lim Da Hyun, Keum Si Eon
 * @version latest: 1
 */
@RequestMapping("/api")
@Tag(name = "Project", description = "프로젝트 관련 기능을 제공하는 API")
public interface ProjectController {
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

  @Operation(
      summary = "[ 관리자 | 토큰 O | 프로젝트 생성 ]",
      description =
          """
            **Parameters**   \n
            title : 프로젝트 제목  \n
            semester : 기수식별자   \n
            award : 수상 여부 \n
            projectType : 프로젝트 타입   \n
            content : 프로젝트 설명    \n
            members : 트랙별 참여자 이름 목록 (Map<Track, List<String>> 형태)  \n
            예시(JSON): {"FRONTEND":["홍길동","김철수"],"BACKEND":["이영희"]}  \n
            projectImages : 프로젝트 이미지 파일 배열

            **Returns**  \n
            프로젝트 생성 성공/실패 여부
            """,
      requestBody =
          @io.swagger.v3.oas.annotations.parameters.RequestBody(
              content =
                  @Content(
                      mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                      schema = @Schema(implementation = CreateProjectMultipartBody.class),
                      encoding = {
                        @Encoding(name = "request", contentType = MediaType.APPLICATION_JSON_VALUE)
                      })))
  @PostMapping(value = "/v1/admin/projects", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  ResponseEntity<BaseResponse<ProjectResponse>> createProject(
      @Valid @RequestPart("request") ProjectCreateRequest request,
      @RequestPart(value = "projectImages") MultipartFile[] projectImages);

  @Operation(
      summary = "[ 관리자 | 토큰 O | project-id를 통한 프로젝트 수정 ]",
      description =
          """
           **Parameters**   \n
           title : 프로젝트 제목  \n
           semester : 기수식별자   \n
           award : 수상 여부 \n
           projectType : 프로젝트 타입   \n
           content : 프로젝트 설명    \n
           remainingProjectMemberIds: 수정 시 유지할 기존 멤버 식별자들   \n
           newMembers : 새로 추가할 트랙별 참여자 이름 목록 (Map<Track, List<String>> 형태)  \n
           예시(JSON): {"FRONTEND":["홍길동","김철수"],"BACKEND":["이영희"]}  \n
           remainingProjectImageIds : 수정 시 유지할 기존 이미지 URL 목록(선택)  \n
           newImages : 새로 추가할 프로젝트 이미지 파일 배열(선택)

           **Returns**  \n
           프로젝트 수정 성공/실패 여부
           """,
      requestBody =
          @io.swagger.v3.oas.annotations.parameters.RequestBody(
              content =
                  @Content(
                      mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                      schema = @Schema(implementation = UpdateProjectMultipartBody.class),
                      encoding = {
                        @Encoding(name = "request", contentType = MediaType.APPLICATION_JSON_VALUE)
                      })))
  @PutMapping(
      value = "/v1/admin/projects/{project-id}",
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  ResponseEntity<BaseResponse<ProjectUpdateResponse>> updateProject(
      @PathVariable(value = "project-id") Long projectId,
      @Valid @RequestPart("request") ProjectUpdateRequest request,
      @RequestPart(value = "newImages", required = false) MultipartFile[] newImages);

  @Operation(
      summary = "[ 관리자 | 토큰 O | project-id를 통한 프로젝트 삭제 ]",
      description =
          """
                    **Parameters**   \n
                    id : 삭제할 프로젝트 ID \n

                    **Returns** \n
                    프로젝트 삭제 성공/실패 여부
                    """)
  @DeleteMapping("/v1/admin/projects/{project-id}")
  ResponseEntity<BaseResponse<Void>> deleteProject(
      @PathVariable(value = "project-id") Long projectId);

  @Operation(
      summary = "[ 사용자 | 토큰 X | 프로젝트 목록 조회(페이징) - 기수/타입/검색어 필터 지원 ]",
      description =
          """
           **Query Parameters**   \n
           page : 조회할 게시글 페이지 번호(1부터 시작, 기본 1) \n
           semester : 기수 식별자(선택) \n
           projectType : 프로젝트 타입(선택) \n
           search : 검색어(선택) - 프로젝트 제목/설명 \n

           **Returns**    \n
           allProjectIds: 해당 필터로 조회된 모든 프로젝트 식별자 리스트    \n
           projectPageResponse: 프로젝트 페이징 처리 응답  \n
           content : 프로젝트 목록 \n
           first: 현재 페이지가 첫 페이지인지 \n
           last: 현재 페이지가 마지막 페이지인지  \n
           pageNum: 현재 페이지 번호  \n
           pageSize: 현재 페이지 크기  \n
           totalElements: 전체(페이지를 넘어서) 프로젝트 수   \n
           totalPages: 나올 페이지 수 \n
           """)
  @GetMapping("/v1/projects")
  ResponseEntity<BaseResponse<ProjectPageWrapperResponse<ProjectPageResponse>>>
      getProjectByPageAndSemesterAndTypeAndSearch(
          @Parameter(description = "프로젝트 타입 ID") @RequestParam(required = false) Long projectTypeId,
          @Parameter(description = "기수") @RequestParam(required = false) @Positive Long semester,
          @Parameter(description = "검색어") @RequestParam(required = false) String search,
          @Parameter(description = "페이지 번호(1부터 시작)") @RequestParam(defaultValue = "1")
              Integer pageNum,
          @Parameter(description = "페이지 크기(기본 6)") @RequestParam(defaultValue = "6")
              Integer pageSize);

  @Operation(
      summary = "[ 사용자 | 토큰 X | project-id를 통한 단일 프로젝트 조회 ]",
      description =
          """
           **Parameters**  \n
           project-id : 조회할 프로젝트 ID \n

           **Returns** \n
           단일 프로젝트 정보
           """)
  @GetMapping("/v1/projects/{project-id}")
  ResponseEntity<BaseResponse<ProjectDetailResponse>> getProjectByProjectId(
      @PathVariable(value = "project-id") @Positive Long projectId);

  @Operation(
      summary = "[ 사용자 | 토큰 X | [메인] 역대 수상작 목록 조회 ]",
      description =
          """
          **Parameters** \n
          page : 조회할 페이지 번호 (0부터 시작, 기본값 0) \n
          size : 페이지당 조회할 수상작 개수 (기본값 3) \n

          **Returns** \n
          메인 화면에 표시되는 역대 수상작 프로젝트 목록 (무한스크롤용 페이징 데이터)
          """)
  @GetMapping("/v1/projects/awards")
  ResponseEntity<BaseResponse<InfiniteResponse<ProjectAwardResponse>>> getAwardProjects(
      @RequestParam(value = "last-cursor-id", required = false) Long lastCursorId,
      @RequestParam(value = "size", defaultValue = "3") Integer size);
}
