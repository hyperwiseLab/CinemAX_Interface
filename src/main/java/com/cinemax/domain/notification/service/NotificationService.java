package com.cinemax.domain.notification.service;

import com.cinemax.domain.notification.dto.NotificationRequest;
import com.cinemax.domain.notification.dto.NotificationResponse;
import com.cinemax.global.enums.NotificationType;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 알림 서비스 인터페이스
 */
public interface NotificationService {

    /**
     * 알림 생성
     */
    NotificationResponse createNotification(Long userId, NotificationRequest request);

    /**
     * 파라미터를 포함한 알림 생성
     * @param userId 수신자 ID
     * @param type 알림 타입
     * @param params 메시지 템플릿에 치환될 파라미터들
     */
    NotificationResponse createNotificationWithParams(Long userId, NotificationType type, Object... params);

    /**
     * 사용자 정보를 포함한 알림 생성
     * @param receiverId 수신자 ID
     * @param type 알림 타입
     * @param targetUserId 메시지에 포함될 대상 사용자 ID
     */
    NotificationResponse createNotificationWithUser(Long receiverId, NotificationType type, Long targetUserId);

    /**
     * 시스템 점검 등 시간 정보를 포함한 알림 생성
     * @param userId 수신자 ID
     * @param type 알림 타입
     * @param startTime 시작 시간
     * @param endTime 종료 시간
     */
    NotificationResponse createNotificationWithDateTime(Long userId, NotificationType type, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 알림 조회
     */
    NotificationResponse getNotification(Long notificationId);

    /**
     * 사용자 ID로 알림 목록 조회
     */
    List<NotificationResponse> getNotificationsByUserId(Long userId);

    /**
     * 사용자 ID로 읽지 않은 알림 조회
     */
    List<NotificationResponse> getUnreadNotifications(Long userId);

    /**
     * 알림 읽음 처리
     */
    NotificationResponse markAsRead(Long notificationId);

    /**
     * 사용자의 모든 알림 읽음 처리
     */
    void markAllAsRead(Long userId);

    /**
     * 알림 삭제
     */
    void deleteNotification(Long notificationId);

    /**
     * 읽지 않은 알림 개수 조회
     */
    Long countUnreadNotifications(Long userId);

    /**
     * 여러 사용자에게 동일한 알림 발송
     * @param userIds 수신자 ID 목록
     * @param type 알림 타입
     * @param params 메시지 템플릿 파라미터
     * @return 생성된 알림 개수
     */
    int sendBulkNotifications(List<Long> userIds, NotificationType type, Object... params);

    /**
     * 여러 사용자에게 커스텀 메시지 알림 발송
     * @param userIds 수신자 ID 목록
     * @param type 알림 타입
     * @param customMessage 커스텀 메시지
     * @return 생성된 알림 개수
     */
    int sendBulkCustomNotifications(List<Long> userIds, NotificationType type, String customMessage);
}
