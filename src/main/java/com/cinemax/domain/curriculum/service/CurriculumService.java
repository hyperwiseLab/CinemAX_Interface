package com.cinemax.domain.curriculum.service;

import com.cinemax.domain.curriculum.dto.CurriculumRequest;
import com.cinemax.domain.curriculum.dto.CurriculumResponse;

import java.util.List;

/**
 * 커리큘럼 서비스 인터페이스
 */
public interface CurriculumService {

    // 커리큘럼 생성
    CurriculumResponse createCurriculum(CurriculumRequest request);

    // 커리큘럼 조회
    CurriculumResponse getCurriculum(Long curId);

    // 커리큘럼 상세 조회 (주차 정보 포함)
    CurriculumResponse getCurriculumWithWeeks(Long curId);

    // 모든 커리큘럼 조회 (활성화 여부 무관)
    List<CurriculumResponse> getAllCurriculums();

    // 모든 활성화된 커리큘럼 조회
    List<CurriculumResponse> getAllActiveCurriculums();

    // 언어별 활성화된 커리큘럼 조회
    List<CurriculumResponse> getActiveCurriculumsByLang(String lang);

    // 커리큘럼 수정
    CurriculumResponse updateCurriculum(Long curId, CurriculumRequest request);

    // 커리큘럼 비활성화
    void deactivateCurriculum(Long curId);

    // 커리큘럼 활성화
    void activateCurriculum(Long curId);

    // 커리큘럼 삭제
    void deleteCurriculum(Long curId);

    // 특정 커리큘럼으로 생성된 수업 개수 조회
    Long getClassCountByCurriculum(Long curId);
}
