package com.cinemax.domain.lecture.service;

import com.cinemax.domain.lecture.dto.LectureSectionRequest;
import com.cinemax.domain.lecture.dto.LectureSectionResponse;

import java.util.List;

/**
 * LectureSection 서비스 인터페이스
 */
public interface LectureSectionService {

    // LectureSection 생성
    LectureSectionResponse createLectureSection(Long lectureId, LectureSectionRequest request);

    // LectureSection 조회 (단일)
    LectureSectionResponse getLectureSection(Long lectureId, Long sectionId);

    // Lecture ID로 모든 Section 조회
    List<LectureSectionResponse> getSectionsByLectureId(Long lectureId);

    // 제목으로 Section 검색
    List<LectureSectionResponse> searchSectionsByHeading(String keyword);

    // LectureSection 수정
    LectureSectionResponse updateLectureSection(Long lectureId, Long sectionId, LectureSectionRequest request);

    // LectureSection 삭제
    void deleteLectureSection(Long lectureId, Long sectionId);
}
