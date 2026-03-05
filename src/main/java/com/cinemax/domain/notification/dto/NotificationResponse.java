package com.cinemax.domain.notification.dto;

import com.cinemax.domain.notification.entity.Notification;
import com.cinemax.global.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 알림 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {

    private Long notificationId;
    private NotificationType type;
    private String typeName;        // 타입 한글명
    private String sender;          // 송신자 역할
    private String receiver;        // 수신자 역할
    private String purpose;         // 목적
    private String payloadContent;  // 실제 메시지
    private String priority;        // 개발 우선순위
    private Boolean isRead;         // 읽음 여부
    private LocalDateTime readDt;
    private LocalDateTime createDt;
    private Long userId;
    private String userName;
}
