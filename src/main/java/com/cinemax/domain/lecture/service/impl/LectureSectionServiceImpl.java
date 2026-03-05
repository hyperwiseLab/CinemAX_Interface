package com.cinemax.domain.lecture.service.impl;

import com.cinemax.core.exception.ResourceNotFoundException;
import com.cinemax.domain.lecture.dto.LectureSectionRequest;
import com.cinemax.domain.lecture.dto.LectureSectionResponse;
import com.cinemax.domain.lecture.entity.LectureSection;
import com.cinemax.domain.lecture.mapper.LectureSectionMapper;
import com.cinemax.domain.lecture.repository.LectureSectionRepository;
import com.cinemax.domain.lecture.service.LectureSectionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * LectureSection 서비스 구현체
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LectureSectionServiceImpl implements LectureSectionService {

    private static final String NOT_FOUND_SECTION = "LectureSection을 찾을 수 없습니다. ";

    private final LectureSectionRepository lectureSectionRepository;
    private final LectureSectionMapper lectureSectionMapper;


    // LectureSection 생성
    @Override
    @Transactional
    public LectureSectionResponse createLectureSection(Long lectureId, LectureSectionRequest request) {

        LectureSection section = LectureSection.create(
                null,
                lectureId,
                request.getHeading(),
                request.getLectureSectionTxt(),
                request.getLectureSectionCode()
        );

        LectureSection savedSection = lectureSectionRepository.save(section);

        return lectureSectionMapper.toDto(savedSection);
    }

    @Override
    public LectureSectionResponse getLectureSection(Long lectureId, Long sectionId) {
        LectureSection section = lectureSectionRepository.findByLectureIdAndSectionId(lectureId, sectionId)
                .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_SECTION + "lectureId: " + lectureId + ", sectionId: " + sectionId));

        return lectureSectionMapper.toDto(section);
    }

    @Override
    public List<LectureSectionResponse> getSectionsByLectureId(Long lectureId) {
        List<LectureSection> sections = lectureSectionRepository.findByLectureId(lectureId);

        return lectureSectionMapper.toDtoList(sections);
    }

    @Override
    public List<LectureSectionResponse> searchSectionsByHeading(String keyword) {
        List<LectureSection> sections = lectureSectionRepository.searchByHeading(keyword);

        return lectureSectionMapper.toDtoList(sections);
    }

    // LectureSection 수정
    @Override
    @Transactional
    public LectureSectionResponse updateLectureSection(Long lectureId, Long sectionId,
                                                       LectureSectionRequest request) {

        LectureSection section = lectureSectionRepository.findByLectureIdAndSectionId(lectureId, sectionId)
                .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_SECTION + "lectureId: " + lectureId + ", sectionId: " + sectionId));

        section.updateInfo(
                request.getHeading(),
                request.getLectureSectionTxt(),
                request.getLectureSectionCode()
        );

        return lectureSectionMapper.toDto(section);
    }

    // LectureSection 삭제
    @Override
    @Transactional
    public void deleteLectureSection(Long lectureId, Long sectionId) {

        LectureSection section = lectureSectionRepository.findByLectureIdAndSectionId(lectureId, sectionId)
                .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_SECTION + "lectureId: " + lectureId + ", sectionId: " + sectionId));

        lectureSectionRepository.delete(section);
    }
}
