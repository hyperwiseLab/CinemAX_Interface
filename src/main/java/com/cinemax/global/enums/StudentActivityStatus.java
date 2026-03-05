package com.cinemax.global.enums;

import lombok.Getter;

@Getter
public enum StudentActivityStatus {

    //  🟢 활동중: 최근 5분 이내 활동
    ACTIVE("활동중", "🟢"),

    // 🔴 도움필요: 5분 이상 진도 없음 또는 테스트 3회 실패
    NEED_HELP("도움필요", "🔴"),

    // 🟡 대기중: 로그인했으나 활동 없음
    IDLE("대기중", "🟡"),

    // ✅ 완료: 해당 주차 모든 테스트 통과
    COMPLETED("완료", "✅");

    private final String description;
    private final String emoji;

    StudentActivityStatus(String description, String emoji) {
        this.description = description;
        this.emoji = emoji;
    }

    public String getDescription() {
        return description;
    }

    public String getEmoji() {
        return emoji;
    }

    public String getDisplayName() {
        return emoji + " " + description;
    }
}
