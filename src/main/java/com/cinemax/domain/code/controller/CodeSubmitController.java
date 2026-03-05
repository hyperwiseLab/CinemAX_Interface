package com.cinemax.domain.code.controller;

import com.cinemax.core.controller.BaseController;
import com.cinemax.core.dto.response.ApiResponse;
import com.cinemax.domain.classes.dto.ClassSubmitResponse;
import com.cinemax.domain.classes.service.ClassSubmitService;
import com.cinemax.domain.code.dto.CodeSubmitRequest;
import com.cinemax.domain.code.dto.SubmitResultResponse;
import com.cinemax.domain.code.service.CodeSubmitService;
import com.cinemax.domain.user.entity.User;
import com.cinemax.global.security.CustomUserDetailsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 코드 제출 API
 */
@Slf4j
@RestController
@RequestMapping("/code/submit")
@RequiredArgsConstructor
@Tag(name = "Code Submit", description = "코드 제출 및 채점 API")
public class CodeSubmitController extends BaseController {

    private final CodeSubmitService codeSubmitService;
    private final ClassSubmitService classSubmitService;

    // 코드 제출 및 자동 채점
    @PostMapping
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR', 'ADMIN')")
    @Operation(
        summary = "코드 제출",
        description = """
            작성한 코드를 제출하고 자동 채점합니다.

            ## testCaseResults 산출 방식

            **프론트엔드에서 처리:**
            1. 과제의 테스트 케이스 목록을 GET /testcases/task/{taskId} 로 조회
            2. 각 테스트 케이스마다:
               - 학생이 작성한 코드를 실행 (브라우저 또는 로컬 환경)
               - 테스트 케이스의 inputText를 표준 입력으로 제공
               - 코드 실행 결과(actualOutput)와 expectedOutput 비교
               - 출력이 일치하면 passed=true, 불일치하면 passed=false
               - 컴파일 에러나 런타임 에러 발생 시 errorMessage에 에러 내용 저장
               - 실행 시간(executionTime) 측정
            3. 모든 테스트 결과를 testCaseResults 배열에 담아 제출

            **백엔드에서 처리:**
            1. 프론트엔드에서 받은 testCaseResults 검증
            2. 각 테스트 케이스의 가중치(weight)를 기반으로 점수 계산:
               - 획득 점수 = (통과한 테스트 가중치 합) / (전체 테스트 가중치 합) × 100
            3. 모든 테스트를 통과했는지 확인 (result: true/false)
            4. 제출 이력을 DB에 저장

            ## 요청 예시
            ```json
            {
              "taskId": 1,
              "classId": 10,
              "weeklySessionId": 5,
              "cycleId": 1,
              "curId": 2,
              "weekNo": 3,
              "syntaxId": 7,
              "language": "java",
              "code": "public class Main { public static void main(String[] args) { System.out.println(\\"Hello World\\"); } }",
              "testCaseResults": [
                {
                  "testCaseId": 1,
                  "passed": true,
                  "input": "",
                  "expectedOutput": "Hello World",
                  "actualOutput": "Hello World",
                  "errorMessage": null,
                  "executionTime": 125,
                  "weight": 50
                },
                {
                  "testCaseId": 2,
                  "passed": false,
                  "input": "5",
                  "expectedOutput": "25",
                  "actualOutput": "20",
                  "errorMessage": null,
                  "executionTime": 98,
                  "weight": 50
                }
              ]
            }
            ```
            """
    )
    public ResponseEntity<ApiResponse<SubmitResultResponse>> submitCode(@Valid @RequestBody CodeSubmitRequest request,
                                                                        @AuthenticationPrincipal com.cinemax.global.security.CustomUserDetailsService userDetails) {

        SubmitResultResponse response = codeSubmitService.submitCode(request, userDetails.getUserId());

        String message = response.getResult() ? "제출 성공! 모든 테스트를 통과했습니다." : "제출 완료. 일부 테스트를 통과하지 못했습니다.";
        return created(response, message);
    }

    // 내 제출 이력 조회
    @GetMapping("/history")
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR', 'ADMIN')")
    @Operation(summary = "내 제출 이력", description = "특정 과제에 대한 내 제출 이력을 조회합니다.")
    public ResponseEntity<ApiResponse<List<ClassSubmitResponse>>> getMySubmissions(@Parameter(description = "과제 ID") @RequestParam Long taskId,
                                                                                   @Parameter(description = "수업 ID") @RequestParam Long classId,
                                                                                   @AuthenticationPrincipal com.cinemax.global.security.CustomUserDetailsService userDetails) {

        List<ClassSubmitResponse> responses = classSubmitService.getStudentSubmissions(taskId, classId, userDetails.getUserId());

        return success(responses, "제출 이력을 조회했습니다.");
    }

    // 내 최신 제출 조회
    @GetMapping("/latest")
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR', 'ADMIN')")
    @Operation(summary = "최신 제출 조회", description = "특정 과제에 대한 최신 제출을 조회합니다.")
    public ResponseEntity<ApiResponse<ClassSubmitResponse>> getLatestSubmission(@Parameter(description = "과제 ID") @RequestParam Long taskId,
                                                                                @Parameter(description = "수업 ID") @RequestParam Long classId,
                                                                                @AuthenticationPrincipal com.cinemax.global.security.CustomUserDetailsService userDetails) {

        ClassSubmitResponse response = classSubmitService.getLatestSubmission(taskId, classId, userDetails.getUserId());

        return success(response, "최신 제출을 조회했습니다.");
    }

    // 제출 횟수 조회
    @GetMapping("/count")
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR', 'ADMIN')")
    @Operation(summary = "제출 횟수", description = "특정 과제에 대한 제출 횟수를 조회합니다.")
    public ResponseEntity<ApiResponse<Long>> getSubmissionCount(@Parameter(description = "과제 ID") @RequestParam Long taskId,
                                                                @Parameter(description = "수업 ID") @RequestParam Long classId,
                                                                @AuthenticationPrincipal com.cinemax.global.security.CustomUserDetailsService userDetails) {

        Long count = classSubmitService.getSubmissionCount(taskId, classId, userDetails.getUserId());

        return success(count, "제출 횟수를 조회했습니다.");
    }

    // 제출 여부 확인
    @GetMapping("/exists")
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR', 'ADMIN')")
    @Operation(summary = "제출 여부 확인", description = "특정 과제에 제출 이력이 있는지 확인합니다.")
    public ResponseEntity<ApiResponse<Boolean>> hasSubmission(@Parameter(description = "과제 ID") @RequestParam Long taskId,
                                                              @Parameter(description = "수업 ID") @RequestParam Long classId,
                                                              @AuthenticationPrincipal com.cinemax.global.security.CustomUserDetailsService userDetails) {

        boolean exists = classSubmitService.hasSubmission(taskId, classId, userDetails.getUserId());

        return success(exists, "제출 여부를 확인했습니다.");
    }

    // 과제의 모든 제출 조회 (교수용)
    @GetMapping("/task/{taskId}")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(summary = "과제 전체 제출 조회", description = "특정 과제의 모든 제출을 조회합니다. (교수 전용)")
    public ResponseEntity<ApiResponse<List<ClassSubmitResponse>>> getAllTaskSubmissions(@Parameter(description = "과제 ID") @PathVariable Long taskId,
                                                                                        @Parameter(description = "수업 ID") @RequestParam Long classId) {

        List<ClassSubmitResponse> responses = classSubmitService.getAllTaskSubmissions(taskId, classId);

        return success(responses, "과제 전체 제출을 조회했습니다.");
    }

    // 주차 수업의 모든 제출 조회 (권한별 필터링)
    @GetMapping("/weekly-session/{weeklySessionId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR', 'ADMIN')")
    @Operation(
        summary = "주차 수업 제출 조회",
        description = """
            특정 주차 수업의 제출을 조회합니다.
            - 교수/관리자: 모든 학생의 제출 조회
            - 학생: 자신의 제출만 조회
            """
    )
    public ResponseEntity<ApiResponse<List<ClassSubmitResponse>>> getAllWeeklySessionSubmissions(
            @Parameter(description = "주차 수업 ID") @PathVariable Long weeklySessionId,
            @Parameter(description = "수업 ID") @RequestParam Long classId,
            @AuthenticationPrincipal CustomUserDetailsService userDetails) {

        List<ClassSubmitResponse> responses = classSubmitService.getAllWeeklySessionSubmissions(
                weeklySessionId, classId, userDetails.getUserId(), userDetails.getRole());

        return success(responses, "주차 수업 제출을 조회했습니다.");
    }

    // 합격 학생 수 조회 (교수용)
    @GetMapping("/task/{taskId}/passed-count")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(summary = "합격 학생 수", description = "특정 과제를 통과한 학생 수를 조회합니다. (교수 전용)")
    public ResponseEntity<ApiResponse<Long>> getPassedStudentCount(@Parameter(description = "과제 ID") @PathVariable Long taskId,
                                                                   @Parameter(description = "수업 ID") @RequestParam Long classId) {

        Long count = classSubmitService.getPassedStudentCount(taskId, classId);

        return success(count, "합격 학생 수를 조회했습니다.");
    }

    // 수업의 모든 제출 조회 (권한별 필터링)
    @GetMapping("/class/{classId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR', 'ADMIN')")
    @Operation(
        summary = "수업 제출 조회",
        description = """
            특정 수업의 제출을 조회합니다. 과제 성공률 계산에 사용됩니다.
            - 교수/관리자: 모든 학생의 제출 조회
            - 학생: 자신의 제출만 조회
            """
    )
    public ResponseEntity<ApiResponse<List<ClassSubmitResponse>>> getAllClassSubmissions(
            @Parameter(description = "수업 ID") @PathVariable Long classId,
            @AuthenticationPrincipal CustomUserDetailsService userDetails) {

        List<ClassSubmitResponse> responses = classSubmitService.getAllClassSubmissions(
                classId, userDetails.getUserId(), userDetails.getRole());

        return success(responses, "수업 제출을 조회했습니다.");
    }
}
