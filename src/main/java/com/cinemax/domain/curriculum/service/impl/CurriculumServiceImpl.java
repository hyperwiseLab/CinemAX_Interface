package com.cinemax.domain.curriculum.service.impl;

import com.cinemax.core.exception.ResourceNotFoundException;
import com.cinemax.domain.curriculum.dto.CurriculumRequest;
import com.cinemax.domain.curriculum.dto.CurriculumResponse;
import com.cinemax.domain.curriculum.dto.CurriculumWeekRequest;
import com.cinemax.domain.curriculum.entity.Curriculum;
import com.cinemax.domain.curriculum.entity.CurriculumWeek;
import com.cinemax.domain.curriculum.repository.CurriculumRepository;
import com.cinemax.domain.curriculum.repository.CurriculumWeekRepository;
import com.cinemax.domain.curriculum.service.CurriculumService;
import com.cinemax.domain.classes.repository.ClassEntityRepository;
import com.cinemax.domain.cycle.entity.Cycle;
import com.cinemax.domain.cycle.repository.CycleRepository;
import com.cinemax.domain.task.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 커리큘럼 서비스 구현체
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CurriculumServiceImpl implements CurriculumService {

    private String NOT_FOUND_CURRICULUM = "커리큘럼을 찾을 수 없습니다. ID: ";

    private final CurriculumRepository curriculumRepository;
    private final CurriculumWeekRepository curriculumWeekRepository;
    private final ClassEntityRepository classEntityRepository;
    private final CycleRepository cycleRepository;
    private final TaskRepository taskRepository;

    // 커리큘럼 생성 요청
    @Override
    @Transactional
    public CurriculumResponse createCurriculum(CurriculumRequest request) {

        // 커리큘럼 생성
        Curriculum curriculum = Curriculum.builder()
                .lang(request.getLang())
                .name(request.getName())
                .description(request.getDescription())
                .durationWeeks(request.getDurationWeeks())
                .useYn(request.getUseYn())
                .build();

        Curriculum savedCurriculum = curriculumRepository.save(curriculum);

        // 주차별 커리큘럼 생성
        if (request.getCurriculumWeeks() != null && !request.getCurriculumWeeks().isEmpty()) {
            for (CurriculumWeekRequest weekRequest : request.getCurriculumWeeks()) {
                CurriculumWeek curriculumWeek = CurriculumWeek.builder()
                        .curId(savedCurriculum.getCurId())
                        .weekNo(weekRequest.getWeekNo())
                        .curriculum(savedCurriculum)
                        .title(weekRequest.getTitle())
                        .subtitle(weekRequest.getSubtitle())
                        .content(weekRequest.getContent())
                        .curLev(weekRequest.getCurLev())
                        .characterNm(weekRequest.getCharacterNm())
                        .build();

                curriculumWeekRepository.save(curriculumWeek);
            }
        }

        return CurriculumResponse.from(savedCurriculum);
    }

    // 커리큘럼 조회
    @Override
    public CurriculumResponse getCurriculum(Long curId) {

        Curriculum curriculum = curriculumRepository.findById(curId)
                .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_CURRICULUM + curId));

        return CurriculumResponse.from(curriculum);
    }

    // 커리큘럼 상세 조회
    @Override
    public CurriculumResponse getCurriculumWithWeeks(Long curId) {

        Curriculum curriculum = curriculumRepository.findByIdWithWeeks(curId)
                .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_CURRICULUM + curId));

        return CurriculumResponse.fromWithWeeks(curriculum);
    }

    // 모든 커리큘럼 조회 (활성화 여부 무관)
    @Override
    public List<CurriculumResponse> getAllCurriculums() {

        List<Curriculum> curriculums = curriculumRepository.findAll();
        return curriculums.stream()
                .map(CurriculumResponse::from)
                .toList();
    }

    // 활성화된 모든 커리큘럼 조회
    @Override
    public List<CurriculumResponse> getAllActiveCurriculums() {

        List<Curriculum> curriculums = curriculumRepository.findActiveCurriculums();
        return curriculums.stream()
                .map(CurriculumResponse::from)
                .toList();
    }

    // 언어별 활성화된 커리큘럼 조회
    @Override
    public List<CurriculumResponse> getActiveCurriculumsByLang(String lang) {

        List<Curriculum> curriculums = curriculumRepository.findActiveCurriculumsByLang(lang);
        return curriculums.stream()
                .map(CurriculumResponse::from)
                .toList();
    }

    // 커리큘럼 수정
    @Override
    @Transactional
    public CurriculumResponse updateCurriculum(Long curId, CurriculumRequest request) {

        Curriculum curriculum = curriculumRepository.findById(curId)
                .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_CURRICULUM + curId));

        // 커리큘럼 정보 수정
        curriculum.updateInfo(request.getLang(), request.getName(), request.getDescription(), 
                            request.getDurationWeeks(), request.getUseYn());

        // 주차별 커리큘럼 수정 (기존 데이터 삭제 후 재생성)
        if (request.getCurriculumWeeks() != null && !request.getCurriculumWeeks().isEmpty()) {
            // 기존 주차별 커리큘럼 삭제
            // FK 제약(week <- cycle <- task) 때문에 자식(task -> cycle)부터 삭제 후 week 삭제
            List<CurriculumWeek> existingWeeks = curriculumWeekRepository.findByCurId(curId);
            for (CurriculumWeek existingWeek : existingWeeks) {
                List<Cycle> cycles = cycleRepository.findByCurWeekId(existingWeek.getCurWeekId());
                for (Cycle cycle : cycles) {
                    taskRepository.deleteAll(taskRepository.findByCycleId(cycle.getCycleId()));
                }
                cycleRepository.deleteAll(cycles);
            }
            curriculumWeekRepository.deleteAll(existingWeeks);

            // 새로운 주차별 커리큘럼 생성
            for (CurriculumWeekRequest weekRequest : request.getCurriculumWeeks()) {
                CurriculumWeek curriculumWeek = CurriculumWeek.builder()
                        .curId(curId)
                        .weekNo(weekRequest.getWeekNo())
                        .curriculum(curriculum)
                        .title(weekRequest.getTitle())
                        .subtitle(weekRequest.getSubtitle())
                        .content(weekRequest.getContent())
                        .curLev(weekRequest.getCurLev())
                        .characterNm(weekRequest.getCharacterNm())
                        .build();

                curriculumWeekRepository.save(curriculumWeek);
            }
        }

        return CurriculumResponse.from(curriculum);
    }

    // 커리큘럼 비활성화
    @Override
    @Transactional
    public void deactivateCurriculum(Long curId) {
        log.info("커리큘럼 비활성화: curId={}", curId);

        Curriculum curriculum = curriculumRepository.findById(curId)
                .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_CURRICULUM + curId));

        curriculum.deactivate();
        log.info("커리큘럼 비활성화 완료: curId={}", curId);
    }

    // 커리큘럼 활성화
    @Override
    @Transactional
    public void activateCurriculum(Long curId) {

        Curriculum curriculum = curriculumRepository.findById(curId)
                .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_CURRICULUM + curId));

        curriculum.activate();
    }

    // 커리큘럼 삭제
    @Override
    @Transactional
    public void deleteCurriculum(Long curId) {

        Curriculum curriculum = curriculumRepository.findById(curId)
                .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_CURRICULUM + curId));

        curriculumRepository.delete(curriculum);
    }

    // 특정 커리큘럼으로 생성된 수업 개수 조회
    @Override
    public Long getClassCountByCurriculum(Long curId) {

        // 커리큘럼 존재 여부 확인
        curriculumRepository.findById(curId)
                .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_CURRICULUM + curId));

        return classEntityRepository.countByCurriculumId(curId);
    }
}
