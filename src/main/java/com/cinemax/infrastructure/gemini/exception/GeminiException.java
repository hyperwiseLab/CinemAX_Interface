package com.cinemax.infrastructure.gemini.exception;

/**
 * Gemini AI 관련 예외
 */
public class GeminiException extends RuntimeException {

    public GeminiException(String message) {
        super(message);
    }

    public GeminiException(String message, Throwable cause) {
        super(message, cause);
    }

    public GeminiException(Throwable cause) {
        super(cause);
    }
}
