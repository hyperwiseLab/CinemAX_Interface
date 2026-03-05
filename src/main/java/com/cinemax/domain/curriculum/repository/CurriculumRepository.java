package com.cinemax.domain.curriculum.repository;

import com.cinemax.domain.curriculum.entity.Curriculum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CurriculumRepository extends JpaRepository<Curriculum, Long> {

    // 언어별 커리큘럼 조회
    List<Curriculum> findByLang(String lang);

    // 활성화된 커리큘럼 조회 (useYn = true)
    @Query("SELECT A FROM Curriculum A WHERE A.useYn = true")
    List<Curriculum> findActiveCurriculums();

    // 언어별 활성화된 커리큘럼 조회
    @Query("SELECT A FROM Curriculum A WHERE A.lang = :lang AND A.useYn = true")
    List<Curriculum> findActiveCurriculumsByLang(@Param("lang") String lang);

    // 커리큘럼명으로 조회
    Optional<Curriculum> findByName(String name);

    // 커리큘럼명과 언어로 조회
    Optional<Curriculum> findByNameAndLang(String name, String lang);

    // 기간별 커리큘럼 조회
    List<Curriculum> findByDurationWeeks(Integer durationWeeks);

    // 커리큘럼과 주차 정보를 함께 조회
    @Query("SELECT A FROM Curriculum A LEFT JOIN FETCH A.curriculumWeeks WHERE A.curId = :curId")
    Optional<Curriculum> findByIdWithWeeks(@Param("curId") Long curId);

    // 활성화 여부별 커리큘럼 수 조회 (관리자 대시보드용)
    Long countByUseYn(Boolean useYn);

    // 언어별 커리큘럼 수 조회 (관리자 대시보드용)
    Long countByLang(String lang);
}
