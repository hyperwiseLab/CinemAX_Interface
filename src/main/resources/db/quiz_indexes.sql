-- =====================================================================
-- 주차별 퀴즈 기능 — 참고 DDL / 인덱스
-- 테이블 자체는 ddl-auto=update 로 자동 생성됨. 아래 인덱스는 수동 적용 권장.
-- 운영 반영 시 한꺼번에 실행할 것.
-- =====================================================================

-- 반+주차로 퀴즈 조회 (관리/응시 진입의 핵심 경로)
CREATE INDEX IDX_QUIZ_CLASS_WEEK ON TBL_QUIZ (CLASS_ID, WEEK_NO);
CREATE INDEX IDX_QUIZ_STATUS     ON TBL_QUIZ (STATUS);

-- 문항 -> 퀴즈, 보기 -> 문항
CREATE INDEX IDX_QUIZ_QUESTION_QUIZ    ON TBL_QUIZ_QUESTION (QUIZ_ID);
CREATE INDEX IDX_QUIZ_OPTION_QUESTION  ON TBL_QUIZ_OPTION (QUESTION_ID);

-- 결과 집계(반 전체)
CREATE INDEX IDX_QUIZ_SUB_CLASS ON TBL_QUIZ_SUBMISSION (CLASS_ID);

-- "최초 제출만 기록" 정책을 DB 레벨에서 보장 (같은 학생이 같은 퀴즈에 1건만)
CREATE UNIQUE INDEX UK_QUIZ_SUB_USER ON TBL_QUIZ_SUBMISSION (QUIZ_ID, USER_ID);
