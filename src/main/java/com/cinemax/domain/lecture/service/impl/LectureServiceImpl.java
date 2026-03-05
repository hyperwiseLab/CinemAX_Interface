package com.cinemax.domain.lecture.service.impl;

import com.cinemax.core.exception.ResourceNotFoundException;
import com.cinemax.domain.lecture.dto.LectureRequest;
import com.cinemax.domain.lecture.dto.LectureResponse;
import com.cinemax.domain.lecture.dto.LectureSectionResponse;
import com.cinemax.domain.lecture.entity.Lecture;
import com.cinemax.domain.lecture.entity.LectureSection;
import com.cinemax.domain.lecture.mapper.LectureMapper;
import com.cinemax.domain.lecture.repository.LectureRepository;
import com.cinemax.domain.lecture.repository.LectureSectionRepository;
import com.cinemax.domain.lecture.service.LectureService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Lecture 서비스 구현체
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LectureServiceImpl implements LectureService {

    private static final String NOT_FOUND_LECTURE = "Lecture를 찾을 수 없습니다. ";

    private final LectureRepository lectureRepository;
    private final LectureSectionRepository lectureSectionRepository;
    private final LectureMapper lectureMapper;

    // 강의 생성
    @Override
    @Transactional
    public LectureResponse createLecture(LectureRequest request) {

        Lecture lecture = Lecture.create(
                request.getCycleId(),
                request.getTaskId(),
                request.getCharacterImg(),
                request.getCharacterPath(),
                request.getTitle(),
                request.getKeyTakeaway(),
                request.getSandboxCode()
        );

        Lecture savedLecture = lectureRepository.save(lecture);

        // Sections 생성
        if (request.getSections() != null && !request.getSections().isEmpty()) {
            for (var sectionRequest : request.getSections()) {
                LectureSection section = LectureSection.create(
                        null,
                        savedLecture.getLectureId(),
                        sectionRequest.getHeading(),
                        sectionRequest.getLectureSectionTxt(),
                        sectionRequest.getLectureSectionCode()
                );
                lectureSectionRepository.save(section);
            }
        }

        // Sections와 함께 재조회
        return getLectureWithSections(savedLecture.getLectureId());
    }

    @Override
    public LectureResponse getLecture(Long lectureId, Long taskId) {
        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_LECTURE + lectureId));

        return lectureMapper.toDto(lecture);
    }

    @Override
    public List<LectureResponse> getAllLectures() {
        List<Lecture> lectures = lectureRepository.findAll();
        return lectureMapper.toDto(lectures);
    }

    @Override
    public List<LectureResponse> getLecturesByTaskId(Long taskId) {
        List<Lecture> lectures = lectureRepository.findByTaskId(taskId);
        return lectureMapper.toDto(lectures);
    }

    @Override
    public List<LectureResponse> searchLecturesByTitle(String keyword) {
        List<Lecture> lectures = lectureRepository.searchByTitle(keyword);
        return lectureMapper.toDto(lectures);
    }

    // 강의 수정
    @Override
    @Transactional
    public LectureResponse updateLecture(Long lectureId, Long taskId, LectureRequest request) {

        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_LECTURE + lectureId));

        lecture.updateInfo(
                request.getCharacterImg(),
                request.getCharacterPath(),
                request.getTitle(),
                request.getKeyTakeaway(),
                request.getSandboxCode()
        );

        log.info("Lecture 수정 완료 - lectureId: {}", lectureId);

        return lectureMapper.toDto(lecture);
    }

    // 강의 삭제
    @Override
    @Transactional
    public void deleteLecture(Long lectureId, Long taskId) {

        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_LECTURE + lectureId));

        lectureRepository.delete(lecture);
    }

    /**
     * Lecture와 LectureSection을 함께 조회하여 LectureResponse 생성
     */
    private LectureResponse getLectureWithSections(Long lectureId) {
        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_LECTURE + lectureId));

        List<LectureSection> sections = lectureSectionRepository.findByLectureId(lectureId);
        List<LectureSectionResponse> sectionResponses = sections.stream()
                .map(LectureSectionResponse::from)
                .collect(Collectors.toList());

        LectureResponse response = lectureMapper.toDto(lecture);
        response.setSections(sectionResponses);

        return response;
    }
}
