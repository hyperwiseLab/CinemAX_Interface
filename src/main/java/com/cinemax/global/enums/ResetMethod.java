package com.cinemax.global.enums;

public enum ResetMethod {
    EMAIL("이메일"),
    SMS("SMS");

    private final String description;

    ResetMethod(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}