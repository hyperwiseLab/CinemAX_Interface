package com.cinemax.domain.analysis.service.impl;

import com.cinemax.domain.analysis.dto.*;
import com.cinemax.domain.analysis.entity.AnalysisResult;
import com.cinemax.domain.analysis.repository.AnalysisResultRepository;
import com.cinemax.domain.analysis.service.AnalysisResultService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * AI 분석 결과 서비스 구현체
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AnalysisResultServiceImpl implements AnalysisResultService {

    private final AnalysisResultRepository analysisResultRepository;

    @Override
    @Transactional
    public AnalysisResultResponse saveAnalysisResult(AnalysisResultRequest request) {

        AnalysisResult analysisResult = AnalysisResult.builder()
                .userId(request.getUserId())
                .taskId(request.getTaskId())
                .submitId(request.getSubmitId())
                .classId(request.getClassId())
                .cycleId(request.getCycleId())
                .studentPrompt(request.getStudentPrompt())
                .studentCode(request.getStudentCode())
                .requestedAt(LocalDateTime.now())
                .llmResponse(request.getLlmResponse())
                .analyzedAt(LocalDateTime.now())
                .requirementsMet(request.getRequirementsMet())
                .codeQualityPass(request.getCodeQualityPass())
                .hasLogicError(request.getHasLogicError())
                .hasSecurityIssue(request.getHasSecurityIssue())
                .needsImprovement(request.getNeedsImprovement())
                .modelVersion(request.getModelVersion())
                .promptTokens(request.getPromptTokens())
                .completionTokens(request.getCompletionTokens())
                .totalTokens(request.getTotalTokens())
                .additionalNotes(request.getAdditionalNotes())
                .build();

        AnalysisResult saved = analysisResultRepository.save(analysisResult);
        return AnalysisResultResponse.from(saved);
    }

    // 분석 걸과 조히
    @Override
    public AnalysisResultResponse getAnalysisResult(Long analysisId) {

        AnalysisResult analysisResult = analysisResultRepository.findById(analysisId)
                .orElseThrow(() -> new IllegalArgumentException("분석 결과를 찾을 수 없습니다. ID: " + analysisId));

        return AnalysisResultResponse.from(analysisResult);
    }

    // 사용자 분석 히스토리 조회
    @Override
    public Page<AnalysisHistoryResponse> getUserHistory(Long userId, Pageable pageable) {

        Page<AnalysisResult> results = analysisResultRepository.findByUserId(userId, pageable);

        return results.map(AnalysisHistoryResponse::from);
    }

    // 사용자 + 과제별 분석 히스토리 조회
    @Override
    public Page<AnalysisHistoryResponse> getUserHistoryByTask(Long userId, Long taskId, Pageable pageable) {

        Page<AnalysisResult> results = analysisResultRepository.findByUserIdAndTaskId(userId, taskId, pageable);

        return results.map(AnalysisHistoryResponse::from);
    }

    // 사용자 + 사이클별 분석 히스토리 조회
    @Override
    public Page<AnalysisHistoryResponse> getUserHistoryByCycle(Long userId, Long cycleId, Pageable pageable) {

        Page<AnalysisResult> results = analysisResultRepository.findByUserIdAndCycleId(userId, cycleId, pageable);

        return results.map(AnalysisHistoryResponse::from);
    }

    // 사용자 기간별 분석 히스토리 조회
    @Override
    public Page<AnalysisHistoryResponse> getUserHistoryByDateRange(Long userId, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {

        List<AnalysisResult> results = analysisResultRepository.findByUserIdAndDateRange(userId, startDate, endDate);
        List<AnalysisHistoryResponse> responses = results.stream()
                .map(AnalysisHistoryResponse::from)
                .collect(Collectors.toList());

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), responses.size());

        return new PageImpl<>(
                responses.subList(start, end),
                pageable,
                responses.size()
        );
    }

    // 제출 ID로 분석 결과 조회
    @Override
    public AnalysisResultResponse getAnalysisResultBySubmitId(Long submitId) {

        List<AnalysisResult> results = analysisResultRepository.findBySubmitId(submitId);

        if (results.isEmpty()) {
            throw new IllegalArgumentException("해당 제출에 대한 분석 결과가 없습니다. submitId: " + submitId);
        }

        // 가장 최근 분석 결과 반환
        AnalysisResult latestResult = results.stream()
                .max((a, b) -> a.getRequestedAt().compareTo(b.getRequestedAt()))
                .orElseThrow();

        return AnalysisResultResponse.from(latestResult);
    }

    // 과제별 통계 조회
    @Override
    public AnalysisStatisticsResponse getTaskStatistics(Long taskId) {

        Long totalAnalyses = analysisResultRepository.countByTaskId(taskId);

        if (totalAnalyses == 0) {
            return AnalysisStatisticsResponse.builder()
                    .totalAnalyses(0L)
                    .requirementsMetRate(0.0)
                    .codeQualityPassRate(0.0)
                    .logicErrorRate(0.0)
                    .securityIssueRate(0.0)
                    .topIssues(new ArrayList<>())
                    .build();
        }

        Long requirementsMetCount = analysisResultRepository.countRequirementsMetByTaskId(taskId);
        Long codeQualityPassCount = analysisResultRepository.countCodeQualityPassByTaskId(taskId);
        Long logicErrorCount = (long) analysisResultRepository.findLogicErrorsByTaskId(taskId).size();
        Long securityIssueCount = (long) analysisResultRepository.findSecurityIssuesByTaskId(taskId).size();

        List<IssueCount> topIssues = new ArrayList<>();
        if (logicErrorCount > 0) {
            topIssues.add(IssueCount.builder()
                    .issueType("논리 오류")
                    .count(logicErrorCount)
                    .percentage((double) logicErrorCount / totalAnalyses * 100)
                    .build());
        }
        if (securityIssueCount > 0) {
            topIssues.add(IssueCount.builder()
                    .issueType("보안 이슈")
                    .count(securityIssueCount)
                    .percentage((double) securityIssueCount / totalAnalyses * 100)
                    .build());
        }

        return AnalysisStatisticsResponse.builder()
                .totalAnalyses(totalAnalyses)
                .requirementsMetRate((double) requirementsMetCount / totalAnalyses)
                .codeQualityPassRate((double) codeQualityPassCount / totalAnalyses)
                .logicErrorRate((double) logicErrorCount / totalAnalyses)
                .securityIssueRate((double) securityIssueCount / totalAnalyses)
                .topIssues(topIssues)
                .build();
    }

    @Override
    public AnalysisStatisticsResponse getClassStatistics(Long classId, Long cycleId) {
        log.info("수업별 통계 조회 - classId: {}, cycleId: {}", classId, cycleId);

        Page<AnalysisResult> results = analysisResultRepository.findByClassIdAndCycleId(
                classId,
                cycleId,
                Pageable.unpaged()
        );

        Long totalAnalyses = results.getTotalElements();

        if (totalAnalyses == 0) {
            return AnalysisStatisticsResponse.builder()
                    .totalAnalyses(0L)
                    .requirementsMetRate(0.0)
                    .codeQualityPassRate(0.0)
                    .logicErrorRate(0.0)
                    .securityIssueRate(0.0)
                    .topIssues(new ArrayList<>())
                    .build();
        }

        List<AnalysisResult> allResults = results.getContent();

        long requirementsMetCount = allResults.stream()
                .filter(ar -> Boolean.TRUE.equals(ar.getRequirementsMet()))
                .count();
        long codeQualityPassCount = allResults.stream()
                .filter(ar -> Boolean.TRUE.equals(ar.getCodeQualityPass()))
                .count();
        long logicErrorCount = allResults.stream()
                .filter(ar -> Boolean.TRUE.equals(ar.getHasLogicError()))
                .count();
        long securityIssueCount = allResults.stream()
                .filter(ar -> Boolean.TRUE.equals(ar.getHasSecurityIssue()))
                .count();

        List<IssueCount> topIssues = new ArrayList<>();
        if (logicErrorCount > 0) {
            topIssues.add(IssueCount.builder()
                    .issueType("논리 오류")
                    .count(logicErrorCount)
                    .percentage((double) logicErrorCount / totalAnalyses * 100)
                    .build());
        }
        if (securityIssueCount > 0) {
            topIssues.add(IssueCount.builder()
                    .issueType("보안 이슈")
                    .count(securityIssueCount)
                    .percentage((double) securityIssueCount / totalAnalyses * 100)
                    .build());
        }

        return AnalysisStatisticsResponse.builder()
                .totalAnalyses(totalAnalyses)
                .requirementsMetRate((double) requirementsMetCount / totalAnalyses)
                .codeQualityPassRate((double) codeQualityPassCount / totalAnalyses)
                .logicErrorRate((double) logicErrorCount / totalAnalyses)
                .securityIssueRate((double) securityIssueCount / totalAnalyses)
                .topIssues(topIssues)
                .build();
    }

    // 분석 결과 플래그 업데이트
    @Override
    @Transactional
    public AnalysisResultResponse updateAnalysisFlags(Long analysisId, Boolean requirementsMet, Boolean codeQualityPass,
                                                      Boolean hasLogicError, Boolean hasSecurityIssue, Boolean needsImprovement) {

        AnalysisResult analysisResult = analysisResultRepository.findById(analysisId)
                .orElseThrow(() -> new IllegalArgumentException("분석 결과를 찾을 수 없습니다. ID: " + analysisId));

        analysisResult.updateAnalysisFlags(
                requirementsMet,
                codeQualityPass,
                hasLogicError,
                hasSecurityIssue,
                needsImprovement
        );

        return AnalysisResultResponse.from(analysisResult);
    }

    // 분석 결과 삭제
    @Override
    @Transactional
    public void deleteAnalysisResult(Long analysisId) {

        if (!analysisResultRepository.existsById(analysisId)) {
            throw new IllegalArgumentException("분석 결과를 찾을 수 없습니다. ID: " + analysisId);
        }

        analysisResultRepository.deleteById(analysisId);
    }

    // 토큰 사용량 통계 조회
    @Override
    public TokenUsageStatisticsResponse getTokenUsageStatistics(LocalDateTime startDate, LocalDateTime endDate, Long classId, Long cycleId) {

        // 기본 통계 조회
        Long totalAnalyses = analysisResultRepository.countByDateRange(startDate, endDate);
        Long totalPromptTokens = analysisResultRepository.getTotalPromptTokensByDateRange(startDate, endDate);
        Long totalCompletionTokens = analysisResultRepository.getTotalCompletionTokensByDateRange(startDate, endDate);
        Long totalTokens = analysisResultRepository.getTotalTokenUsageByDateRange(startDate, endDate);

        // null 체크 및 기본값 설정
        totalAnalyses = totalAnalyses != null ? totalAnalyses : 0L;
        totalPromptTokens = totalPromptTokens != null ? totalPromptTokens : 0L;
        totalCompletionTokens = totalCompletionTokens != null ? totalCompletionTokens : 0L;
        totalTokens = totalTokens != null ? totalTokens : 0L;

        // 평균 계산
        double averagePromptTokens = totalAnalyses > 0 ? (double) totalPromptTokens / totalAnalyses : 0.0;
        double averageCompletionTokens = totalAnalyses > 0 ? (double) totalCompletionTokens / totalAnalyses : 0.0;
        double averageTokensPerAnalysis = totalAnalyses > 0 ? (double) totalTokens / totalAnalyses : 0.0;

        // 비용 추정 (Gemini 2.5 Flash 기준 - 참고용)
        double inputCost = (totalPromptTokens / 1_000_000.0) * 0.075;
        double outputCost = (totalCompletionTokens / 1_000_000.0) * 0.30;
        double totalCost = inputCost + outputCost;

        CostEstimate costEstimate = CostEstimate.builder()
                .estimatedCostUSD(totalCost)
                .pricingModel("Gemini 2.5 Flash")
                .note("추정 비용입니다. 실제 비용은 Google Cloud 콘솔에서 확인하세요.")
                .build();

        // 모델별 사용량 (간단히 전체를 gemini-2.5-flash로 가정)
        ModelTokenUsage modelTokenUsage = ModelTokenUsage.builder()
                .modelVersion("gemini-2.5-flash")
                .analysisCount(totalAnalyses)
                .totalTokens(totalTokens)
                .averageTokens(averageTokensPerAnalysis)
                .build();

        return TokenUsageStatisticsResponse.builder()
                .totalAnalyses(totalAnalyses)
                .totalPromptTokens(totalPromptTokens)
                .totalCompletionTokens(totalCompletionTokens)
                .totalTokens(totalTokens)
                .averagePromptTokens(averagePromptTokens)
                .averageCompletionTokens(averageCompletionTokens)
                .averageTokensPerAnalysis(averageTokensPerAnalysis)
                .modelTokenUsage(modelTokenUsage)
                .periodStart(startDate)
                .periodEnd(endDate)
                .costEstimate(costEstimate)
                .build();
    }

    // 전체 토큰 사용량 통계 조회
    @Override
    public TokenUsageStatisticsResponse getTotalTokenUsageStatistics() {

        Long totalAnalyses = analysisResultRepository.count();
        Long totalPromptTokens = analysisResultRepository.getTotalPromptTokens();
        Long totalCompletionTokens = analysisResultRepository.getTotalCompletionTokens();
        Long totalTokens = analysisResultRepository.getTotalTokenUsage();

        // null 체크 및 기본값 설정
        totalAnalyses = totalAnalyses != null ? totalAnalyses : 0L;
        totalPromptTokens = totalPromptTokens != null ? totalPromptTokens : 0L;
        totalCompletionTokens = totalCompletionTokens != null ? totalCompletionTokens : 0L;
        totalTokens = totalTokens != null ? totalTokens : 0L;

        // 평균 계산
        double averagePromptTokens = totalAnalyses > 0 ? (double) totalPromptTokens / totalAnalyses : 0.0;
        double averageCompletionTokens = totalAnalyses > 0 ? (double) totalCompletionTokens / totalAnalyses : 0.0;
        double averageTokensPerAnalysis = totalAnalyses > 0 ? (double) totalTokens / totalAnalyses : 0.0;

        // 비용 추정
        double inputCost = (totalPromptTokens / 1_000_000.0) * 0.075;
        double outputCost = (totalCompletionTokens / 1_000_000.0) * 0.30;
        double totalCost = inputCost + outputCost;

        CostEstimate costEstimate = CostEstimate.builder()
                .estimatedCostUSD(totalCost)
                .pricingModel("Gemini 2.5 Flash")
                .note("추정 비용입니다. 실제 비용은 Google Cloud 콘솔에서 확인하세요.")
                .build();

        ModelTokenUsage modelTokenUsage = ModelTokenUsage.builder()
                .modelVersion("gemini-2.5-flash")
                .analysisCount(totalAnalyses)
                .totalTokens(totalTokens)
                .averageTokens(averageTokensPerAnalysis)
                .build();

        return TokenUsageStatisticsResponse.builder()
                .totalAnalyses(totalAnalyses)
                .totalPromptTokens(totalPromptTokens)
                .totalCompletionTokens(totalCompletionTokens)
                .totalTokens(totalTokens)
                .averagePromptTokens(averagePromptTokens)
                .averageCompletionTokens(averageCompletionTokens)
                .averageTokensPerAnalysis(averageTokensPerAnalysis)
                .modelTokenUsage(modelTokenUsage)
                .periodStart(null)
                .periodEnd(null)
                .costEstimate(costEstimate)
                .build();
    }
}
