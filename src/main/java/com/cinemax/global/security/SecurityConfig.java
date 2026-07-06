package com.cinemax.global.security;

import com.cinemax.core.constants.CommonConstants;
import com.cinemax.global.jwt.JwtAuthenticationFilter;
import com.cinemax.global.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.http.HttpMethod;

import java.util.Arrays;
import java.util.List;

/**
 * Spring Security 설정 클래스
 * JWT 기반 인증/인가 설정
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    // PasswordEncoder Bean 등록
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // AuthenticationManager Bean 등록
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * Security Filter Chain 설정
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // CSRF 비활성화 (JWT 사용으로 불필요)
                .csrf(AbstractHttpConfigurer::disable)

                // CORS 설정
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // 세션 사용하지 않음 (Stateless)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Exception Handling 설정
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                        .accessDeniedHandler(jwtAccessDeniedHandler))

                // 권한 설정
                .authorizeHttpRequests(auth -> auth
                        // 인증 관련 엔드포인트 (완전히 공개)
                        .requestMatchers("/auth/**").permitAll()

//                        .requestMatchers("/auth/login", "/auth/signup", "/auth/send-verification-code", "/auth/refresh").permitAll()

                        // 이메일 인증 관련 엔드포인트
                        .requestMatchers("/auth/email-verification/**").permitAll()

                        // QR 리다이렉트 공개
                        .requestMatchers("/join/**").permitAll()

                        // 초대 코드로 수업 참여 (공개)
                        .requestMatchers("/classes/join-by-code/**").permitAll()
                        .requestMatchers("/classes/invite/**").permitAll()

                        // 공개 엔드포인트
                        .requestMatchers("/public/**").permitAll()
                        
                        // 테스트 및 개발용 엔드포인트
                        .requestMatchers("/test/**", "/api/test/**").permitAll()
                        
                        // API 문서 관련
                        .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/api-docs/**", "/v3/api-docs/**").permitAll()
                        
                        // 모니터링 및 헬스체크
                        .requestMatchers("/actuator/**", "/error", "/health/**").permitAll()
                        
                        // 정적 리소스
                        .requestMatchers("/json/**", "/static/**", "/css/**", "/js/**", "/images/**").permitAll()

                        // WebSocket 엔드포인트
                        .requestMatchers("/ws/**").permitAll()

                        // OPTIONS 요청 허용 (CORS preflight)
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // ADMIN 권한이 필요한 경로
                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        // 그 외 모든 요청은 인증 필요
                        .anyRequest().authenticated()
                )

                // JWT 인증 필터 추가
                .addFilterBefore(
                        new JwtAuthenticationFilter(jwtTokenProvider),
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    /**
     * CORS 설정
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // 개발용 React 프론트엔드 도메인 화이트리스트
        configuration.setAllowedOriginPatterns(Arrays.asList(
                "http://localhost:3000",
                "http://localhost:3001",
                "http://175.126.37.175:3001",
                "http://221.148.101.200:3004",
                "http://cinema-x.kr",
                "https://cinema-x.kr"
        ));

        // 허용할 HTTP Method (모두 허용)
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.addAllowedMethod("*");

        // 허용할 Header (모두 허용)
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.addAllowedHeader("*");

        // 인증 정보 포함 허용 (WebSocket 연결을 위해 true로 설정)
        configuration.setAllowCredentials(true);

        // 노출할 Header
        configuration.setExposedHeaders(Arrays.asList(
                CommonConstants.JWT_HEADER_NAME,
                "Content-Type",
                "X-Requested-With",
                "Accept",
                "Origin",
                "Access-Control-Request-Method",
                "Access-Control-Request-Headers"
        ));

        // Preflight 요청 캐시 시간
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
