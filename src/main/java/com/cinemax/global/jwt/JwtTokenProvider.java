package com.cinemax.global.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * JWT 토큰 생성 및 검증을 담당하는 클래스
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final JwtProperties jwtProperties;

    private static final String AUTHORITIES_KEY = "auth";
    private static final String USER_ID_KEY = "userId";
    private static final String BEARER_TYPE = "Bearer";

    /**
     * SecretKey 생성
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Access Token 생성
     */
    public String createAccessToken(Authentication authentication) {
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        long now = new Date().getTime();
        Date accessTokenExpiresIn = new Date(now + jwtProperties.getAccessTokenExpiration());

        // principal에서 userId 추출
        Object principal = authentication.getPrincipal();
        Long userId = null;
        if (principal instanceof com.cinemax.global.security.CustomUserDetailsService) {
            userId = ((com.cinemax.global.security.CustomUserDetailsService) principal).getUserId();
        }

        var builder = Jwts.builder()
                .setSubject(authentication.getName())
                .claim(AUTHORITIES_KEY, authorities)
                .setIssuer(jwtProperties.getIssuer())
                .setIssuedAt(new Date())
                .setExpiration(accessTokenExpiresIn);

        // userId가 있으면 claim에 추가
        if (userId != null) {
            builder.claim(USER_ID_KEY, userId);
        }

        return builder.signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Refresh Token 생성
     */
    public String createRefreshToken(Authentication authentication) {
        long now = new Date().getTime();
        Date refreshTokenExpiresIn = new Date(now + jwtProperties.getRefreshTokenExpiration());

        return Jwts.builder()
                .setSubject(authentication.getName())
                .setIssuer(jwtProperties.getIssuer())
                .setIssuedAt(new Date())
                .setExpiration(refreshTokenExpiresIn)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Token에서 Authentication 정보 추출
     */
    public Authentication getAuthentication(String token) {
        Claims claims = parseClaims(token);

        if (claims.get(AUTHORITIES_KEY) == null) {
            throw new RuntimeException("권한 정보가 없는 토큰입니다.");
        }

        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        UserDetails principal;

        // userId claim이 있으면 CustomUserDetailsService 생성
        if (claims.get(USER_ID_KEY) != null) {
            Long userId = claims.get(USER_ID_KEY, Long.class);
            String email = claims.getSubject();

            // ROLE_ prefix를 제거한 role 추출
            String role = authorities.stream()
                    .map(GrantedAuthority::getAuthority)
                    .findFirst()
                    .orElse("ROLE_STUDENT")
                    .replace("ROLE_", "");

            principal = new com.cinemax.global.security.CustomUserDetailsService(
                    userId,
                    email,
                    "",  // password는 불필요
                    role,
                    true  // enabled
            );
        } else {
            // userId가 없으면 기본 User 사용 (하위 호환성)
            principal = new User(claims.getSubject(), "", authorities);
        }

        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    /**
     * Token 유효성 검증
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.error("잘못된 JWT 서명입니다.", e);
        } catch (ExpiredJwtException e) {
            log.error("만료된 JWT 토큰입니다.", e);
        } catch (UnsupportedJwtException e) {
            log.error("지원되지 않는 JWT 토큰입니다.", e);
        } catch (IllegalArgumentException e) {
            log.error("JWT 토큰이 잘못되었습니다.", e);
        }
        return false;
    }

    /**
     * Token에서 Claims 추출
     */
    private Claims parseClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    /**
     * Token에서 사용자 ID 추출
     */
    public String getUserIdFromToken(String token) {
        Claims claims = parseClaims(token);
        return claims.getSubject();
    }

    /**
     * Token 만료 시간 가져오기
     */
    public Date getExpirationFromToken(String token) {
        Claims claims = parseClaims(token);
        return claims.getExpiration();
    }

    /**
     * Token이 곧 만료되는지 확인 (예: 5분 이내)
     */
    public boolean isTokenExpiringSoon(String token) {
        Date expiration = getExpirationFromToken(token);
        long now = new Date().getTime();
        long timeUntilExpiration = expiration.getTime() - now;

        return timeUntilExpiration < 300000;
    }

    /**
     * Bearer 타입 반환
     */
    public String getBearerType() {
        return BEARER_TYPE;
    }
}
