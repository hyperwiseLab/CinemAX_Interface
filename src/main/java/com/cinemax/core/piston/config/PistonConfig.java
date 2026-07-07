package com.cinemax.core.piston.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

// Piston 클라이언트 설정을 담당하는 Configuration 클래스
@Slf4j
@Configuration
@RequiredArgsConstructor
public class PistonConfig {

    private final PistonProperties pistonProperties;

    // Piston API 호출을 위한 WebClient Bean 생성
    @Bean
    public WebClient pistonWebClient() {

        HttpClient httpClient = HttpClient.create()
            .responseTimeout(Duration.ofMillis(pistonProperties.getReadTimeout()));

        return WebClient.builder()
            .baseUrl(pistonProperties.getUrl())
            .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json")
            .clientConnector(new ReactorClientHttpConnector(httpClient))
            .build();
    }
}
