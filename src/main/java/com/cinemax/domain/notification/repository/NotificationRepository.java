package com.cinemax.domain.notification.repository;

import com.cinemax.domain.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // 사용자 ID로 알림 조회
    List<Notification> findByUserUserId(Long userId);

    // 사용자 ID로 읽지 않은 알림 조회
    @Query("SELECT A FROM Notification A WHERE A.user.userId = :userId AND A.readDt IS NULL")
    List<Notification> findUnreadByUserId(@Param("userId") Long userId);

    // 사용자 ID로 알림 조회 (생성일시 역순)
    List<Notification> findByUserUserIdOrderByCreateDtDesc(Long userId);

    // 사용자 ID로 읽지 않은 알림 개수 조회
    @Query("SELECT COUNT(A) FROM Notification A WHERE A.user.userId = :userId AND A.readDt IS NULL")
    Long countUnreadByUserId(@Param("userId") Long userId);

    // 사용자 ID로 모든 알림을 읽음 처리
    @Modifying
    @Query("UPDATE Notification A SET A.readDt = :readDt WHERE A.user.userId = :userId AND A.readDt IS NULL")
    void markAllAsReadByUserId(@Param("userId") Long userId, @Param("readDt") LocalDateTime readDt);

    // 사용자 ID로 알림 존재 여부 확인
    boolean existsByUserUserId(Long userId);

    // 사용자 ID와 알림 ID로 알림 조회
    @Query("SELECT A FROM Notification A WHERE A.notificationId = :notificationId AND A.user.userId = :userId")
    Notification findByNotificationIdAndUserId(@Param("notificationId") Long notificationId, @Param("userId") Long userId);
}
