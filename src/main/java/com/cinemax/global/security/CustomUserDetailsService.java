package com.cinemax.global.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.micrometer.observation.annotation.Observed;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

/**
 * Spring Security UserDetails 커스텀 구현
 */
@Getter
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetails, Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final Long userId;
    private final String email;

    @JsonIgnore
    private final String password;

    private final String role;
    private final boolean enabled;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role));
    }

    @Override public String getPassword() {
        return password;
    }
    @Override public String getUsername() {
        return email;
    }
    @Override public boolean isAccountNonExpired() {
        return true;
    }
    @Override public boolean isAccountNonLocked() {
        return true;
    }
    @Override public boolean isCredentialsNonExpired() {
        return true;
    }
    @Override public boolean isEnabled() {
        return enabled;
    }

    // 사용자 ID 반환 (Primary Key)
    public Long getUserId() { return userId;}

    // 사용자 이메일 반환
    public String getEmail() { return email;}

    // 사용자 역할 반환
    public String getRole() { return role;}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CustomUserDetailsService that)) return false;
        return Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }
}
