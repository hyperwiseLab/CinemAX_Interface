package com.cinemax.domain.activity.mapper;

import com.cinemax.core.config.GlobalMapperConfig;
import com.cinemax.domain.activity.dto.ActivityStatusMessage;
import com.cinemax.domain.progress.entity.Progress;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Progress 엔티티와 ActivityStatusMessage DTO 간 변환을 위한 Mapper
 * 주의: ActivityStatusMessage는 previousStatus를 필요로 하므로,
 * 일반적으로 정적 팩토리 메서드 from(Progress, StudentActivityStatus)를 사용하는 것을 권장
 */
@Mapper(config = GlobalMapperConfig.class)
public interface ActivityStatusMessageMapper {

    @Mapping(source = "user.userId", target = "userId")
    @Mapping(source = "user.name", target = "userName")
    @Mapping(source = "weeklySessionId", target = "weeklySessionId")
    @Mapping(target = "previousStatus", ignore = true)  // 별도로 설정 필요
    @Mapping(source = "activityStatus", target = "currentStatus")
    @Mapping(source = "activityStatus.displayName", target = "currentStatusDisplay")
    @Mapping(source = "progressPct", target = "progressPct")
    @Mapping(source = "testFailCount", target = "testFailCount")
    @Mapping(expression = "java(java.time.LocalDateTime.now())", target = "timestamp")
    ActivityStatusMessage toDto(Progress entity);
}
