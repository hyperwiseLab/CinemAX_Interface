package com.cinemax.domain.weeklySession.mapper;

import com.cinemax.core.config.GlobalMapperConfig;
import com.cinemax.domain.weeklySession.dto.WeeklySessionRequest;
import com.cinemax.domain.weeklySession.dto.WeeklySessionResponse;
import com.cinemax.domain.weeklySession.entity.WeeklySession;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * WeeklySession 엔티티와 DTO 간의 변환을 담당하는 MapStruct 매퍼
 * Entity ↔ DTO 변환 로직을 중앙화하여 관심사 분리
 */
@Mapper(config = GlobalMapperConfig.class)
public interface WeeklySessionMapper {

    // WeeklySession Entity -> WeeklySessionRequest DTO 변환
    @Mapping(source = "inviteId", target = "inviteId")
    @Mapping(source = "weekNo", target = "weekNo")
    WeeklySessionRequest toDto(WeeklySession entity);

    List<WeeklySessionRequest> toDto(List<WeeklySession> entities);

    // WeeklySession Entity -> WeeklySessionResponse DTO 변환
    @Mapping(source = "weeklySessionId", target = "weeklySessionId")
    @Mapping(source = "inviteId", target = "inviteId")
    @Mapping(source = "weekNo", target = "weekNo")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "startDt", target = "startDt")
    @Mapping(source = "updateDt", target = "endDt")  // 종료 시간은 updateDt 사용
    @Mapping(source = "autoClosed", target = "autoClosed")
    @Mapping(source = "createDt", target = "createDt")
    @Mapping(source = "updateDt", target = "updateDt")
    @Mapping(source = "classInvite.classEntity.classId", target = "classId")
    @Mapping(source = "classInvite.classEntity.classNm", target = "classNm")
    WeeklySessionResponse toResponse(WeeklySession entity);

    List<WeeklySessionResponse> toResponse(List<WeeklySession> entities);

    // WeeklySessionRequest DTO -> WeeklySession Entity 변환
    @Mapping(source = "inviteId", target = "inviteId")
    @Mapping(source = "weekNo", target = "weekNo")
    @Mapping(target = "weeklySessionId", ignore = true)
    @Mapping(target = "classInvite", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "startDt", ignore = true)
    @Mapping(target = "autoClosed", ignore = true)
    @Mapping(target = "activityMonitors", ignore = true)
    WeeklySession toEntity(WeeklySessionRequest dto);

    List<WeeklySession> toEntity(List<WeeklySessionRequest> dtos);
}
