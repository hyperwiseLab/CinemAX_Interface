package com.cinemax.domain.brief.repository;

import com.cinemax.domain.brief.entity.Brief;
 
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BriefRepository extends JpaRepository<Brief, Long> {

    // Task ID로 Brief 조회
    @Query("SELECT A FROM Brief A WHERE A.taskId = :taskId")
    List<Brief> findByTaskId(@Param("taskId") Long taskId);

    // 제목으로 검색
    @Query("SELECT A FROM Brief A WHERE A.title LIKE CONCAT('%', :keyword, '%')")
    List<Brief> searchByTitle(@Param("keyword") String keyword);
}
