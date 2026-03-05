package com.cinemax.domain.lecture.service;

import com.cinemax.domain.lecture.dto.LectureRequest;
import com.cinemax.domain.lecture.dto.LectureResponse;

import java.util.List;

/**
 * Lecture 서비스 인터페이스
 */
public interface LectureService {

    // Lecture 생성
    LectureResponse createLecture(LectureRequest request);

    // Lecture 조회 (단일)
    LectureResponse getLecture(Long lectureId, Long taskId);

    // 모든 Lecture 조회
    List<LectureResponse> getAllLectures();

    // Task ID로 Lecture 조회
    List<LectureResponse> getLecturesByTaskId(Long taskId);

    // 제목으로 검색
    List<LectureResponse> searchLecturesByTitle(String keyword);

    // Lecture 수정
    LectureResponse updateLecture(Long lectureId, Long taskId, LectureRequest request);

    // Lecture 삭제
    void deleteLecture(Long lectureId, Long taskId);
}
