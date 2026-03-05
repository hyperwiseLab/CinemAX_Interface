package com.cinemax.domain.worklog.mapper;

import com.cinemax.core.config.GlobalMapperConfig;
import com.cinemax.domain.worklog.dto.WorkLogResponse;
import com.cinemax.domain.worklog.entity.WorkLog;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * WorkLog 엔티티와 DTO 간 변환을 위한 Mapper
 */
@Mapper(config = GlobalMapperConfig.class)
public interface WorkLogMapper {

    // WorkLog Entity -> WorkLogResponse DTO 변환
    @Mapping(source = "userId", target = "userId")
    @Mapping(source = "user.name", target = "userName")
    @Mapping(source = "user.email", target = "userEmail")
    @Mapping(source = "weeklySessionId", target = "weeklySessionId")
    @Mapping(source = "weeklySession.weekNo", target = "weekNo")
    @Mapping(source = "logDate", target = "logDate")
    @Mapping(source = "createDt", target = "createdAt")
    @Mapping(source = "updateDt", target = "updatedAt")
    @Mapping(source = "professorFeedback", target = "professorFeedback")
    @Mapping(source = "feedbackDate", target = "feedbackDate")
    @Mapping(source = "feedbackScore", target = "feedbackScore")
    WorkLogResponse toDto(WorkLog entity);

    List<WorkLogResponse> toDto(List<WorkLog> entities);
}
