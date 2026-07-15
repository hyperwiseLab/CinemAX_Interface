package com.cinemax.domain.quiz.service;

import com.cinemax.domain.assign.entity.Assign;
import com.cinemax.domain.assign.repository.AssignRepository;
import com.cinemax.domain.brief.entity.Brief;
import com.cinemax.domain.brief.repository.BriefRepository;
import com.cinemax.domain.curriculum.entity.CurriculumWeek;
import com.cinemax.domain.curriculum.repository.CurriculumWeekRepository;
import com.cinemax.domain.lecture.entity.Lecture;
import com.cinemax.domain.lecture.entity.LectureSection;
import com.cinemax.domain.lecture.repository.LectureRepository;
import com.cinemax.domain.lecture.repository.LectureSectionRepository;
import com.cinemax.domain.task.entity.Task;
import com.cinemax.domain.task.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 특정 커리큘럼의 특정 주차 시나리오 텍스트(과제/브리핑/강의)를 모아
 * AI 프롬프트 재료로 만드는 컴포넌트.
 *
 * 저장된 커리큘럼 상세(content 500자)는 빈약하므로,
 * task/assign/brief/lecture 개별 테이블에서 풀텍스트를 직접 aggregate 한다.
 */
@Component
@RequiredArgsConstructor
public class ScenarioAggregator {

    private final CurriculumWeekRepository curriculumWeekRepository;
    private final TaskRepository taskRepository;
    private final AssignRepository assignRepository;
    private final BriefRepository briefRepository;
    private final LectureRepository lectureRepository;
    private final LectureSectionRepository lectureSectionRepository;

    /**
     * curId + weekNo 로 주차 시나리오 전체 텍스트를 조합해 반환.
     * 재료가 전혀 없으면 빈 문자열을 반환한다.
     */
    public String aggregate(Long curId, Integer weekNo) {
        StringBuilder sb = new StringBuilder();

        // 1) 주차 메타 (제목/부제/요약)
        curriculumWeekRepository.findByCurIdAndWeekNo(curId, weekNo).ifPresent(week ->
                appendWeekMeta(sb, week));

        // 2) 주차의 과제들 순회 → 각 과제의 지시/브리핑/강의
        List<Task> tasks = taskRepository.findByWeek(curId, weekNo);
        for (Task task : tasks) {
            appendTask(sb, task);
        }

        return clean(sb.toString());
    }

    private void appendWeekMeta(StringBuilder sb, CurriculumWeek week) {
        appendLine(sb, "[주차 주제] ", week.getTitle());
        appendLine(sb, "[부제] ", week.getSubtitle());
        appendLine(sb, "[개요] ", week.getContent());
    }

    private void appendTask(StringBuilder sb, Task task) {
        appendLine(sb, "\n[과제] ", task.getTaskTitle());

        // 과제 지시 (Assign)
        for (Assign assign : assignRepository.findByTaskId(task.getTaskId())) {
            appendLine(sb, "  - 지시: ", assign.getTitle());
            appendLine(sb, "    ", assign.getSubTitle());
            appendLine(sb, "    ", assign.getAssignContent());
        }

        // 브리핑 (Brief)
        for (Brief brief : briefRepository.findByTaskId(task.getTaskId())) {
            appendLine(sb, "  - 브리핑: ", brief.getTitle());
            appendLine(sb, "    ", brief.getSubTitle());
            appendLine(sb, "    ", brief.getBriefContent());
        }

        // 강의 (Lecture + Sections)
        for (Lecture lecture : lectureRepository.findByTaskId(task.getTaskId())) {
            appendLine(sb, "  - 강의: ", lecture.getTitle());
            appendLine(sb, "    핵심: ", lecture.getKeyTakeaway());
            for (LectureSection section : lectureSectionRepository.findByLectureId(lecture.getLectureId())) {
                appendLine(sb, "    · ", section.getHeading());
                appendLine(sb, "      ", section.getLectureSectionTxt());
            }
        }
    }

    private void appendLine(StringBuilder sb, String prefix, String value) {
        if (value != null && !value.isBlank()) {
            sb.append(prefix).append(value).append('\n');
        }
    }

    /**
     * AI 투입 전 정제: HTML 태그 제거 + ${...} 플레이스홀더 제거.
     */
    private String clean(String text) {
        if (text == null) {
            return "";
        }
        return text
                .replaceAll("<[^>]*>", "")        // HTML 태그
                .replaceAll("\\$\\{[^}]*}", "")   // ${userName} 등 플레이스홀더
                .trim();
    }
}
