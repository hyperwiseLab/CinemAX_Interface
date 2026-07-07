package com.cinemax.core.piston.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Piston(코드 실행 엔진) 연결 설정을 관리하는 Properties 클래스
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "piston")
public class PistonProperties {

    // Piston 서버 URL (예: http://221.148.101.200:2000)
    private String url;

    // 읽기 타임아웃 (밀리초)
    private Integer readTimeout = 30000;
}
