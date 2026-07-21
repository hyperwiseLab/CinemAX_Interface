-- 사이클별 평가 점수 테이블
--
-- 배경: tbl_work_log 는 주차당 점수 1쌍(difficulty_level/proficiency_level)만 저장했다.
--       그래서 "과제별 평가" 화면이 사이클 3개에 같은 점수를 반복 출력했다.
--       이 테이블이 사이클별 원본을 보관하고, 주차 점수는 그 평균으로 파생된다.
--
-- 주의: 이 프로젝트는 spring.jpa.hibernate.ddl-auto=update 라서
--       애플리케이션을 띄우면 이 테이블이 자동 생성된다.
--       이 스크립트는 수동 생성용 / 스키마 문서용이다. 이미 있으면 건너뛴다.

CREATE TABLE IF NOT EXISTS `tbl_work_log_cycle_score` (
  `work_log_cycle_score_id` bigint(20)  NOT NULL AUTO_INCREMENT,
  `create_dt`               datetime(6) NOT NULL,
  `update_dt`               datetime(6) NOT NULL,
  `work_log_id`             bigint(20)  DEFAULT NULL,
  `cycle_id`                bigint(20)  NOT NULL,
  `concept_score`           int(11)     NOT NULL COMMENT '개념 이해도 1~5',
  `application_score`       int(11)     NOT NULL COMMENT '코드 활용도 1~5',
  `order_no`                int(11)     NOT NULL COMMENT '화면 표시 순서',
  PRIMARY KEY (`work_log_cycle_score_id`),
  UNIQUE KEY `UK_WORK_LOG_CYCLE` (`work_log_id`, `cycle_id`),
  KEY `IDX_WLCS_CYCLE` (`cycle_id`),
  CONSTRAINT `FK_WLCS_WORK_LOG` FOREIGN KEY (`work_log_id`)
      REFERENCES `tbl_work_log` (`work_log_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- cycle_id 에 FK 를 걸지 않는 이유:
--   커리큘럼 개편으로 사이클을 지울 때 과거 학습 기록이 삭제를 막으면 안 된다.
--   tbl_task 도 같은 이유로 cycle_id 를 FK 없는 스칼라로 둔다.
--   대신 조회 성능을 위해 IDX_WLCS_CYCLE 인덱스만 둔다.
