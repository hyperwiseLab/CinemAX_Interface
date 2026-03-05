package com.cinemax.domain.curriculum.repository;

import com.cinemax.domain.curriculum.entity.CurriculumWeek;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CurriculumWeekRepository extends JpaRepository<CurriculumWeek, Long> {

    // 커리큘럼 ID로 주차별 커리큘럼 조회
    List<CurriculumWeek> findByCurId(Long curId);

    // 커리큘럼 ID와 주차 번호로 조회
    Optional<CurriculumWeek> findByCurIdAndWeekNo(Long curId, Integer weekNo);

    // 커리큘럼 ID로 주차별 커리큘럼을 주차 순으로 정렬하여 조회
    @Query("SELECT A FROM CurriculumWeek A WHERE A.curId = :curId ORDER BY A.weekNo")
    List<CurriculumWeek> findByCurIdOrderByWeekNo(@Param("curId") Long curId);

    // 특정 커리큘럼의 특정 주차 범위 조회
    @Query("SELECT A FROM CurriculumWeek A WHERE A.curId = :curId AND A.weekNo BETWEEN :startWeek AND :endWeek ORDER BY A.weekNo")
    List<CurriculumWeek> findByCurIdAndWeekNoBetween(@Param("curId") Long curId,
                                                     @Param("startWeek") Integer startWeek,
                                                     @Param("endWeek") Integer endWeek);

    // 커리큘럼 ID로 주차 수 조회
    @Query("SELECT COUNT(A) FROM CurriculumWeek A WHERE A.curId = :curId")
    Long countByCurId(@Param("curId") Long curId);

    // 특정 커리큘럼의 최대 주차 번호 조회
    @Query("SELECT MAX(A.weekNo) FROM CurriculumWeek A WHERE A.curId = :curId")
    Optional<Integer> findMaxWeekNoByCurId(@Param("curId") Long curId);
}
