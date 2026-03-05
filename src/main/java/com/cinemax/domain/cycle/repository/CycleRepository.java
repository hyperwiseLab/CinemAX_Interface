package com.cinemax.domain.cycle.repository;

import com.cinemax.domain.cycle.entity.Cycle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CycleRepository extends JpaRepository<Cycle, Long> {

    // 커리큘럼 주차 ID로 Cycle 조회
    @Query("SELECT DISTINCT c FROM Cycle c LEFT JOIN FETCH c.syntaxes s " +
           "WHERE c.curWeekId = :curWeekId ORDER BY s.syntaxOrder")
    List<Cycle> findByCurWeekId(@Param("curWeekId") Long curWeekId);

    // Cycle ID로 Syntax와 SyntaxDetail까지 함께 조회
    @Query("SELECT c FROM Cycle c LEFT JOIN FETCH c.syntaxes s " +
           "WHERE c.cycleId = :cycleId ORDER BY s.syntaxOrder")
    Optional<Cycle> findByIdWithSyntax(@Param("cycleId") Long cycleId);
}
