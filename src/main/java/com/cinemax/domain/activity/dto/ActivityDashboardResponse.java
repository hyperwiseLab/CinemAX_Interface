package com.cinemax.domain.activity.dto;

import com.cinemax.global.enums.TaskMode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 실시간 모니터링 대시보드 집계 응답.
 * 기존에 프론트가 학생마다 반복 호출하던 (반 정보 → 커리큘럼 → 사이클 → 과제 → 최신코드 → 에러수)
 * N+1 체인을 서버에서 한 번에 조인해 내려준다. 이후 갱신은 /topic/activity-monitor 웹소켓 이벤트로 처리.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "실시간 모니터링 대시보드 집계 응답")
public class ActivityDashboardResponse {

    @Schema(description = "주차 수업 ID")
    private Long weeklySessionId;

    @Schema(description = "반 ID")
    private Long classId;

    @Schema(description = "커리큘럼 ID")
    private Long curId;

    @Schema(description = "주차")
    private Integer weekNo;

    @Schema(description = "이 주차의 사이클 목록 (모드별 과제 포함)")
    private List<CycleInfo> cycles;

    @Schema(description = "학생별 활동/코드/에러 현황")
    private List<StudentDashboard> students;

    @Schema(description = "반 전체 수강생 수(활성)")
    private Integer totalStudents;

    @Schema(description = "과제 성공률(%) - 제출 없으면 0")
    private Integer successRate;

    @Schema(description = "총 제출 수")
    private Integer submissionTotal;

    @Schema(description = "성공 제출 수")
    private Integer submissionSuccess;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "사이클 정보")
    public static class CycleInfo {

        @Schema(description = "사이클 ID")
        private Long cycleId;

        @Schema(description = "사이클 제목")
        private String cycleTitle;

        @Schema(description = "사이클의 과제 목록 (EASY/ADVANCE)")
        private List<TaskInfo> tasks;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "과제 정보")
    public static class TaskInfo {

        @Schema(description = "과제 ID")
        private Long taskId;

        @Schema(description = "과제 제목")
        private String taskTitle;

        @Schema(description = "과제 모드")
        private TaskMode taskMode;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "학생별 대시보드 항목")
    public static class StudentDashboard {

        @Schema(description = "학생 활동 상태 (기존 활동 조회 응답과 동일)")
        private StudentActivityResponse activity;

        @Schema(description = "최신 저장 코드 (없으면 null)")
        private LatestCode latestCode;

        @Schema(description = "현재 작업 과제의 에러(테스트 실패) 수")
        private Long errorCount;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "학생 최신 코드 스냅샷")
    public static class LatestCode {

        @Schema(description = "작업 중 과제 ID")
        private Long taskId;

        @Schema(description = "작업 중 사이클 ID")
        private Long cycleId;

        @Schema(description = "과제 제목")
        private String taskTitle;

        @Schema(description = "코드 내용")
        private String content;

        @Schema(description = "저장 시각")
        private LocalDateTime saveAt;
    }
}
