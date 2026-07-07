package com.cinemax.infrastructure.openai.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAI 설정 클래스
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(OpenAiProperties.class)
@ConditionalOnProperty(name = "openai.enabled", havingValue = "true", matchIfMissing = false)
public class OpenAiConfig {

    private final OpenAiProperties properties;

    // OpenAI Chat Model Bean 생성 (API 키가 없으면 null 반환하여 기능 비활성화)
    @Bean
    public OpenAiChatModel openAiChatModel() {
        String apiKey = properties.getApiKey();
        if (apiKey == null || apiKey.isBlank() || apiKey.equals("your-openai-api-key")) {
            log.warn("OpenAI API 키가 설정되지 않았습니다 - AI 기능이 비활성화됩니다.");
            return null;
        }

        OpenAiApi openAiApi = new OpenAiApi(apiKey);

        OpenAiChatOptions options = OpenAiChatOptions.builder()
                .withModel(properties.getModel())
                .withTemperature(properties.getTemperature())
                .withMaxTokens(properties.getMaxTokens())
                .withTopP(properties.getTopP())
                .build();

        log.info("OpenAI ChatModel initialized successfully. model={}", properties.getModel());
        return new OpenAiChatModel(openAiApi, options);
    }
}
