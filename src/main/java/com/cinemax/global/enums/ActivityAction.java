package com.cinemax.global.enums;

import lombok.Getter;

@Getter
public enum ActivityAction {
    // 기본 상태
    ACTIVE("활동중"),
    IDLE("대기중"),
    HELP_NEEDED("도움 필요"),
    COMPLETED("완료"),

    // 구체적인 활동
    CODE_SAVE("코드 저장"),
    CODE_RUN("코드 실행"),
    TEST_RUN("테스트 실행"),
    TEST_PASS("테스트 통과"),
    TEST_FAIL("테스트 실패"),
    PAGE_VIEW("페이지 조회"),
    LECTURE_VIEW("강의 조회"),
    HINT_VIEW("힌트 조회"),
    QUESTION_SUBMIT("질문 제출"),
    LOGIN("로그인"),
    LOGOUT("로그아웃");

    private final String description;

    ActivityAction(String description) {
        this.description = description;
    }
}
