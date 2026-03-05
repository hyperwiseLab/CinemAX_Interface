package com.cinemax.domain.testcase.controller;

import com.cinemax.core.controller.BaseController;
import com.cinemax.core.dto.response.ApiResponse;
import com.cinemax.domain.testcase.dto.TestCaseRequest;
import com.cinemax.domain.testcase.dto.TestCaseResponse;
import com.cinemax.domain.testcase.service.TestCaseService;
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
 * 테스트 케이스 관리 API Controller
 */
@Slf4j
@RestController
@RequestMapping("/testcases")
@RequiredArgsConstructor
@Tag(name = "TestCase", description = "테스트 케이스 관리 API")
public class TestCaseController extends BaseController {

    private final TestCaseService testCaseService;

    // 테스트 케이스 생성
    @PostMapping
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(
            summary = "테스트 케이스 생성",
            description = """
                    새로운 테스트 케이스를 생성합니다. (교수/관리자 전용)

                    ## 필드 설명

                    ### 1. startCode (시작 코드)
                    학생이 코딩을 시작할 때 에디터에 표시되는 초기 코드 템플릿
                    - 빈 메인 메서드나 클래스 구조를 제공
                    - 학생은 이 템플릿을 수정하거나 채워서 과제 완성

                    ### 2. testCode (테스트 코드)
                    실제 테스트를 수행하는 검증 코드 (학생에게는 비공개)
                    - 학생 코드의 정확성을 검증하는 로직
                    - 학생은 볼 수 없음

                    ### 3. inputText (입력 데이터)
                    학생 코드 실행 시 표준 입력(stdin)으로 제공될 값
                    - **입력이 있는 경우**: "5" 또는 여러 줄 입력 시 "3\\n5\\n7"
                    - **입력이 없는 경우** (print문만 사용): 빈 문자열 ""
                    - Scanner나 System.in으로 읽는 값

                    ### 4. expectedOutput (정답 출력)
                    학생 코드가 출력해야 하는 정답 값
                    - System.out.print/println의 출력과 비교
                    - 공백, 줄바꿈까지 정확히 일치해야 통과

                    ### 5. weight (배점)
                    이 테스트 케이스의 점수 비중
                    - 기본값 1점
                    - 모든 테스트 케이스의 weight 합계 = 과제 총점
                    - 예: TC1(10) + TC2(20) + TC3(30) = 60점 만점

                    ## 예시 1: 입력이 있는 경우 (두 수의 합)
                    ```json
                    {
                      "taskId": 1,
                      "cycleId": 1,
                      "curId": 1,
                      "weekNo": 1,
                      "syntaxId": 1,
                      "startCode": "import java.util.Scanner;\\npublic class Main {\\n  public static void main(String[] args) {\\n    // 여기에 코드 작성\\n  }\\n}",
                      "testCode": "// 테스트 검증 코드",
                      "inputText": "3\\n5",
                      "expectedOutput": "8",
                      "weight": 10
                    }
                    ```

                    ## 예시 2: 입력이 없는 경우 (Hello World 출력)
                    ```json
                    {
                      "taskId": 2,
                      "cycleId": 1,
                      "curId": 1,
                      "weekNo": 1,
                      "syntaxId": 1,
                      "startCode": "public class Main {\\n  public static void main(String[] args) {\\n    // 여기에 코드 작성\\n  }\\n}",
                      "testCode": "// 테스트 검증 코드",
                      "inputText": "",
                      "expectedOutput": "Hello World",
                      "weight": 5
                    }
                    ```

                    ## 예시 3: 팩토리얼 계산 (5! = 120)
                    ```json
                    {
                      "taskId": 3,
                      "cycleId": 1,
                      "curId": 1,
                      "weekNo": 2,
                      "syntaxId": 1,
                      "startCode": "import java.util.Scanner;\\npublic class Main {\\n  public static void main(String[] args) {\\n    Scanner sc = new Scanner(System.in);\\n    // 여기에 코드 작성\\n  }\\n}",
                      "testCode": "// 테스트 검증 코드",
                      "inputText": "5",
                      "expectedOutput": "120",
                      "weight": 15
                    }
                    ```
                    """
    )
    public ResponseEntity<ApiResponse<TestCaseResponse>> createTestCase(
            @Valid @RequestBody TestCaseRequest request) {

        TestCaseResponse response = testCaseService.createTestCase(request);

        return created(response, "테스트 케이스가 성공적으로 생성되었습니다.");
    }

    // 과제의 모든 테스트 케이스 조회 (교수용 - 전체 정보 포함)
    @GetMapping("/task/{taskId}")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(
            summary = "과제의 모든 테스트 케이스 조회",
            description = """
                    특정 과제의 모든 테스트 케이스를 조회합니다. (교수/관리자 전용)
                    testCode를 포함한 전체 정보를 반환합니다.

                    ## Query Parameters
                    - **cycleId**: 사이클 ID (필수)
                    """
    )
    public ResponseEntity<ApiResponse<List<TestCaseResponse>>> getAllTestCases(
            @Parameter(description = "과제 ID") @PathVariable Long taskId,
            @Parameter(description = "사이클 ID") @RequestParam Long cycleId) {

        List<TestCaseResponse> responses = testCaseService.getAllTestCases(taskId, cycleId);

        return success(responses, "테스트 케이스 목록 조회 성공");
    }

    // 과제의 공개 테스트 케이스 조회 (학생용 - testCode 제외)
    @GetMapping("/task/{taskId}/public")
    @Operation(
            summary = "과제의 공개 테스트 케이스 조회",
            description = """
                    특정 과제의 공개 테스트 케이스를 조회합니다. (학생용)
                    testCode는 제외되고, inputText와 expectedOutput만 포함됩니다.

                    학생이 코드를 제출하기 전에 이 API로 테스트 케이스 목록을 가져와서
                    각 테스트 케이스를 실행하고 결과를 비교합니다.

                    ## Query Parameters
                    - **cycleId**: 사이클 ID (필수)

                    ## 사용 예시
                    1. 이 API로 테스트 케이스 목록 조회
                    2. 각 테스트 케이스의 inputText를 입력으로 학생 코드 실행
                    3. actualOutput과 expectedOutput 비교
                    4. 결과를 testCaseResults에 담아 POST /code/submit으로 제출
                    """
    )
    public ResponseEntity<ApiResponse<List<TestCaseResponse>>> getPublicTestCases(
            @Parameter(description = "과제 ID") @PathVariable Long taskId,
            @Parameter(description = "사이클 ID") @RequestParam Long cycleId) {

        List<TestCaseResponse> responses = testCaseService.getPublicTestCases(taskId, cycleId);

        return success(responses, "공개 테스트 케이스 조회 성공");
    }

    // 특정 테스트 케이스 조회
    @GetMapping("/{testCaseId}")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(
            summary = "특정 테스트 케이스 조회",
            description = "테스트 케이스 ID로 단일 테스트 케이스를 조회합니다. (교수/관리자 전용)"
    )
    public ResponseEntity<ApiResponse<TestCaseResponse>> getTestCase(
            @Parameter(description = "테스트 케이스 ID") @PathVariable Long testCaseId) {

        TestCaseResponse response = testCaseService.getTestCase(testCaseId);

        return success(response, "테스트 케이스 조회 성공");
    }

    // 시작 코드 조회
    @GetMapping("/task/{taskId}/start-code")
    @Operation(
            summary = "시작 코드 조회",
            description = """
                    과제의 시작 코드(템플릿)를 조회합니다.
                    학생이 코드 작성을 시작할 때 에디터에 표시할 초기 코드를 가져옵니다.

                    ## Query Parameters
                    - **cycleId**: 사이클 ID (필수)
                    """
    )
    public ResponseEntity<ApiResponse<String>> getStartCode(
            @Parameter(description = "과제 ID") @PathVariable Long taskId,
            @Parameter(description = "사이클 ID") @RequestParam Long cycleId) {

        String startCode = testCaseService.getStartCode(taskId, cycleId);

        return success(startCode, "시작 코드 조회 성공");
    }

    // 테스트 케이스 개수 조회
    @GetMapping("/task/{taskId}/count")
    @Operation(
            summary = "테스트 케이스 개수 조회",
            description = """
                    특정 과제의 테스트 케이스 개수를 조회합니다.

                    ## Query Parameters
                    - **cycleId**: 사이클 ID (필수)
                    """
    )
    public ResponseEntity<ApiResponse<Long>> getTestCaseCount(
            @Parameter(description = "과제 ID") @PathVariable Long taskId,
            @Parameter(description = "사이클 ID") @RequestParam Long cycleId) {

        Long count = testCaseService.getTestCaseCount(taskId, cycleId);

        return success(count, "테스트 케이스 개수 조회 성공");
    }

    // 총 가중치 조회
    @GetMapping("/task/{taskId}/total-weight")
    @Operation(
            summary = "총 가중치 조회",
            description = """
                    특정 과제의 모든 테스트 케이스 가중치 합계를 조회합니다.
                    점수 계산 시 사용됩니다.

                    ## Query Parameters
                    - **cycleId**: 사이클 ID (필수)
                    """
    )
    public ResponseEntity<ApiResponse<Integer>> getTotalWeight(
            @Parameter(description = "과제 ID") @PathVariable Long taskId,
            @Parameter(description = "사이클 ID") @RequestParam Long cycleId) {

        Integer totalWeight = testCaseService.getTotalWeight(taskId, cycleId);

        return success(totalWeight, "총 가중치 조회 성공");
    }

    // 테스트 케이스 수정
    @PutMapping("/{testCaseId}")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(
            summary = "테스트 케이스 수정",
            description = "테스트 케이스 정보를 수정합니다. (교수/관리자 전용)"
    )
    public ResponseEntity<ApiResponse<TestCaseResponse>> updateTestCase(
            @Parameter(description = "테스트 케이스 ID") @PathVariable Long testCaseId,
            @Valid @RequestBody TestCaseRequest request) {

        TestCaseResponse response = testCaseService.updateTestCase(testCaseId, request);

        return success(response, "테스트 케이스가 성공적으로 수정되었습니다.");
    }

    // 테스트 케이스 삭제
    @DeleteMapping("/{testCaseId}")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(
            summary = "테스트 케이스 삭제",
            description = "테스트 케이스를 삭제합니다. (교수/관리자 전용)"
    )
    public ResponseEntity<ApiResponse<Void>> deleteTestCase(
            @Parameter(description = "테스트 케이스 ID") @PathVariable Long testCaseId) {

        testCaseService.deleteTestCase(testCaseId);

        return success(null, "테스트 케이스가 성공적으로 삭제되었습니다.");
    }
}
