package com.cinemax.domain.notification.service.impl;

import com.cinemax.core.exception.ResourceNotFoundException;
import com.cinemax.domain.notification.dto.NotificationRequest;
import com.cinemax.domain.notification.dto.NotificationResponse;
import com.cinemax.domain.notification.entity.Notification;
import com.cinemax.domain.notification.repository.NotificationRepository;
import com.cinemax.domain.notification.service.NotificationService;
import com.cinemax.domain.notification.mapper.NotificationMapper;
import com.cinemax.domain.user.entity.User;
import com.cinemax.domain.user.repository.UserRepository;
import com.cinemax.global.enums.NotificationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 알림 서비스 구현
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final NotificationMapper notificationMapper;

    // 알림 생성 -> payloadContent가 제공되면 커스텀 메시지로 사용하고, 없으면 타입의 기본 템플릿 사용
    @Override
    @Transactional
    public NotificationResponse createNotification(Long userId, NotificationRequest request) {

        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));

        Notification notification;
        if (request.getPayloadContent() != null && !request.getPayloadContent().trim().isEmpty()) {
            notification = Notification.createWithCustomMessage(user, request.getType(), request.getPayloadContent());
        } else {
            notification = Notification.create(user, request.getType());
        }

        Notification saved = notificationRepository.save(notification);

        return notificationMapper.toDto(saved);
    }

    /**
     * 파라미터를 포함한 알림 생성
     * 예: "수업이 {N}시간 후 종료됩니다" -> "수업이 2시간 후 종료됩니다"
     */
    @Override
    @Transactional
    public NotificationResponse createNotificationWithParams(Long userId, NotificationType type, Object... params) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));

        Notification notification = Notification.create(user, type, params);
        Notification saved = notificationRepository.save(notification);

        return notificationMapper.toDto(saved);
    }

    // 사용자 정보를 포함한 알림 생성
    @Override
    @Transactional
    public NotificationResponse createNotificationWithUser(Long receiverId, NotificationType type, Long targetUserId) {

        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "userId", receiverId));

        User targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "userId", targetUserId));

        Notification notification = Notification.createWithUser(receiver, type, targetUser);
        Notification saved = notificationRepository.save(notification);

        return notificationMapper.toDto(saved);
    }

    // 시스템 점검 등 시간 정보를 포함한 알림 생성
    @Override
    @Transactional
    public NotificationResponse createNotificationWithDateTime(Long userId, NotificationType type,
                                                               LocalDateTime startTime, LocalDateTime endTime) {

        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));

        String message = type.formatMessageWithDateTime(startTime, endTime);
        Notification notification = Notification.createWithCustomMessage(user, type, message);
        Notification saved = notificationRepository.save(notification);

        return notificationMapper.toDto(saved);
    }

    // 알림 조회
    @Override
    public NotificationResponse getNotification(Long notificationId) {

        Notification notification = notificationRepository.findById(notificationId).orElseThrow(() -> new ResourceNotFoundException("Notification", "notificationId", notificationId));

        return notificationMapper.toDto(notification);
    }

    // 사용자 ID로 알림 목록 조회
    @Override
    public List<NotificationResponse> getNotificationsByUserId(Long userId) {

        // 사용자 존재 여부 확인
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User", "userId", userId);
        }

        List<Notification> notifications = notificationRepository.findByUserUserIdOrderByCreateDtDesc(userId);

        return notificationMapper.toDtoList(notifications);
    }

    // 사용자 ID로 읽지 않은 알림 조회
    @Override
    public List<NotificationResponse> getUnreadNotifications(Long userId) {

        // 사용자 존재 여부 확인
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User", "userId", userId);
        }

        List<Notification> notifications = notificationRepository.findUnreadByUserId(userId);
        return notificationMapper.toDtoList(notifications);
    }

    // 알림 읽음 처리
    @Override
    @Transactional
    public NotificationResponse markAsRead(Long notificationId) {

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification", "notificationId", notificationId));

        notification.markAsRead();
        Notification updated = notificationRepository.save(notification);

        return notificationMapper.toDto(updated);
    }

    // 사용자의 모든 알림 읽음 처리
    @Override
    @Transactional
    public void markAllAsRead(Long userId) {

        // 사용자 존재 여부 확인
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User", "userId", userId);
        }

        notificationRepository.markAllAsReadByUserId(userId, LocalDateTime.now());
    }

    /**
     * 알림 삭제
     */
    @Override
    @Transactional
    public void deleteNotification(Long notificationId) {

        Notification notification = notificationRepository.findById(notificationId).orElseThrow(() -> new ResourceNotFoundException("Notification", "notificationId", notificationId));

        notificationRepository.delete(notification);
    }

    /**
     * 읽지 않은 알림 개수 조회
     */
    @Override
    public Long countUnreadNotifications(Long userId) {

        // 사용자 존재 여부 확인
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User", "userId", userId);
        }

        return notificationRepository.countUnreadByUserId(userId);
    }

    /**
     * 여러 사용자에게 동일한 알림 발송
     */
    @Override
    @Transactional
    public int sendBulkNotifications(List<Long> userIds, NotificationType type, Object... params) {
        if (userIds == null || userIds.isEmpty()) {
            throw new IllegalArgumentException("수신자 ID 목록은 비어있을 수 없습니다.");
        }

        int successCount = 0;
        for (Long userId : userIds) {
            try {
                User user = userRepository.findById(userId)
                        .orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));

                Notification notification = Notification.create(user, type, params);
                notificationRepository.save(notification);
                successCount++;
            } catch (Exception e) {
                log.error("알림 발송 실패 - userId: {}, type: {}, error: {}", userId, type, e.getMessage());
            }
        }

        log.info("대량 알림 발송 완료 - 성공: {}, 실패: {}", successCount, userIds.size() - successCount);
        return successCount;
    }

    /**
     * 여러 사용자에게 커스텀 메시지 알림 발송
     */
    @Override
    @Transactional
    public int sendBulkCustomNotifications(List<Long> userIds, NotificationType type, String customMessage) {
        if (userIds == null || userIds.isEmpty()) {
            throw new IllegalArgumentException("수신자 ID 목록은 비어있을 수 없습니다.");
        }

        if (customMessage == null || customMessage.isBlank()) {
            throw new IllegalArgumentException("커스텀 메시지는 필수입니다.");
        }

        int successCount = 0;
        for (Long userId : userIds) {
            try {
                User user = userRepository.findById(userId)
                        .orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));

                Notification notification = Notification.createWithCustomMessage(user, type, customMessage);
                notificationRepository.save(notification);
                successCount++;
            } catch (Exception e) {
                log.error("커스텀 알림 발송 실패 - userId: {}, type: {}, error: {}", userId, type, e.getMessage());
            }
        }

        log.info("대량 커스텀 알림 발송 완료 - 성공: {}, 실패: {}", successCount, userIds.size() - successCount);
        return successCount;
    }
}
