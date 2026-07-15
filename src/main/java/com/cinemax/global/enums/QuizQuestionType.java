package com.cinemax.global.enums;

import lombok.Getter;

@Getter
public enum QuizQuestionType {
    MULTIPLE("객관식"),
    OX("O/X");

    private final String description;

    QuizQuestionType(String description) {
        this.description = description;
    }
}
