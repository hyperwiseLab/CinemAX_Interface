package com.cinemax.domain.cycle.service.impl;

import com.cinemax.core.exception.ResourceNotFoundException;
import com.cinemax.domain.cycle.dto.CycleRequest;
import com.cinemax.domain.cycle.dto.CycleResponse;
import com.cinemax.domain.cycle.entity.Cycle;
import com.cinemax.domain.cycle.repository.CycleRepository;
import com.cinemax.domain.cycle.service.CycleService;
import com.cinemax.domain.syntax.entity.Syntax;
import com.cinemax.domain.syntax.entity.SyntaxDetail;
import com.cinemax.domain.syntax.repository.SyntaxDetailRepository;
import com.cinemax.domain.syntax.repository.SyntaxRepository;
import com.cinemax.domain.task.dto.TaskResponse;
import com.cinemax.domain.task.entity.Task;
import com.cinemax.domain.task.mapper.TaskMapper;
import com.cinemax.domain.task.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CycleServiceImpl implements CycleService {

    private final CycleRepository cycleRepository;
    private final SyntaxRepository syntaxRepository;
    private final SyntaxDetailRepository syntaxDetailRepository;
    private final TaskRepository taskRepository;

    private final TaskMapper taskMapper;

    /*
     <Cycle 생성 방법>
     1. Cycle 생성 및 저장
     2. Syntax 및 SyntaxDetail 생성
        2-1. Syntax 생성(order는 1로 고정)
        2-2. SyntaxDetail 목록 생성
     3. Syntax와 SyntaxDetail을 포함하여 다시 조회
     */
    @Override
    @Transactional
    public CycleResponse createCycle(CycleRequest request) {

        Cycle cycle = Cycle.create(request.getCurWeekId(), null, null, request.getCycleTitle(), request.getFileNm());
        Cycle savedCycle = cycleRepository.save(cycle);

        if (request.getSyntaxDetails() != null && !request.getSyntaxDetails().isEmpty()) {

            Syntax syntax = Syntax.create(savedCycle.getCycleId(), 1);
            Syntax savedSyntax = syntaxRepository.save(syntax);

            savedCycle.updateInfo(null, savedSyntax.getSyntaxId(), savedCycle.getCycleTitle(), savedCycle.getFileNm());

            var syntaxDetails = request.getSyntaxDetails();

            for (int i = 0; i < syntaxDetails.size(); i++) {
                var detailRequest = syntaxDetails.get(i);
                SyntaxDetail syntaxDetail = SyntaxDetail.create(savedSyntax.getSyntaxId(),
                                                                detailRequest.getSyntaxTitle(),
                                                                detailRequest.getSyntaxComment(),
                                                                detailRequest.getSyntaxCode(),
                                                                i + 1
                );

                syntaxDetailRepository.save(syntaxDetail);
            }
        }

        Cycle cycleWithSyntax = cycleRepository.findByIdWithSyntax(savedCycle.getCycleId())
                .orElseThrow(() -> new ResourceNotFoundException("Cycle", "cycleId", savedCycle.getCycleId()));

        return CycleResponse.from(cycleWithSyntax);
    }

    // Cycle 수정
    @Override
    @Transactional
    public CycleResponse updateCycle(Long cycleId, CycleRequest request) {

        Cycle cycle = cycleRepository.findById(cycleId).orElseThrow(() -> new ResourceNotFoundException("Cycle", "cycleId", cycleId));

        if (request.getSyntaxDetails() != null && !request.getSyntaxDetails().isEmpty()) {
            List<Syntax> existingSyntaxes = syntaxRepository.findByCycleIdWithDetails(cycleId);
            Syntax syntax;

            if (existingSyntaxes.isEmpty()) {
                syntax = com.cinemax.domain.syntax.entity.Syntax.create(cycleId, 1);
                syntax = syntaxRepository.save(syntax);
            } else {
                syntax = existingSyntaxes.get(0);
                syntaxDetailRepository.deleteBySyntaxId(syntax.getSyntaxId());
            }

            cycle.updateInfo(null, syntax.getSyntaxId(), request.getCycleTitle(), request.getFileNm());

            // SyntaxDetail 새로 생성
            var syntaxDetails = request.getSyntaxDetails();
            for (int i = 0; i < syntaxDetails.size(); i++) {
                var detailRequest = syntaxDetails.get(i);
                SyntaxDetail syntaxDetail = SyntaxDetail.create(syntax.getSyntaxId(),
                                                                detailRequest.getSyntaxTitle(),
                                                                detailRequest.getSyntaxComment(),
                                                                detailRequest.getSyntaxCode(),
                                                        i + 1
                );

                syntaxDetailRepository.save(syntaxDetail);
            }
        } else {
            cycle.updateInfo(null, null, request.getCycleTitle(), request.getFileNm());
        }

        Cycle cycleWithSyntax = cycleRepository.findByIdWithSyntax(cycleId).orElseThrow(() -> new ResourceNotFoundException("Cycle", "cycleId", cycleId));

        return CycleResponse.from(cycleWithSyntax);
    }

    // Cycle 삭제
    @Override
    @Transactional
    public void deleteCycle(Long cycleId) {

        if (!cycleRepository.existsById(cycleId)) {
            throw new ResourceNotFoundException("Cycle", "cycleId", cycleId);
        }

        cycleRepository.deleteById(cycleId);
    }

    // Cycle 조회
    @Override
    @Transactional(readOnly = true)
    public CycleResponse getCycleById(Long cycleId) {

        // Cycle 조회 (Syntax와 함께)
        Cycle cycle = cycleRepository.findByIdWithSyntax(cycleId)
                .orElseThrow(() -> new ResourceNotFoundException("Cycle", "cycleId", cycleId));

        // Task 목록 조회
        List<Task> tasks = taskRepository.findByCycleId(cycleId);
        List<TaskResponse> taskResponses = taskMapper.toDto(tasks);

        return CycleResponse.of(cycle, taskResponses);
    }

    // 커리큘럼 주차별 Cycle 목록 조회
    @Override
    @Transactional(readOnly = true)
    public List<CycleResponse> getCyclesByWeek(Long curWeekId) {

        List<Cycle> cycles = cycleRepository.findByCurWeekId(curWeekId);
        List<CycleResponse> responses = cycles.stream()
                .map(cycle -> {
                    List<Task> tasks = taskRepository.findByCycleId(cycle.getCycleId());
                    List<TaskResponse> taskResponses = taskMapper.toDto(tasks);
                    return CycleResponse.of(cycle, taskResponses);
                }).collect(Collectors.toList());

        return responses;
    }

}
