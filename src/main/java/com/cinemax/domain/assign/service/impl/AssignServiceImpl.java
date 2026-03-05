package com.cinemax.domain.assign.service.impl;

import com.cinemax.core.exception.ResourceNotFoundException;
import com.cinemax.domain.assign.dto.AssignRequest;
import com.cinemax.domain.assign.dto.AssignResponse;
import com.cinemax.domain.assign.entity.Assign;
import com.cinemax.domain.assign.mapper.AssignMapper;
import com.cinemax.domain.assign.repository.AssignRepository;
import com.cinemax.domain.assign.service.AssignService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Assign 서비스 구현체
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AssignServiceImpl implements AssignService {

    private static final String NOT_FOUND_ASSIGN = "Assign을 찾을 수 없습니다. ";

    private final AssignRepository assignRepository;
    private final AssignMapper assignMapper;

    // Assign 생성
    @Override
    @Transactional
    public AssignResponse createAssign(AssignRequest request) {

        Assign assign = Assign.create(
                request.getCycleId(),
                request.getTaskId(),
                request.getCharacterImg(),
                request.getCharacterPath(),
                request.getTitle(),
                request.getSubTitle(),
                request.getAssignContent()
        );

        Assign savedAssign = assignRepository.save(assign);

        return assignMapper.toDto(savedAssign);
    }

    @Override
    public AssignResponse getAssign(Long assignId, Long taskId) {
        Assign assign = assignRepository.findByTaskIdAndAssignId(taskId, assignId)
                .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_ASSIGN + "assignId: " + assignId + ", taskId: " + taskId));

        return assignMapper.toDto(assign);
    }

    @Override
    public List<AssignResponse> getAllAssigns() {

        List<Assign> assigns = assignRepository.findAll();

        return assignMapper.toDto(assigns);
    }

    @Override
    public List<AssignResponse> getAssignsByTaskId(Long taskId) {

        List<Assign> assigns = assignRepository.findByTaskId(taskId);

        return assignMapper.toDto(assigns);
    }

    @Override
    public List<AssignResponse> searchAssignsByTitle(String keyword) {

        List<Assign> assigns = assignRepository.searchByTitle(keyword);

        return assignMapper.toDto(assigns);
    }

    // Assign 수정
    @Override
    @Transactional
    public AssignResponse updateAssign(Long assignId, Long taskId, AssignRequest request) {

        Assign assign = assignRepository.findByTaskIdAndAssignId(taskId, assignId)
                .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_ASSIGN + "assignId: " + assignId + ", taskId: " + taskId));

        assign.updateInfo(
                request.getCharacterImg(),
                request.getCharacterPath(),
                request.getTitle(),
                request.getSubTitle(),
                request.getAssignContent()
        );

        return assignMapper.toDto(assign);
    }

    // Assign 삭제
    @Override
    @Transactional
    public void deleteAssign(Long assignId, Long taskId) {

        Assign assign = assignRepository.findByTaskIdAndAssignId(taskId, assignId)
                .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_ASSIGN + "assignId: " + assignId + ", taskId: " + taskId));

        assignRepository.delete(assign);
    }
}
