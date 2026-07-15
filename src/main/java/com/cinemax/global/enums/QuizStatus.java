package com.cinemax.global.enums;

import lombok.Getter;

@Getter
public enum QuizStatus {
    DRAFT("초안"),
    REVIEW("검토중"),
    PUBLISHED("공개");

    private final String description;

    QuizStatus(String description) {
        this.description = description;
    }
}
