package com.cinemax.global.enums;

import lombok.Getter;

@Getter
public enum WeeklySessionStatus {
    PENDING("시작 전"),
    IN_PROGRESS("진행 중"),
    COMPLETED("종료됨");

    private final String description;

    WeeklySessionStatus(String description) {
        this.description = description;
    }
}
