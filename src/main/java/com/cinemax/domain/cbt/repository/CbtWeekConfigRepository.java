package com.cinemax.domain.cbt.repository;

import com.cinemax.domain.cbt.entity.CbtWeekConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CbtWeekConfigRepository extends JpaRepository<CbtWeekConfig, Long> {

    List<CbtWeekConfig> findByClassIdOrderByWeekNoAsc(Long classId);

    Optional<CbtWeekConfig> findByClassIdAndWeekNo(Long classId, Integer weekNo);
}
