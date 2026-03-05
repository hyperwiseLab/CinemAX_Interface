package com.cinemax.domain.lecture.mapper;

import com.cinemax.core.config.GlobalMapperConfig;
import com.cinemax.domain.lecture.dto.LectureRequest;
import com.cinemax.domain.lecture.dto.LectureResponse;
import com.cinemax.domain.lecture.entity.Lecture;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * Lecture 엔티티와 DTO 간 변환을 위한 Mapper
 */
@Mapper(config = GlobalMapperConfig.class)
public interface LectureMapper {

    // Lecture Entity -> LectureResponse DTO 변환
    @Mapping(source = "lectureId", target = "lectureId")
    @Mapping(source = "cycleId", target = "cycleId")
    @Mapping(source = "taskId", target = "taskId")
    @Mapping(target = "sections", ignore = true)
    LectureResponse toDto(Lecture entity);

    // List 변환을 위한 메서드
    List<LectureResponse> toDto(List<Lecture> entities);
}