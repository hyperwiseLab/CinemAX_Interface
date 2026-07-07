package com.cinemax.infrastructure.openai.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * OpenAI 설정 프로퍼티
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "openai")
public class OpenAiProperties {

    // OpenAI API 키
    private String apiKey;

    // 사용할 모델 (예: gpt-5-mini)
    private String model = "gpt-5-mini";

    /**
     * 생성 온도 (0.0 ~ 2.0)
     * 낮을수록 결정적, 높을수록 창의적
     */
    private Double temperature = 0.7;

    // 최대 출력 토큰 수
    private Integer maxTokens = 2048;

    // Top-P 샘플링 파라미터
    private Double topP = 0.95;
}
