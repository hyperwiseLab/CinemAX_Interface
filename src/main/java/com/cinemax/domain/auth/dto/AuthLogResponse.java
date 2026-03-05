package com.cinemax.domain.auth.dto;

import com.cinemax.domain.auth.entity.AuthLog;
import com.cinemax.domain.user.entity.User;
import com.cinemax.global.enums.AuthEventType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 인증 로그 응답 DTO
 */
@Getter
@Builder
public class AuthLogResponse {

    private Long authLogId;
    private String email;
    private Integer event;
    private String eventDescription;
    private Long userId;
    private String userEmail;
    private LocalDateTime createDt;

    // AuthLog 엔티티를 AuthLogResponse로 변환
    public static AuthLogResponse from(AuthLog authLog) {
        AuthEventType eventType = AuthEventType.fromCodeOrNull(authLog.getEvent());
        
        return AuthLogResponse.builder()
                .authLogId(authLog.getAuthLogId())
                .email(authLog.getEmail())
                .event(authLog.getEvent())
                .eventDescription(eventType != null ? eventType.getDescription() : "알 수 없는 이벤트")
                .userId(authLog.getUser() != null ? authLog.getUser().getUserId() : null)
                .userEmail(authLog.getUser() != null ? authLog.getUser().getEmail() : null)
                .createDt(authLog.getCreateDt())
                .build();
    }
}

