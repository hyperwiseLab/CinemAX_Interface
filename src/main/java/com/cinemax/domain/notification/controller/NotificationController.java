package com.cinemax.domain.notification.controller;

import com.cinemax.core.controller.BaseController;
import com.cinemax.core.dto.response.ApiResponse;
import com.cinemax.domain.notification.dto.BulkNotificationRequest;
import com.cinemax.domain.notification.dto.NotificationRequest;
import com.cinemax.domain.notification.dto.NotificationResponse;
import com.cinemax.domain.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 알림 관리 API
 */
@Slf4j
@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
@Tag(name = "Notification", description = "알림 관리 API")
public class NotificationController extends BaseController {

    private final NotificationService notificationService;

    /**
     * 알림 생성
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR', 'ADMIN')")
    @Operation(summary = "알림 생성", description = "새로운 알림을 생성합니다.")
    public ResponseEntity<ApiResponse<NotificationResponse>> createNotification(@Parameter(description = "사용자 ID") @RequestParam Long userId,
                                                                                @Valid @RequestBody NotificationRequest request) {

        NotificationResponse response = notificationService.createNotification(userId, request);

        return created(response, "알림이 성공적으로 생성되었습니다.");
    }

    /**
     * 알림 조회
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR', 'ADMIN')")
    @Operation(summary = "알림 조회", description = "알림 정보를 조회합니다.")
    public ResponseEntity<ApiResponse<NotificationResponse>> getNotification(
            @Parameter(description = "알림 ID") @PathVariable("id") Long notificationId) {

        NotificationResponse response = notificationService.getNotification(notificationId);
        return success(response, "알림 조회 성공");
    }

    /**
     * 사용자 알림 목록 조회
     */
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR', 'ADMIN')")
    @Operation(summary = "사용자 알림 목록 조회", description = "사용자의 모든 알림을 조회합니다.")
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getNotificationsByUserId(
            @Parameter(description = "사용자 ID") @PathVariable Long userId) {

        List<NotificationResponse> responses = notificationService.getNotificationsByUserId(userId);
        return success(responses, "사용자 알림 목록 조회 성공");
    }

    /**
     * 읽지 않은 알림 조회
     */
    @GetMapping("/user/{userId}/unread")
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR', 'ADMIN')")
    @Operation(summary = "읽지 않은 알림 조회", description = "사용자의 읽지 않은 알림을 조회합니다.")
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getUnreadNotifications(
            @Parameter(description = "사용자 ID") @PathVariable Long userId) {

        List<NotificationResponse> responses = notificationService.getUnreadNotifications(userId);
        return success(responses, "읽지 않은 알림 조회 성공");
    }

    /**
     * 알림 읽음 처리
     */
    @PatchMapping("/{id}/read")
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR', 'ADMIN')")
    @Operation(summary = "알림 읽음 처리", description = "알림을 읽음 상태로 변경합니다.")
    public ResponseEntity<ApiResponse<NotificationResponse>> markAsRead(
            @Parameter(description = "알림 ID") @PathVariable("id") Long notificationId) {

        NotificationResponse response = notificationService.markAsRead(notificationId);
        return success(response, "알림 읽음 처리 완료");
    }

    /**
     * 모든 알림 읽음 처리
     */
    @PatchMapping("/user/{userId}/read-all")
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR', 'ADMIN')")
    @Operation(summary = "모든 알림 읽음 처리", description = "사용자의 모든 알림을 읽음 상태로 변경합니다.")
    public ResponseEntity<ApiResponse<Void>> markAllAsRead(
            @Parameter(description = "사용자 ID") @PathVariable Long userId) {

        notificationService.markAllAsRead(userId);
        return success(null, "모든 알림 읽음 처리 완료");
    }

    /**
     * 알림 삭제
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR', 'ADMIN')")
    @Operation(summary = "알림 삭제", description = "알림을 삭제합니다.")
    public ResponseEntity<ApiResponse<Void>> deleteNotification(
            @Parameter(description = "알림 ID") @PathVariable("id") Long notificationId) {

        notificationService.deleteNotification(notificationId);
        return success(null, "알림이 성공적으로 삭제되었습니다.");
    }

    /**
     * 읽지 않은 알림 개수 조회
     */
    @GetMapping("/user/{userId}/count/unread")
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR', 'ADMIN')")
    @Operation(summary = "읽지 않은 알림 개수 조회", description = "사용자의 읽지 않은 알림 개수를 조회합니다.")
    public ResponseEntity<ApiResponse<Long>> countUnreadNotifications(
            @Parameter(description = "사용자 ID") @PathVariable Long userId) {

        Long count = notificationService.countUnreadNotifications(userId);
        return success(count, "읽지 않은 알림 개수 조회 성공");
    }

    /**
     * 대량 알림 발송 (여러 사용자에게 동일한 알림)
     */
    @PostMapping("/bulk")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(summary = "대량 알림 발송", description = "여러 사용자에게 동일한 알림을 발송합니다.")
    public ResponseEntity<ApiResponse<Integer>> sendBulkNotifications(
            @Valid @RequestBody BulkNotificationRequest request) {

        int successCount;
        if (request.getCustomMessage() != null && !request.getCustomMessage().isBlank()) {
            // 커스텀 메시지가 있으면 커스텀 메시지로 발송
            successCount = notificationService.sendBulkCustomNotifications(
                    request.getUserIds(),
                    request.getType(),
                    request.getCustomMessage()
            );
        } else {
            // 기본 템플릿 사용
            successCount = notificationService.sendBulkNotifications(
                    request.getUserIds(),
                    request.getType()
            );
        }

        return created(successCount, String.format("대량 알림 발송 완료 (%d명)", successCount));
    }
}
