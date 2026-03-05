package com.cinemax.domain.code.dto;

import com.cinemax.domain.testcase.dto.TestCaseResultRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 코드 제출 요청 DTO
 *
 * <h3>프론트엔드에서 코드 실행 후 테스트 결과와 함께 제출</h3>
 *
 * <p><b>testCaseResults 산출 프로세스:</b></p>
 * <ol>
 *   <li>GET /testcases/task/{taskId}?cycleId={cycleId} 로 테스트 케이스 목록 조회</li>
 *   <li>각 테스트 케이스마다 학생 코드 실행:
 *     <ul>
 *       <li>inputText를 표준 입력으로 제공</li>
 *       <li>코드 실행 결과를 actualOutput에 저장</li>
 *       <li>expectedOutput과 비교하여 passed 판정 (대소문자, 공백 정규화 후 비교)</li>
 *       <li>에러 발생 시 errorMessage에 상세 내용 저장</li>
 *       <li>실행 시간(ms) 측정하여 executionTime에 저장</li>
 *     </ul>
 *   </li>
 *   <li>모든 테스트 결과를 testCaseResults 배열에 담아 제출</li>
 * </ol>
 *
 * <p><b>백엔드 점수 계산:</b></p>
 * <ul>
 *   <li>획득 점수 = (통과한 테스트 케이스의 가중치 합) / (전체 테스트 케이스의 가중치 합) × 100</li>
 *   <li>모든 테스트 통과 시 result = true, 하나라도 실패 시 result = false</li>
 * </ul>
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "코드 제출 요청")
public class CodeSubmitRequest {

    @NotNull(message = "과제 ID는 필수입니다")
    @Schema(description = "과제 ID", example = "1")
    private Long taskId;

    @NotNull(message = "수업 ID는 필수입니다")
    @Schema(description = "수업 ID", example = "1")
    private Long classId;

    @NotNull(message = "주차 수업 ID는 필수입니다")
    @Schema(description = "주차 수업 ID", example = "1")
    private Long weeklySessionId;

    @NotNull(message = "사이클 ID는 필수입니다")
    @Schema(description = "사이클 ID", example = "1")
    private Long cycleId;

    @NotNull(message = "커리큘럼 ID는 필수입니다")
    @Schema(description = "커리큘럼 ID", example = "1")
    private Long curId;

    @NotNull(message = "주차 번호는 필수입니다")
    @Schema(description = "주차 번호", example = "1")
    private Integer weekNo;

    @NotNull(message = "문법 ID는 필수입니다")
    @Schema(description = "문법 ID", example = "1")
    private Long syntaxId;

    @NotBlank(message = "언어는 필수입니다")
    @Schema(description = "프로그래밍 언어", example = "java")
    private String language;

    @NotBlank(message = "코드는 필수입니다")
    @Schema(description = "제출할 코드")
    private String code;

    @NotNull(message = "테스트 케이스 결과는 필수입니다")
    @Schema(
        description = """
            테스트 케이스별 실행 결과 (프론트엔드에서 코드 실행 후 산출)

            각 테스트 케이스마다:
            - testCaseId: 테스트 케이스 ID
            - passed: 테스트 통과 여부 (expectedOutput과 actualOutput 비교)
            - input: 테스트 입력값 (inputText)
            - expectedOutput: 기대 출력값
            - actualOutput: 실제 코드 실행 결과
            - errorMessage: 컴파일/런타임 에러 메시지 (있는 경우)
            - executionTime: 실행 시간(ms)
            - weight: 가중치 (점수 계산에 사용)
            """,
        example = """
            [
              {
                "testCaseId": 1,
                "passed": true,
                "input": "",
                "expectedOutput": "Hello World",
                "actualOutput": "Hello World",
                "errorMessage": null,
                "executionTime": 125,
                "weight": 50
              }
            ]
            """
    )
    private List<TestCaseResultRequest> testCaseResults;
}
