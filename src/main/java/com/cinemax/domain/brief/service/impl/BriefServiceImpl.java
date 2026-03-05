package com.cinemax.domain.brief.service.impl;

import com.cinemax.core.exception.ResourceNotFoundException;
import com.cinemax.domain.brief.dto.BriefRequest;
import com.cinemax.domain.brief.dto.BriefResponse;
import com.cinemax.domain.brief.entity.Brief;
import com.cinemax.domain.brief.repository.BriefRepository;
import com.cinemax.domain.brief.service.BriefService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Brief 서비스 구현체
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BriefServiceImpl implements BriefService {

    private static final String NOT_FOUND_BRIEF = "Brief를 찾을 수 없습니다. ";

    private final BriefRepository briefRepository;

    // 브리핑 생성
    @Override
    @Transactional
    public BriefResponse createBrief(BriefRequest request) {

        Brief brief = Brief.create(
                request.getCycleId(),
                request.getTaskId(),
                request.getCharacterImg(),
                request.getCharacterPath(),
                request.getTitle(),
                request.getSubTitle(),
                request.getBriefContent()
        );

        Brief savedBrief = briefRepository.save(brief);

        return BriefResponse.from(savedBrief);
    }

    @Override
    public BriefResponse getBrief(Long briefId, Long taskId) {
        Brief brief = briefRepository.findById(briefId)
                .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_BRIEF + briefId));

        return BriefResponse.from(brief);
    }

    @Override
    public List<BriefResponse> getAllBriefs() {

        List<Brief> briefs = briefRepository.findAll();

        return briefs.stream()
                .map(BriefResponse::from)
                .toList();
    }

    @Override
    public List<BriefResponse> getBriefsByTaskId(Long taskId) {

        List<Brief> briefs = briefRepository.findByTaskId(taskId);

        return briefs.stream()
                .map(BriefResponse::from)
                .toList();
    }

    // 제목 기반 브리핑 검색
    @Override
    public List<BriefResponse> searchBriefsByTitle(String keyword) {

        List<Brief> briefs = briefRepository.searchByTitle(keyword);

        return briefs.stream()
                .map(BriefResponse::from)
                .toList();
    }

    // 브리핑 수정
    @Override
    @Transactional
    public BriefResponse updateBrief(Long briefId, Long taskId, BriefRequest request) {
        Brief brief = briefRepository.findById(briefId).orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_BRIEF + briefId));

        brief.updateInfo(
                request.getCharacterImg(),
                request.getCharacterPath(),
                request.getTitle(),
                request.getSubTitle(),
                request.getBriefContent()
        );

        return BriefResponse.from(brief);
    }

    // 브리핑 삭제
    @Override
    @Transactional
    public void deleteBrief(Long briefId, Long taskId) {
        Brief brief = briefRepository.findById(briefId).orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_BRIEF + briefId));

        briefRepository.delete(brief);
    }
}
