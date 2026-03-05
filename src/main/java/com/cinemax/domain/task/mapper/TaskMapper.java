package com.cinemax.domain.task.mapper;

import com.cinemax.core.config.GlobalMapperConfig;
import com.cinemax.domain.task.dto.TaskRequest;
import com.cinemax.domain.task.dto.TaskResponse;
import com.cinemax.domain.task.entity.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * Task 엔티티와 DTO 간 변환을 위한 Mapper
 */
@Mapper(config = GlobalMapperConfig.class)
public interface TaskMapper {

    TaskMapper INSTANCE = Mappers.getMapper(TaskMapper.class);

    TaskResponse toDto(Task entity);

    List<TaskResponse> toDto(List<Task> entities);
}
