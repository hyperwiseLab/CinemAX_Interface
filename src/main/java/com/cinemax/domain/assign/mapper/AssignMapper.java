package com.cinemax.domain.assign.mapper;

import com.cinemax.core.config.GlobalMapperConfig;
import com.cinemax.domain.assign.dto.AssignRequest;
import com.cinemax.domain.assign.dto.AssignResponse;
import com.cinemax.domain.assign.entity.Assign;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * Assign 엔티티와 DTO 간 변환을 위한 Mapper
 */
@Mapper(config = GlobalMapperConfig.class)
public interface AssignMapper {

    // Entity to DTO
    @Mapping(source = "assignId", target = "assignId")
    @Mapping(source = "cycleId", target = "cycleId")
    @Mapping(source = "taskId", target = "taskId")
    @Mapping(source = "characterImg", target = "characterImg")
    @Mapping(source = "characterPath", target = "characterPath")
    @Mapping(source = "title", target = "title")
    @Mapping(source = "subTitle", target = "subTitle")
    @Mapping(source = "assignContent", target = "assignContent")
    @Mapping(source = "createDt", target = "createDt")
    @Mapping(source = "updateDt", target = "updateDt")
    AssignResponse toDto(Assign entity);

    // DTO to Entity
    @Mapping(source = "assignId", target = "assignId")
    @Mapping(source = "cycleId", target = "cycleId")
    @Mapping(source = "taskId", target = "taskId")
    @Mapping(source = "characterImg", target = "characterImg")
    @Mapping(source = "characterPath", target = "characterPath")
    @Mapping(source = "title", target = "title")
    @Mapping(source = "subTitle", target = "subTitle")
    @Mapping(source = "assignContent", target = "assignContent")
    @Mapping(target = "task", ignore = true)
    Assign toEntity(AssignRequest dto);

    List<AssignResponse> toDto(List<Assign> entities);
}
