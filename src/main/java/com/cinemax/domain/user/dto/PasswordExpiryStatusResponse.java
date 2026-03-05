package com.cinemax.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 비밀번호 만료 상태 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "비밀번호 만료 상태 응답")
public class PasswordExpiryStatusResponse {

    @Schema(description = "비밀번호 만료 여부", example = "false")
    private Boolean isExpired;

    @Schema(description = "비밀번호 만료까지 남은 일수", example = "45")
    private Long daysUntilExpiry;

    @Schema(description = "마지막 비밀번호 변경 일시", example = "2025-05-12T10:30:00")
    private LocalDateTime lastPasswordChangedAt;

    @Schema(description = "비밀번호 만료 예정일", example = "2025-11-12T10:30:00")
    private LocalDateTime expiryDate;

    @Schema(description = "경고 메시지", example = "비밀번호를 30일 이내에 변경해주세요.")
    private String warningMessage;

    public static PasswordExpiryStatusResponse from(com.cinemax.domain.user.entity.User user) {
        boolean isExpired = user.isPasswordExpired();
        long daysUntilExpiry = user.getDaysUntilPasswordExpiry();
        LocalDateTime lastChanged = user.getLastPasswordChangedAt();
        LocalDateTime expiryDate = lastChanged != null ? lastChanged.plusMonths(6) : null;

        String warningMessage = null;
        if (isExpired) {
            warningMessage = "비밀번호 변경 주기가 경과했습니다. 보안을 위해 즉시 변경해주세요.";
        } else if (daysUntilExpiry <= 30 && daysUntilExpiry > 0) {
            warningMessage = String.format("비밀번호를 %d일 이내에 변경해주세요.", daysUntilExpiry);
        } else if (daysUntilExpiry <= 0) {
            warningMessage = "비밀번호 변경 주기가 경과했습니다. 보안을 위해 즉시 변경해주세요.";
        }

        return PasswordExpiryStatusResponse.builder()
                .isExpired(isExpired)
                .daysUntilExpiry(daysUntilExpiry)
                .lastPasswordChangedAt(lastChanged)
                .expiryDate(expiryDate)
                .warningMessage(warningMessage)
                .build();
    }
}
