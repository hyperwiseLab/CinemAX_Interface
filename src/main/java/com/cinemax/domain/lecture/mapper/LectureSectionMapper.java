package com.cinemax.domain.lecture.mapper;

import com.cinemax.domain.lecture.dto.LectureSectionResponse;
import com.cinemax.domain.lecture.entity.LectureSection;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * LectureSection 엔티티와 DTO 간 변환을 위한 Mapper
 */
@Mapper(componentModel = "spring")
public interface LectureSectionMapper {

    // LectureSection Entity -> LectureSectionResponse DTO 변환
    LectureSectionResponse toDto(LectureSection entity);

    // List 변환을 위한 메서드
    List<LectureSectionResponse> toDtoList(List<LectureSection> entities);
}