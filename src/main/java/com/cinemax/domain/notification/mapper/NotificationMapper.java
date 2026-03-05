package com.cinemax.domain.notification.mapper;

import com.cinemax.domain.notification.dto.NotificationResponse;
import com.cinemax.domain.notification.entity.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * Notification 엔티티와 DTO 간 변환을 위한 Mapper
 */
@Mapper(componentModel = "spring")
public interface NotificationMapper {

    // Notification Entity -> NotificationResponse DTO 변환
    @Mapping(target = "notificationId", source = "notificationId")
    @Mapping(target = "type", source = "type")
    @Mapping(target = "typeName", expression = "java(notification.getType() != null ? notification.getType().name() : null)")
    @Mapping(target = "sender", expression = "java(notification.getType() != null ? notification.getType().getSender() : null)")
    @Mapping(target = "receiver", expression = "java(notification.getType() != null ? notification.getType().getReceiver() : null)")
    @Mapping(target = "purpose", expression = "java(notification.getType() != null ? notification.getType().getPurpose() : null)")
    @Mapping(target = "payloadContent", source = "payloadContent")
    @Mapping(target = "priority", expression = "java(notification.getType() != null ? notification.getType().getPriority() : null)")
    @Mapping(target = "isRead", expression = "java(notification.isRead())")
    @Mapping(target = "readDt", source = "readDt")
    @Mapping(target = "createDt", source = "createDt")
    @Mapping(target = "userId", expression = "java(notification.getUser() != null ? notification.getUser().getUserId() : null)")
    @Mapping(target = "userName", expression = "java(notification.getUser() != null ? notification.getUser().getName() : null)")
    NotificationResponse toDto(Notification notification);

    // List 변환을 위한 메서드
    List<NotificationResponse> toDtoList(List<Notification> notifications);
}
