package com.cinemax.domain.task.controller;

import com.cinemax.core.controller.BaseController;
import com.cinemax.core.dto.response.ApiResponse;
import com.cinemax.domain.task.dto.TaskRequest;
import com.cinemax.domain.task.dto.TaskResponse;
import com.cinemax.domain.task.service.TaskService;
import com.cinemax.global.enums.TaskMode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Task 관리 API Controller
 */
@Slf4j
@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
@Tag(name = "Task", description = "Task 관리 API (과제/문항)")
public class TaskController extends BaseController {

    private final TaskService taskService;

    // Task 생성
    @PostMapping
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(summary = "Task 생성", description = "새로운 Task를 생성합니다.")
    public ResponseEntity<ApiResponse<TaskResponse>> createTask(@Valid @RequestBody TaskRequest request) {

        TaskResponse response = taskService.createTask(request);

        return created(response, "Task가 성공적으로 생성되었습니다.");
    }

    // Task 조회 (단일)
    // TaskTitle 추가해달래 김원탁이 -> 오태훈 일하자
    @GetMapping("/{taskId}/{cycleId}/{curId}/{weekNo}")
    @Operation(summary = "Task 조회", description = "Task 정보를 조회합니다.")
    public ResponseEntity<ApiResponse<TaskResponse>> getTask(@Parameter(description = "Task ID") @PathVariable Long taskId,
                                                             @Parameter(description = "Cycle ID") @PathVariable Long cycleId,
                                                             @Parameter(description = "Curriculum ID") @PathVariable Long curId,
                                                             @Parameter(description = "주차 번호") @PathVariable Integer weekNo) {

        TaskResponse response = taskService.getTask(taskId);

        return success(response, "Task 조회 성공");
    }

    /**
     * 전체 Task 조회
     */
    @GetMapping
    @Operation(summary = "전체 Task 목록 조회", description = "모든 Task를 조회합니다.")
    public ResponseEntity<ApiResponse<List<TaskResponse>>> getAllTasks() {

        List<TaskResponse> responses = taskService.getAllTasks();

        return success(responses, "전체 Task 목록 조회 성공");
    }

    // Cycle별 Task 조회
    @GetMapping("/cycle/{cycleId}")
    @Operation(summary = "Cycle별 Task 조회", description = "특정 Cycle의 모든 Task를 조회합니다.")
    public ResponseEntity<ApiResponse<List<TaskResponse>>> getTasksByCycleId(@Parameter(description = "Cycle ID") @PathVariable Long cycleId) {

        List<TaskResponse> responses = taskService.getTasksByCycleId(cycleId);

        return success(responses, "Cycle별 Task 조회 성공");
    }

    // Curriculum별 Task 조회
    @GetMapping("/curriculum/{curId}")
    @Operation(summary = "Curriculum별 Task 조회", description = "특정 Curriculum의 모든 Task를 조회합니다.")
    public ResponseEntity<ApiResponse<List<TaskResponse>>> getTasksByCurId(@Parameter(description = "Curriculum ID") @PathVariable Long curId) {

        List<TaskResponse> responses = taskService.getTasksByCurId(curId);

        return success(responses, "Curriculum별 Task 조회 성공");
    }

    // 주차별 Task 조회
    @GetMapping("/curriculum/{curId}/week/{weekNo}")
    @Operation(summary = "주차별 Task 조회", description = "특정 Curriculum의 특정 주차 Task를 조회합니다.")
    public ResponseEntity<ApiResponse<List<TaskResponse>>> getTasksByWeek(@Parameter(description = "Curriculum ID") @PathVariable Long curId,
                                                                          @Parameter(description = "주차 번호") @PathVariable Integer weekNo) {

        List<TaskResponse> responses = taskService.getTasksByWeek(curId, weekNo);

        return success(responses, "주차별 Task 조회 성공");
    }

    // Curriculum과 Cycle로 Task 조회
    @GetMapping("/curriculum/{curId}/cycle/{cycleId}")
    @Operation(summary = "Curriculum-Cycle별 Task 조회", description = "특정 Curriculum의 특정 Cycle Task를 조회합니다.")
    public ResponseEntity<ApiResponse<List<TaskResponse>>> getTasksByCurIdAndCycleId(@Parameter(description = "Curriculum ID") @PathVariable Long curId,
                                                                                     @Parameter(description = "Cycle ID") @PathVariable Long cycleId) {

        List<TaskResponse> responses = taskService.getTasksByCurIdAndCycleId(curId, cycleId);

        return success(responses, "Curriculum-Cycle별 Task 조회 성공");
    }

    // Task Mode로 조회
    @GetMapping("/mode/{taskMode}")
    @Operation(summary = "Task Mode별 조회", description = "특정 Task Mode의 모든 Task를 조회합니다.")
    public ResponseEntity<ApiResponse<List<TaskResponse>>> getTasksByMode(@Parameter(description = "Task Mode (ADVANCE, EASY)") @PathVariable TaskMode taskMode) {

        List<TaskResponse> responses = taskService.getTasksByMode(taskMode);

        return success(responses, "Task Mode별 조회 성공");
    }

    // Task 수정
    @PutMapping("/{taskId}/{cycleId}/{curId}/{weekNo}")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(summary = "Task 수정", description = "Task 정보를 수정합니다.")
    public ResponseEntity<ApiResponse<TaskResponse>> updateTask(@Parameter(description = "Task ID") @PathVariable Long taskId,
                                                                @Parameter(description = "Cycle ID") @PathVariable Long cycleId,
                                                                @Parameter(description = "Curriculum ID") @PathVariable Long curId,
                                                                @Parameter(description = "주차 번호") @PathVariable Integer weekNo,
                                                                @Valid @RequestBody TaskRequest request) {

        TaskResponse response = taskService.updateTask(taskId, request);

        return success(response, "Task가 성공적으로 수정되었습니다.");
    }

    // Task 삭제
    @DeleteMapping("/{taskId}/{cycleId}/{curId}/{weekNo}")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(summary = "Task 삭제", description = "Task를 삭제합니다.")
    public ResponseEntity<ApiResponse<Void>> deleteTask(@Parameter(description = "Task ID") @PathVariable Long taskId,
                                                        @Parameter(description = "Cycle ID") @PathVariable Long cycleId,
                                                        @Parameter(description = "Curriculum ID") @PathVariable Long curId,
                                                        @Parameter(description = "주차 번호") @PathVariable Integer weekNo) {

        taskService.deleteTask(taskId);

        return success(null, "Task가 성공적으로 삭제되었습니다.");
    }
}
