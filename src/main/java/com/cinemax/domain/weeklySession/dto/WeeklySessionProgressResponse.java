package com.cinemax.domain.weeklySession.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 주차별 수업 진행률 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeeklySessionProgressResponse {

    private Long inviteId;
    private String inviteCd;
    private Integer totalWeeks;
    private Integer completedWeeks;
    private Integer inProgressWeeks;
    private Integer pendingWeeks;
    private Double progressRate;
    private List<WeeklySessionResponse> sessions;

    // 진행률 계산
    public static WeeklySessionProgressResponse of(Long inviteId,
                                                   String inviteCd,
                                                   Integer totalWeeks, 
                                                   Integer completedWeeks, 
                                                   Integer inProgressWeeks, 
                                                   Integer pendingWeeks,
                                                   List<WeeklySessionResponse> sessions) {
        double progressRate = totalWeeks > 0 ? (double) completedWeeks / totalWeeks * 100 : 0.0;
        
        return WeeklySessionProgressResponse.builder()
                .inviteId(inviteId)
                .inviteCd(inviteCd)
                .totalWeeks(totalWeeks)
                .completedWeeks(completedWeeks)
                .inProgressWeeks(inProgressWeeks)
                .pendingWeeks(pendingWeeks)
                .progressRate(Math.round(progressRate * 100.0) / 100.0)
                .sessions(sessions)
                .build();
    }
}
