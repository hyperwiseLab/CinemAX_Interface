package com.cinemax.domain.testcase.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 테스트 케이스 요청 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "테스트 케이스 요청")
public class TestCaseRequest {

    @NotNull(message = "Task ID는 필수입니다.")
    @Schema(description = "과제 ID", example = "1", required = true)
    private Long taskId;

    @NotNull(message = "Cycle ID는 필수입니다.")
    @Schema(description = "사이클 ID", example = "1", required = true)
    private Long cycleId;

    @NotNull(message = "Curriculum ID는 필수입니다.")
    @Schema(description = "커리큘럼 ID", example = "1", required = true)
    private Long curId;

    @NotNull(message = "주차 번호는 필수입니다.")
    @Schema(description = "주차 번호", example = "1", required = true)
    private Integer weekNo;

    @NotNull(message = "Syntax ID는 필수입니다.")
    @Schema(description = "문법 ID", example = "1", required = true)
    private Long syntaxId;

    @NotBlank(message = "시작 코드는 필수입니다.")
    @Schema(description = "시작 코드 (템플릿)", required = true)
    private String startCode;

    @NotBlank(message = "테스트 코드는 필수입니다.")
    @Schema(description = "테스트 코드", required = true)
    private String testCode;

    @NotNull(message = "입력 데이터는 필수입니다. (입력이 필요 없는 경우 빈 문자열 \"\" 사용)")
    @Schema(
            description = """
                    학생 코드 실행 시 표준 입력(stdin)으로 제공될 값

                    - Scanner나 System.in으로 입력받는 경우: 입력값 작성 (예: "5" 또는 "3\\n5")
                    - print문만 사용하는 경우: 빈 문자열 "" 사용
                    - 여러 줄 입력: \\n으로 구분 (예: "3\\n5\\n7")
                    """,
            example = "5",
            required = true
    )
    private String inputText;

    @NotBlank(message = "예상 출력은 필수입니다.")
    @Schema(
            description = """
                    학생 코드가 출력해야 하는 정답 값

                    - System.out.print/println의 출력 결과와 비교
                    - 공백, 줄바꿈까지 정확히 일치해야 함
                    - 여러 줄 출력: \\n으로 구분
                    """,
            example = "120",
            required = true
    )
    private String expectedOutput;

    @Schema(
            description = """
                    이 테스트 케이스의 배점 (점수 비중)

                    - 기본값: 1
                    - 여러 테스트 케이스의 weight 합계가 과제의 총점이 됨
                    - 예시: TC1(10점) + TC2(20점) + TC3(30점) = 60점 만점
                    """,
            example = "10"
    )
    private Integer weight;
}
