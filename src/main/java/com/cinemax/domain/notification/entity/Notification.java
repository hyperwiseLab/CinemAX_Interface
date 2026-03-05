package com.cinemax.domain.notification.entity;

import com.cinemax.domain.user.entity.User;
import com.cinemax.global.enums.NotificationType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "TBL_NOTIFICATION")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "NOTIFICATION_ID")
    private Long notificationId;

    @Enumerated(EnumType.STRING)
    @Column(name = "TYPE", nullable = false, length = 100)
    private NotificationType type;

    @Column(name = "PAYLOAD_CONTENT", length = 500)
    private String payloadContent;

    @Column(name = "READ_DT")
    private LocalDateTime readDt;

    @Column(name = "CREATE_DT")
    private LocalDateTime createDt;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", nullable = false)
    private User user;

    // 알림 생성 (타입과 메시지 자동 생성)
    public static Notification create(User user, NotificationType type, Object... params) {
        String message = type.formatMessage(params);
        return Notification.builder()
                .user(user)
                .type(type)
                .payloadContent(message)
                .createDt(LocalDateTime.now())
                .build();
    }

    // 알림 생성 (사용자 정보 포함)
    public static Notification createWithUser(User receiver, NotificationType type, User targetUser) {
        String message = type.formatMessageWithUser(targetUser);
        return Notification.builder()
                .user(receiver)
                .type(type)
                .payloadContent(message)
                .createDt(LocalDateTime.now())
                .build();
    }

    // 알림 생성 (커스텀 메시지)
    public static Notification createWithCustomMessage(User user, NotificationType type, String customMessage) {
        return Notification.builder()
                .user(user)
                .type(type)
                .payloadContent(customMessage)
                .createDt(LocalDateTime.now())
                .build();
    }

    // 알림 읽음 처리
    public void markAsRead() { this.readDt = LocalDateTime.now();}

    // 읽음 여부 확인
    public boolean isRead() { return this.readDt != null;}
}
