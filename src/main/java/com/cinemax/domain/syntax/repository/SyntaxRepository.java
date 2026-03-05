package com.cinemax.domain.syntax.repository;

import com.cinemax.domain.syntax.entity.Syntax;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SyntaxRepository extends JpaRepository<Syntax, Long> {

    // Cycle ID로 Syntax 조회
    List<Syntax> findByCycleId(Long cycleId);

    // Cycle ID로 SyntaxDetail까지 Fetch Join 조회
    @Query("SELECT DISTINCT s FROM Syntax s LEFT JOIN FETCH s.syntaxDetails sd " +
           "WHERE s.cycleId = :cycleId ORDER BY s.syntaxOrder, sd.detailOrder")
    List<Syntax> findByCycleIdWithDetails(@Param("cycleId") Long cycleId);

    // Syntax ID로 SyntaxDetail까지 Fetch Join 조회
    @Query("SELECT s FROM Syntax s LEFT JOIN FETCH s.syntaxDetails sd " +
           "WHERE s.syntaxId = :syntaxId ORDER BY sd.detailOrder")
    Optional<Syntax> findByIdWithDetails(@Param("syntaxId") Long syntaxId);
}
