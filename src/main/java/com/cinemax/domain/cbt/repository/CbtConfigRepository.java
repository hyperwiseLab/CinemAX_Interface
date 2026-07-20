package com.cinemax.domain.cbt.repository;

import com.cinemax.domain.cbt.entity.CbtConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CbtConfigRepository extends JpaRepository<CbtConfig, Long> {

    Optional<CbtConfig> findByClassId(Long classId);

    void deleteByClassId(Long classId);
}
