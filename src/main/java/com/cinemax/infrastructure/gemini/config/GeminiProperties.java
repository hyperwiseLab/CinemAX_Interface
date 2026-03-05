package com.cinemax.infrastructure.gemini.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Gemini AI 설정 프로퍼티
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "gemini")
public class GeminiProperties {

    // Gemini API 키
    private String apiKey;

    // GCP 프로젝트 ID
    private String projectId;

    // GCP 리전 (예: us-central1)
    private String location = "us-central1";

    // 사용할 Gemini 모델 (Spring AI 1.0.0-M4 기준 기본값: gemini-pro)
    private String model = "gemini-2.5-flash";

    /**
     * 생성 온도 (0.0 ~ 2.0)
     * 낮을수록 결정적, 높을수록 창의적
     */
    private Double temperature = 0.7;

    // 최대 출력 토큰 수
    private Integer maxTokens = 2048;

    // Top-P 샘플링 파라미터
    private Double topP = 0.95;

    // Top-K 샘플링 파라미터
    private Integer topK = 40;

    // 인증 파일 경로 (선택사항, 기본값: keys.json)
    private String credentialsPath;
}
