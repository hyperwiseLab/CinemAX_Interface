package com.cinemax.domain.classes.dto;

import com.cinemax.domain.classes.entity.ClassInvite;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 수업 초대 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClassInviteResponse {

    private Long inviteId;
    private String inviteCd;
    private String qrCd;
    private Boolean activeYn;
    private Long classId;
    private LocalDateTime createDt;
    private LocalDateTime updateDt;

    /**
     * Entity -> DTO 변환
     */
    public static ClassInviteResponse from(ClassInvite classInvite) {
        return ClassInviteResponse.builder()
                .inviteId(classInvite.getInviteId())
                .inviteCd(classInvite.getInviteCd())
                .qrCd(classInvite.getQrCd())
                .activeYn(classInvite.getActiveYn())
                .classId(classInvite.getClassEntity().getClassId())
                .createDt(classInvite.getCreateDt())
                .updateDt(classInvite.getUpdateDt())
                .build();
    }
}
