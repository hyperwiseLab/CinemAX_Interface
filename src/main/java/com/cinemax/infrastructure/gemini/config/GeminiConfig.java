package com.cinemax.infrastructure.gemini.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.vertexai.VertexAI;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.vertexai.gemini.VertexAiGeminiChatModel;
import org.springframework.ai.vertexai.gemini.VertexAiGeminiChatOptions;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;

/**
 * Gemini AI 설정 클래스
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(GeminiProperties.class)
@ConditionalOnProperty(name = "gemini.enabled", havingValue = "true", matchIfMissing = false)
public class GeminiConfig {

    private final GeminiProperties properties;

    // VertexAI 클라이언트 Bean 생성
    @Bean
    public VertexAI vertexAI() {
        try {
            GoogleCredentials credentials = loadCredentials();

            VertexAI vertexAI = new VertexAI.Builder()
                    .setProjectId(properties.getProjectId())
                    .setLocation(properties.getLocation())
                    .setCredentials(credentials)
                    .build();

            return vertexAI;
        } catch (Exception e) {
            log.warn("Vertex AI 초기화 실패 - Gemini 기능이 비활성화됩니다. 원인: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Google Cloud 인증 정보 로드
     * 1. GOOGLE_APPLICATION_CREDENTIALS 환경 변수 우선 확인
     * 2. 없으면 프로젝트 루트의 keys.json 확인
     */
    private GoogleCredentials loadCredentials() throws IOException {
        String credentialsPath = null;
        File credentialsFile = null;

        // 1. GOOGLE_APPLICATION_CREDENTIALS 환경 변수 확인
        String envCredentialsPath = System.getenv("GOOGLE_APPLICATION_CREDENTIALS");
        if (envCredentialsPath != null && !envCredentialsPath.isBlank()) {
            credentialsFile = new File(envCredentialsPath);
            if (credentialsFile.exists() && credentialsFile.isFile()) {
                credentialsPath = envCredentialsPath;
            }
        }

        // 2. 환경 변수가 없거나 파일이 없으면 프로젝트 루트의 keys.json 확인
        if (credentialsPath == null) {
            String defaultPath = Paths.get(System.getProperty("user.dir"), "keys.json").toString();
            File defaultFile = new File(defaultPath);
            if (defaultFile.exists() && defaultFile.isFile()) {
                credentialsPath = defaultPath;
                credentialsFile = defaultFile;
            }
        }

        // 3. 둘 다 없으면 에러
        if (credentialsPath == null || credentialsFile == null || !credentialsFile.exists() || !credentialsFile.isFile()) {
            String errorMsg = String.format(
                    "keys.json not found. Checked:\n" +
                            "  1. GOOGLE_APPLICATION_CREDENTIALS: %s\n" +
                            "  2. Default path: %s/keys.json",
                    envCredentialsPath != null ? envCredentialsPath : "not set",
                    System.getProperty("user.dir")
            );
            log.error(errorMsg);
            throw new IOException(errorMsg);
        }


        // Vertex AI 호출에 필요한 cloud-platform 스코프로 제한
        GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(credentialsFile))
                .createScoped("https://www.googleapis.com/auth/cloud-platform");

        credentials.getRequestMetadata();
        properties.getProjectId();

        return credentials;
    }

    // Gemini Chat Model Bean 생성 (VertexAI Bean이 있을 때만 생성)
    @Bean
    @ConditionalOnBean(VertexAI.class)
    public VertexAiGeminiChatModel vertexAiGeminiChatModel(VertexAI vertexAI) {
        VertexAiGeminiChatOptions options = VertexAiGeminiChatOptions.builder()
                .withModel(properties.getModel())
                .withTemperature(properties.getTemperature())
                .withMaxOutputTokens(properties.getMaxTokens())
                .withTopP(properties.getTopP())
                .withTopK(properties.getTopK().floatValue())
                .build();

        return new VertexAiGeminiChatModel(vertexAI, options);
    }
}
