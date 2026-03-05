package com.cinemax.domain.analysis.exception;

/**
 * 분석 결과를 찾을 수 없을 때 발생하는 예외
 */
public class AnalysisResultNotFoundException extends RuntimeException {

    public AnalysisResultNotFoundException(String message) {
        super(message);
    }

    public AnalysisResultNotFoundException(Long analysisId) {
        super("분석 결과를 찾을 수 없습니다. ID: " + analysisId);
    }
}
