-- =====================================================================
-- 주차별 퀴즈 기능 — 참고 DDL / 인덱스
-- 테이블 자체는 ddl-auto=update 로 자동 생성됨. 아래 인덱스는 수동 적용 권장.
-- 운영 반영 시 한꺼번에 실행할 것.
-- =====================================================================

-- ---------------------------------------------------------------------
-- [필수/즉시] 기존 FK 제약 제거
-- Quiz/QuizSubmission 이 weekly_session / class / user 를 FK 참조하면
-- 반·세션·유저 삭제가 막힌다(FK 위반). 엔티티에서 연관관계를 제거했지만
-- ddl-auto=update 는 이미 생성된 FK 를 자동으로 지우지 않으므로 수동 삭제 필요.
--
-- 1) 먼저 실제 FK 이름을 확인:
--    SELECT constraint_name, table_name, referenced_table_name
--    FROM information_schema.key_column_usage
--    WHERE table_schema = 'cinemax'
--      AND table_name IN ('tbl_quiz','tbl_quiz_submission')
--      AND referenced_table_name IS NOT NULL;
--
-- 2) cinemax 개발서버에서 확인된 실제 FK 이름 (2026-07-15 기준):
ALTER TABLE tbl_quiz            DROP FOREIGN KEY FK3i8xjp100pi4q72x9xqelb30m;  -- weekly_session
ALTER TABLE tbl_quiz            DROP FOREIGN KEY FKgp6rd2j33hilfnyb7f1bs26if;  -- class
ALTER TABLE tbl_quiz_submission DROP FOREIGN KEY FK5y1gf39gpth4l4kp7lf7ihc0r;  -- class
ALTER TABLE tbl_quiz_submission DROP FOREIGN KEY FKpfpv1fj1vexu7w68d4vib41c4;  -- quiz
ALTER TABLE tbl_quiz_submission DROP FOREIGN KEY FKqhal335iulntalulgrcclyo1d;  -- user
--    (FK 이름은 환경마다 다를 수 있으니 위 1) SELECT 로 재확인 후 실행)
-- ---------------------------------------------------------------------


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
