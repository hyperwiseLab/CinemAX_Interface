package com.cinemax.domain.notification.dto;

import com.cinemax.global.enums.NotificationType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 대량 알림 발송 요청 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BulkNotificationRequest {

    @NotEmpty(message = "수신자 ID 목록은 필수입니다.")
    private List<Long> userIds;

    @NotNull(message = "알림 타입은 필수입니다.")
    private NotificationType type;

    private String customMessage;
}
