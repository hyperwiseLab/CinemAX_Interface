package com.cinemax.domain.analysis.entity;

import com.cinemax.core.entity.BaseTimeEntity;
import com.cinemax.domain.classes.entity.ClassSubmit;
import com.cinemax.domain.task.entity.Task;
import com.cinemax.domain.user.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * AI 분석 결과 엔티티
 * 학생의 과제 제출에 대한 LLM 분석 결과를 저장
 */
@Entity
@Table(name = "TBL_ANALYSIS_RESULT", indexes = {
        @Index(name = "idx_analysis_user_task", columnList = "USER_ID, TASK_ID"),
        @Index(name = "idx_analysis_submit", columnList = "SUBMIT_ID"),
        @Index(name = "idx_analysis_created", columnList = "CREATE_DT")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AnalysisResult extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ANALYSIS_ID")
    private Long analysisId;

    // ===== 연관 관계 =====
    @Column(name = "USER_ID", nullable = false)
    private Long userId;

    @Column(name = "TASK_ID", nullable = false)
    private Long taskId;

    @Column(name = "SUBMIT_ID")
    private Long submitId;

    @Column(name = "CLASS_ID")
    private Long classId;

    @Column(name = "CYCLE_ID")
    private Long cycleId;

    // ===== 분석 요청 정보 =====
    @Lob
    @Column(name = "STUDENT_PROMPT", columnDefinition = "TEXT")
    private String studentPrompt;

    @Lob
    @Column(name = "STUDENT_CODE", columnDefinition = "TEXT")
    private String studentCode;

    @Column(name = "REQUESTED_AT", nullable = false)
    private LocalDateTime requestedAt;

    // ===== LLM 응답 정보 =====
    @Lob
    @Column(name = "LLM_RESPONSE", columnDefinition = "TEXT")
    private String llmResponse;

    @Column(name = "ANALYZED_AT")
    private LocalDateTime analyzedAt;

    // ===== 분석 결과 (T/F 플래그) =====
    @Column(name = "REQUIREMENTS_MET")
    private Boolean requirementsMet;

    @Column(name = "CODE_QUALITY_PASS")
    private Boolean codeQualityPass;

    @Column(name = "HAS_LOGIC_ERROR")
    private Boolean hasLogicError;

    @Column(name = "HAS_SECURITY_ISSUE")
    private Boolean hasSecurityIssue;

    @Column(name = "NEEDS_IMPROVEMENT")
    private Boolean needsImprovement;

    // ===== 메타 정보 =====
    @Column(name = "MODEL_VERSION", length = 100)
    private String modelVersion;

    @Column(name = "PROMPT_TOKENS")
    private Integer promptTokens;

    @Column(name = "COMPLETION_TOKENS")
    private Integer completionTokens;

    @Column(name = "TOTAL_TOKENS")
    private Integer totalTokens;

    // ===== 추가 정보 =====
    @Column(name = "ADDITIONAL_NOTES", columnDefinition = "TEXT")
    private String additionalNotes;

    // ===== 연관 관계 매핑 =====
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", insertable = false, updatable = false)
    private User user;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TASK_ID", insertable = false, updatable = false)
    private Task task;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SUBMIT_ID", insertable = false, updatable = false)
    private ClassSubmit classSubmit;

    @Builder
    public AnalysisResult(Long userId, Long taskId, Long submitId, Long classId, Long cycleId,
                          String studentPrompt, String studentCode, LocalDateTime requestedAt,
                          String llmResponse, LocalDateTime analyzedAt,
                          Boolean requirementsMet, Boolean codeQualityPass, Boolean hasLogicError,
                          Boolean hasSecurityIssue, Boolean needsImprovement,
                          String modelVersion, Integer promptTokens, Integer completionTokens,
                          Integer totalTokens, String additionalNotes) {
        this.userId = userId;
        this.taskId = taskId;
        this.submitId = submitId;
        this.classId = classId;
        this.cycleId = cycleId;
        this.studentPrompt = studentPrompt;
        this.studentCode = studentCode;
        this.requestedAt = requestedAt != null ? requestedAt : LocalDateTime.now();
        this.llmResponse = llmResponse;
        this.analyzedAt = analyzedAt != null ? analyzedAt : LocalDateTime.now();
        this.requirementsMet = requirementsMet;
        this.codeQualityPass = codeQualityPass;
        this.hasLogicError = hasLogicError;
        this.hasSecurityIssue = hasSecurityIssue;
        this.needsImprovement = needsImprovement;
        this.modelVersion = modelVersion;
        this.promptTokens = promptTokens;
        this.completionTokens = completionTokens;
        this.totalTokens = totalTokens;
        this.additionalNotes = additionalNotes;
    }

    /**
     * 정적 팩토리 메서드 - 기본 분석 결과 생성
     */
    public static AnalysisResult create(Long userId, Long taskId, Long submitId,
                                        String studentCode, String llmResponse,
                                        Boolean requirementsMet, Boolean codeQualityPass,
                                        String modelVersion) {
        return AnalysisResult.builder()
                .userId(userId)
                .taskId(taskId)
                .submitId(submitId)
                .studentCode(studentCode)
                .llmResponse(llmResponse)
                .requestedAt(LocalDateTime.now())
                .analyzedAt(LocalDateTime.now())
                .requirementsMet(requirementsMet)
                .codeQualityPass(codeQualityPass)
                .modelVersion(modelVersion)
                .build();
    }

    /**
     * 분석 결과 업데이트
     */
    public void updateAnalysisFlags(Boolean requirementsMet, Boolean codeQualityPass,
                                    Boolean hasLogicError, Boolean hasSecurityIssue,
                                    Boolean needsImprovement) {
        this.requirementsMet = requirementsMet;
        this.codeQualityPass = codeQualityPass;
        this.hasLogicError = hasLogicError;
        this.hasSecurityIssue = hasSecurityIssue;
        this.needsImprovement = needsImprovement;
    }

    /**
     * 토큰 사용량 업데이트
     */
    public void updateTokenUsage(Integer promptTokens, Integer completionTokens) {
        this.promptTokens = promptTokens;
        this.completionTokens = completionTokens;
        this.totalTokens = (promptTokens != null ? promptTokens : 0) + (completionTokens != null ? completionTokens : 0);
    }
}
