package com.cinemax.domain.brief.mapper;

import com.cinemax.core.config.GlobalMapperConfig;
import com.cinemax.domain.brief.dto.BriefRequest;
import com.cinemax.domain.brief.dto.BriefResponse;
import com.cinemax.domain.brief.entity.Brief;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Brief 엔티티와 DTO 간의 변환을 담당하는 MapStruct 매퍼
 */
@Mapper(config = GlobalMapperConfig.class)
public interface BriefMapper {

    // Brief Entity -> BriefRequest DTO 변환
    @Mapping(source = "briefId", target = "briefId")
    @Mapping(source = "cycleId", target = "cycleId")
    @Mapping(source = "taskId", target = "taskId")
    @Mapping(source = "characterImg", target = "characterImg")
    @Mapping(source = "characterPath", target = "characterPath")
    @Mapping(source = "title", target = "title")
    @Mapping(source = "subTitle", target = "subTitle")
    @Mapping(source = "briefContent", target = "briefContent")
    BriefRequest toDto(Brief entity);

    // Brief Entity -> BriefResponse DTO 변환
    @Mapping(source = "briefId", target = "briefId")
    @Mapping(source = "cycleId", target = "cycleId")
    @Mapping(source = "taskId", target = "taskId")
    @Mapping(source = "characterImg", target = "characterImg")
    @Mapping(source = "characterPath", target = "characterPath")
    @Mapping(source = "title", target = "title")
    @Mapping(source = "subTitle", target = "subTitle")
    @Mapping(source = "briefContent", target = "briefContent")
    @Mapping(source = "createDt", target = "createDt")
    @Mapping(source = "updateDt", target = "updateDt")
    BriefResponse toResponse(Brief entity);

    // BriefRequest DTO -> Brief Entity 변환
    @Mapping(source = "briefId", target = "briefId")
    @Mapping(source = "cycleId", target = "cycleId")
    @Mapping(source = "taskId", target = "taskId")
    @Mapping(source = "characterImg", target = "characterImg")
    @Mapping(source = "characterPath", target = "characterPath")
    @Mapping(source = "title", target = "title")
    @Mapping(source = "subTitle", target = "subTitle")
    @Mapping(source = "briefContent", target = "briefContent")
    @Mapping(target = "task", ignore = true)
    Brief toEntity(BriefRequest dto);
}