package com.cinemax.domain.hint.service.impl;

import com.cinemax.core.exception.ResourceNotFoundException;
import com.cinemax.domain.hint.dto.HintRequest;
import com.cinemax.domain.hint.dto.HintResponse;
import com.cinemax.domain.hint.entity.Hint;
import com.cinemax.domain.hint.repository.HintRepository;
import com.cinemax.domain.hint.service.HintService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Hint 서비스 구현체
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HintServiceImpl implements HintService {

    private static final String NOT_FOUND_HINT = "Hint를 찾을 수 없습니다. ";

    private final HintRepository hintRepository;

    // Hint 생성
    @Override
    @Transactional
    public HintResponse createHint(HintRequest request) {

        Hint hint = Hint.create(
                request.getTaskId(),
                request.getTitle(),
                request.getContent(),
                request.getVideoUrl()
        );

        Hint savedHint = hintRepository.save(hint);

        return HintResponse.from(savedHint);
    }

    @Override
    public HintResponse getHint(Long hintId, Long taskId) {
        Hint hint = hintRepository.findByTaskIdAndHintId(taskId, hintId)
                .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_HINT + "hintId: " + hintId + ", taskId: " + taskId));

        return HintResponse.from(hint);
    }

    @Override
    public List<HintResponse> getAllHints() {
        List<Hint> hints = hintRepository.findAll();

        return hints.stream()
                .map(HintResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    public List<HintResponse> getHintsByTaskId(Long taskId) {
        List<Hint> hints = hintRepository.findByTaskId(taskId);

        return hints.stream()
                .map(HintResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    public List<HintResponse> searchHintsByTitle(String keyword) {
        List<Hint> hints = hintRepository.searchByTitle(keyword);

        return hints.stream()
                .map(HintResponse::from)
                .collect(Collectors.toList());
    }

    // Hint 수정
    @Override
    @Transactional
    public HintResponse updateHint(Long hintId, Long taskId, HintRequest request) {

        Hint hint = hintRepository.findByTaskIdAndHintId(taskId, hintId)
                .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_HINT + "hintId: " + hintId + ", taskId: " + taskId));

        hint.updateInfo(
                request.getTitle(),
                request.getContent(),
                request.getVideoUrl()
        );

        return HintResponse.from(hint);
    }

    // Hint 삭제
    @Override
    @Transactional
    public void deleteHint(Long hintId, Long taskId) {

        Hint hint = hintRepository.findByTaskIdAndHintId(taskId, hintId)
                .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_HINT + "hintId: " + hintId + ", taskId: " + taskId));

        hintRepository.delete(hint);
    }
}
