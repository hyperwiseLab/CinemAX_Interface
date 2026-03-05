package com.cinemax.domain.analysis.exception;

/**
 * 잘못된 분석 요청 시 발생하는 예외
 */
public class InvalidAnalysisRequestException extends RuntimeException {

    public InvalidAnalysisRequestException(String message) {
        super(message);
    }

    public InvalidAnalysisRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
