package com.cinemax.infrastructure.openai.exception;

/**
 * OpenAI 관련 예외
 */
public class OpenAiException extends RuntimeException {

    public OpenAiException(String message) {
        super(message);
    }

    public OpenAiException(String message, Throwable cause) {
        super(message, cause);
    }

    public OpenAiException(Throwable cause) {
        super(cause);
    }
}
