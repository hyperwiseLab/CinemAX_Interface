package com.cinemax.global.jwt;

import com.cinemax.core.constants.CommonConstants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT 인증 필터
 */
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    // JWT 토큰 인증 필터
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Request Header에서 JWT 토큰 추출
        String jwt = resolveToken(request);

        // validateToken으로 토큰 유효성 검사
        if (StringUtils.hasText(jwt) && jwtTokenProvider.validateToken(jwt)) {
            // 토큰이 유효할 경우 Authentication 객체를 SecurityContext에 저장
            Authentication authentication = jwtTokenProvider.getAuthentication(jwt);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    // Request Header에서 토큰 정보 추출
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(CommonConstants.JWT_HEADER_NAME);

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(CommonConstants.JWT_TOKEN_PREFIX)) {
            return bearerToken.substring(CommonConstants.JWT_TOKEN_PREFIX.length());
        }

        return null;
    }

    /**
     * 특정 경로는 JWT 필터를 거치지 않도록 설정
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        
        // context-path가 /api/v1이므로 이를 제거한 경로로 비교
        String contextPath = request.getContextPath();
        if (contextPath != null && path.startsWith(contextPath)) {
            path = path.substring(contextPath.length());
        }
        
        // 인증이 필요 없는 경로들
        boolean shouldNotFilter = path.startsWith("/auth/") ||
               path.startsWith("/email-verification/") ||
               path.startsWith("/public/") ||
               path.startsWith("/test/") ||
               path.startsWith("/swagger-ui") ||
               path.startsWith("/api-docs") ||
               path.startsWith("/v3/api-docs") ||
               path.startsWith("/actuator/") ||
               path.startsWith("/health/") ||
               path.startsWith("/json/") ||
               path.startsWith("/static/") ||
               path.startsWith("/css/") ||
               path.startsWith("/js/") ||
               path.startsWith("/images/");
               
        log.debug("JWT Filter - Path: {}, ShouldNotFilter: {}", path, shouldNotFilter);
        return shouldNotFilter;
    }
}
