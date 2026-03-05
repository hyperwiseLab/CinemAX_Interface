package com.cinemax.domain.weeklySession.service;

import com.cinemax.domain.weeklySession.dto.WeeklySessionProgressResponse;
import com.cinemax.domain.weeklySession.entity.WeeklySession;
import com.cinemax.global.enums.WeeklySessionStatus;

import java.util.List;

/**
 * 주차별 수업 서비스 인터페이스
 */
public interface WeeklySessionService {

    /**
     * 주차별 수업 시작
     * - 동시 진행 제한: 하나의 수업에서 동시에 2개 이상의 주차별 수업을 진행할 수 없음
     * - 순차 진행 불필요: 1주차를 건너뛰고 2주차부터 시작 가능
     */
    WeeklySession startWeeklySession(Long inviteId, Integer weekNo);

    /**
     * 주차별 수업 종료
     * - 수동 종료
     */
    WeeklySession endWeeklySession(Long inviteId, Integer weekNo);

    /**
     * 24시간 자동 종료 처리
     * - 주차별 수업 시작 후 24시간 경과 시 자동 종료
     */
    void autoEndExpiredSessions();

    /**
     * 진행 중인 주차별 수업 조회
     * - 한 수업에서 동시에 하나의 주차만 "진행 중" 가능
     */
    WeeklySession getInProgressSession(String inviteCd);

    // 주차별 수업 상태 조회
    WeeklySessionStatus getSessionStatus(Long inviteId, Integer weekNo);

    // 수업의 모든 주차별 수업 조회
    List<WeeklySession> getAllSessions(Long inviteId);

    // 특정 상태의 주차별 수업 조회
    List<WeeklySession> getSessionsByStatus(Long inviteId, WeeklySessionStatus status);

    // 상태만으로 주차별 수업 조회 (inviteId 제외)
    List<WeeklySession> getSessionsByStatusOnly(WeeklySessionStatus status);

    // 주차별 수업 상세 조회
    WeeklySession getSession(Long inviteId, Integer weekNo);

    // 수업 진행률 조회 (완료된 주차 수 / 전체 주차 수)
    double getProgressRate(Long inviteId);

    // 수업 진행률 상세 조회
    WeeklySessionProgressResponse getProgress(String inviteCd);

    /**
     * 초대 코드에 대한 주차별 수업 일괄 생성
     * - 해당 수업의 커리큘럼에 정의된 CurriculumWeek의 week_no를 사용하여 WeeklySession 생성
     * - 기존에 생성된 WeeklySession이 있으면 건너뜀
     */
    void createWeeklySessionsForInvite(Long inviteId);
}
