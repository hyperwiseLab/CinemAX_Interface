package com.cinemax.domain.testcase.entity;

import com.cinemax.core.entity.BaseTimeEntity;
import com.cinemax.domain.task.entity.Task;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "TBL_TEST_CASE")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TestCase extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TEST_CASE_ID")
    private Long testCaseId;

    @Column(name = "TASK_ID")
    private Long taskId;

    @Column(name = "CYCLE_ID")
    private Long cycleId;

    @Column(name = "START_CODE", nullable = false, columnDefinition = "MEDIUMTEXT")
    private String startCode;

    @Column(name = "TEST_CODE", nullable = false, columnDefinition = "MEDIUMTEXT")
    private String testCode;

    @Column(name = "INPUT_TEXT", nullable = false, length = 500)
    private String inputText;

    @Column(name = "EXPECTED_OUTPUT", nullable = false, length = 500)
    private String expectedOutput;

    @Column(name = "WEIGHT", nullable = false)
    private Integer weight = 1;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TASK_ID", insertable = false, updatable = false)
    private Task task;

    // TestCase 생성 팩토리 메서드
    @Builder
    public static TestCase create(Long taskId, Long cycleId, String startCode, String testCode, String inputText,
                                  String expectedOutput, Integer weight) {
        TestCase testCase = new TestCase();
        testCase.taskId = taskId;
        testCase.cycleId = cycleId;
        testCase.startCode = startCode;
        testCase.testCode = testCode;
        testCase.inputText = inputText;
        testCase.expectedOutput = expectedOutput;
        testCase.weight = weight != null ? weight : 1;
        return testCase;
    }

    /**
     * TestCase 내용 수정
     */
    public void update(String startCode, String testCode, String inputText,
                      String expectedOutput, Integer weight) {
        if (startCode != null) {
            this.startCode = startCode;
        }
        if (testCode != null) {
            this.testCode = testCode;
        }
        if (inputText != null) {
            this.inputText = inputText;
        }
        if (expectedOutput != null) {
            this.expectedOutput = expectedOutput;
        }
        if (weight != null) {
            this.weight = weight;
        }
    }
}
