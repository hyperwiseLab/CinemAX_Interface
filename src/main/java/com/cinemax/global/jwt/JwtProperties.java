package com.cinemax.global.jwt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * JWT 설정 프로퍼티
 * application.properties의 jwt.* 설정을 바인딩
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    /**
     * JWT 서명에 사용되는 시크릿 키
     */
    private String secret;

    /**
     * Access Token 만료 시간 (밀리초)
     */
    private Long accessTokenExpiration;

    /**
     * Refresh Token 만료 시간 (밀리초)
     */
    private Long refreshTokenExpiration;

    /**
     * JWT 발급자
     */
    private String issuer;
}
