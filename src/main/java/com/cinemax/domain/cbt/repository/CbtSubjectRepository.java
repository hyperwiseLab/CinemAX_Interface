package com.cinemax.domain.cbt.repository;

import com.cinemax.domain.cbt.entity.CbtSubject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CbtSubjectRepository extends JpaRepository<CbtSubject, Long> {

    List<CbtSubject> findByClassIdOrderByOrderNoAsc(Long classId);

    List<CbtSubject> findByClassIdAndUseYnTrueOrderByOrderNoAsc(Long classId);

    Optional<CbtSubject> findByClassIdAndSubjectNm(Long classId, String subjectNm);

    void deleteByClassId(Long classId);
}
