package com.cinemax.domain.user.service.impl;

import com.cinemax.domain.user.entity.User;
import com.cinemax.domain.user.repository.UserRepository;
import com.cinemax.global.security.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Spring Security UserDetailsService 구현체
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    // 이메일(username)로 사용자 정보 로드
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + email));

        // User 상태가 활성화되어 있는지 확인 (status가 null이거나 0이 아닌 경우 활성)
        boolean enabled = user.getStatus() == null || user.getStatus() == 0;

        return new CustomUserDetailsService(
                user.getUserId(),
                user.getEmail(),
                user.getPasswordHash(),
                user.getRole().name(),
                enabled
        );
    }
}
