package com.cinemax.domain.cbt.repository;

import com.cinemax.domain.cbt.entity.CbtAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CbtAttemptRepository extends JpaRepository<CbtAttempt, Long> {

    List<CbtAttempt> findByClassIdAndUserIdOrderByRoundNoDesc(Long classId, Long userId);

    List<CbtAttempt> findByClassIdOrderByUserIdAscRoundNoAsc(Long classId);

    @Query("SELECT COALESCE(MAX(a.roundNo), 0) FROM CbtAttempt a WHERE a.classId = :classId AND a.userId = :userId")
    int findMaxRoundNo(@Param("classId") Long classId, @Param("userId") Long userId);

    void deleteByClassId(Long classId);
}
