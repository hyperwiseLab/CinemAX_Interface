package com.cinemax.domain.notification.dto;

import com.cinemax.global.enums.NotificationType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 알림 생성 요청 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRequest {

    @NotNull(message = "알림 타입은 필수입니다.")
    private NotificationType type;

    private String payloadContent;

    @NotNull(message = "수신자 ID는 필수입니다.")
    private Long userId;
}
