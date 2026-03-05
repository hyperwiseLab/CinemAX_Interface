package com.cinemax.domain.syntax.repository;

import com.cinemax.domain.syntax.entity.SyntaxDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SyntaxDetailRepository extends JpaRepository<SyntaxDetail, Long> {

    List<SyntaxDetail> findBySyntaxIdOrderByDetailOrder(Long syntaxId);

    void deleteBySyntaxId(Long syntaxId);
}
