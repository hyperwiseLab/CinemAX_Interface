-- eduverse.tbl_curriculum definition

CREATE TABLE `tbl_curriculum` (
                                  `cur_id` bigint(20) NOT NULL AUTO_INCREMENT,
                                  `create_dt` datetime(6) NOT NULL,
                                  `update_dt` datetime(6) NOT NULL,
                                  `description` varchar(255) DEFAULT NULL,
                                  `duration_weeks` int(11) NOT NULL,
                                  `lang` varchar(100) NOT NULL,
                                  `name` varchar(100) NOT NULL,
                                  `use_yn` bit(1) NOT NULL,
                                  PRIMARY KEY (`cur_id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


-- eduverse.tbl_file definition

CREATE TABLE `tbl_file` (
                            `file_id` bigint(20) NOT NULL AUTO_INCREMENT,
                            `create_dt` datetime(6) NOT NULL,
                            `update_dt` datetime(6) NOT NULL,
                            `org_file_nm` varchar(200) NOT NULL,
                            `save_file_nm` varchar(200) NOT NULL,
                            `use_yn` bit(1) NOT NULL,
                            PRIMARY KEY (`file_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


-- eduverse.tbl_lecture_section definition

CREATE TABLE `tbl_lecture_section` (
                                       `lecture_id` bigint(20) NOT NULL,
                                       `lecture_section_id` bigint(20) NOT NULL,
                                       `create_dt` datetime(6) NOT NULL,
                                       `heading` varchar(200) NOT NULL,
                                       `lecture_section_code` mediumtext DEFAULT NULL,
                                       `lecture_section_txt` varchar(500) NOT NULL,
                                       PRIMARY KEY (`lecture_id`,`lecture_section_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


-- eduverse.tbl_syntax definition

CREATE TABLE `tbl_syntax` (
                              `syntax_id` bigint(20) NOT NULL AUTO_INCREMENT,
                              `category` varchar(100) DEFAULT NULL,
                              `create_dt` datetime(6) NOT NULL,
                              `description` varchar(500) DEFAULT NULL,
                              `name` varchar(200) NOT NULL,
                              `syntax_key` varchar(100) NOT NULL,
                              PRIMARY KEY (`syntax_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


-- eduverse.tbl_task definition

CREATE TABLE `tbl_task` (
                            `cur_id` bigint(20) NOT NULL,
                            `cycle_id` bigint(20) NOT NULL,
                            `syntax_id` bigint(20) NOT NULL,
                            `task_id` bigint(20) NOT NULL,
                            `week_no` int(11) NOT NULL,
                            `create_dt` datetime(6) NOT NULL,
                            `update_dt` datetime(6) NOT NULL,
                            `config_json` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL CHECK (json_valid(`config_json`)),
                            `order_no` int(11) NOT NULL,
                            `task_mode` enum('ADVANCE','EASY') DEFAULT NULL,
                            PRIMARY KEY (`cur_id`,`cycle_id`,`syntax_id`,`task_id`,`week_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


-- eduverse.tbl_user definition

CREATE TABLE `tbl_user` (
                            `user_id` bigint(20) NOT NULL AUTO_INCREMENT,
                            `create_dt` datetime(6) NOT NULL,
                            `update_dt` datetime(6) NOT NULL,
                            `email` varchar(100) NOT NULL,
                            `marketing_agreed` bit(1) DEFAULT NULL,
                            `must_change_password` bit(1) DEFAULT NULL,
                            `name` varchar(100) NOT NULL,
                            `password_hash` varchar(150) NOT NULL,
                            `privacy_agreed` bit(1) DEFAULT NULL,
                            `role` enum('ADMIN','PROFESSOR','STUDENT') NOT NULL,
                            `status` tinyint(4) DEFAULT NULL,
                            `student_num` varchar(255) NOT NULL,
                            `terms_agreed` bit(1) DEFAULT NULL,
                            `PROFILE_IMAGE_URL` varchar(500) DEFAULT NULL,
                            PRIMARY KEY (`user_id`),
                            UNIQUE KEY `UKnpn1wf1yu1g5rjohbek375pp1` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


-- eduverse.tbl_admin_log definition

CREATE TABLE `tbl_admin_log` (
                                 `admin_log_id` bigint(20) NOT NULL AUTO_INCREMENT,
                                 `create_dt` datetime(6) NOT NULL,
                                 `update_dt` datetime(6) NOT NULL,
                                 `action` enum('CLASS_DELETE','USER_SUSPEND') NOT NULL,
                                 `target_id` bigint(20) NOT NULL,
                                 `target_type` enum('CLASS','ENROLL','USER') NOT NULL,
                                 `user_id` bigint(20) NOT NULL,
                                 PRIMARY KEY (`admin_log_id`),
                                 KEY `FKrcw9jhvao4mhwru7fi7u4vxpf` (`user_id`),
                                 CONSTRAINT `FKrcw9jhvao4mhwru7fi7u4vxpf` FOREIGN KEY (`user_id`) REFERENCES `tbl_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


-- eduverse.tbl_assign definition

CREATE TABLE `tbl_assign` (
                              `assign_id` bigint(20) NOT NULL,
                              `task_id` bigint(20) NOT NULL,
                              `create_dt` datetime(6) NOT NULL,
                              `update_dt` datetime(6) NOT NULL,
                              `assign_content` varchar(1000) NOT NULL,
                              `character_img` varchar(500) NOT NULL,
                              `character_path` varchar(1000) NOT NULL,
                              `cur_id` bigint(20) DEFAULT NULL,
                              `cycle_id` bigint(20) DEFAULT NULL,
                              `sub_title` varchar(255) NOT NULL,
                              `syntax_id` bigint(20) DEFAULT NULL,
                              `title` varchar(255) NOT NULL,
                              `week_no` int(11) DEFAULT NULL,
                              PRIMARY KEY (`assign_id`,`task_id`),
                              KEY `FKbu36bgww00ntofdj8fjei4xur` (`cur_id`,`cycle_id`,`syntax_id`,`task_id`,`week_no`),
                              CONSTRAINT `FKbu36bgww00ntofdj8fjei4xur` FOREIGN KEY (`cur_id`, `cycle_id`, `syntax_id`, `task_id`, `week_no`) REFERENCES `tbl_task` (`cur_id`, `cycle_id`, `syntax_id`, `task_id`, `week_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


-- eduverse.tbl_auth_log definition

CREATE TABLE `tbl_auth_log` (
                                `auth_log_id` bigint(20) NOT NULL AUTO_INCREMENT,
                                `create_dt` datetime(6) NOT NULL,
                                `update_dt` datetime(6) NOT NULL,
                                `email` varchar(200) DEFAULT NULL,
                                `event` int(11) NOT NULL,
                                `user_id` bigint(20) NOT NULL,
                                PRIMARY KEY (`auth_log_id`),
                                KEY `FKaot3rugx52na7kbj7098c2a8q` (`user_id`),
                                CONSTRAINT `FKaot3rugx52na7kbj7098c2a8q` FOREIGN KEY (`user_id`) REFERENCES `tbl_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


-- eduverse.tbl_brief definition

CREATE TABLE `tbl_brief` (
                             `brief_id` bigint(20) NOT NULL AUTO_INCREMENT,
                             `task_id` bigint(20) NOT NULL,
                             `create_dt` datetime(6) NOT NULL,
                             `update_dt` datetime(6) NOT NULL,
                             `brief_content` varchar(1000) NOT NULL,
                             `character_img` varchar(500) NOT NULL,
                             `character_path` varchar(1000) NOT NULL,
                             `cur_id` bigint(20) DEFAULT NULL,
                             `cycle_id` bigint(20) DEFAULT NULL,
                             `sub_title` varchar(255) NOT NULL,
                             `syntax_id` bigint(20) DEFAULT NULL,
                             `title` varchar(255) NOT NULL,
                             `week_no` int(11) DEFAULT NULL,
                             PRIMARY KEY (`brief_id`),
                             KEY `FK6jmcb3otjhis6sqoqcn3a4pna` (`cur_id`,`cycle_id`,`syntax_id`,`task_id`,`week_no`),
                             CONSTRAINT `FK6jmcb3otjhis6sqoqcn3a4pna` FOREIGN KEY (`cur_id`, `cycle_id`, `syntax_id`, `task_id`, `week_no`) REFERENCES `tbl_task` (`cur_id`, `cycle_id`, `syntax_id`, `task_id`, `week_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


-- eduverse.tbl_class definition

CREATE TABLE `tbl_class` (
                             `class_id` bigint(20) NOT NULL AUTO_INCREMENT,
                             `create_dt` datetime(6) NOT NULL,
                             `update_dt` datetime(6) NOT NULL,
                             `class_nm` varchar(200) NOT NULL,
                             `current_invite_id` varchar(100) DEFAULT NULL,
                             `description` varchar(500) DEFAULT NULL,
                             `term` varchar(20) NOT NULL,
                             `use_yn` bit(1) NOT NULL,
                             `year` int(11) NOT NULL,
                             `cur_id` bigint(20) NOT NULL,
                             `user_id` bigint(20) NOT NULL,
                             PRIMARY KEY (`class_id`),
                             KEY `FK29cnqb0eqo770rc920dkpcxty` (`cur_id`),
                             KEY `FKkc6jdw531j8gdbgj1ks6x3hfg` (`user_id`),
                             CONSTRAINT `FK29cnqb0eqo770rc920dkpcxty` FOREIGN KEY (`cur_id`) REFERENCES `tbl_curriculum` (`cur_id`),
                             CONSTRAINT `FKkc6jdw531j8gdbgj1ks6x3hfg` FOREIGN KEY (`user_id`) REFERENCES `tbl_user` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=35 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


-- eduverse.tbl_class_enroll definition

CREATE TABLE `tbl_class_enroll` (
                                    `enroll_id` bigint(20) NOT NULL AUTO_INCREMENT,
                                    `create_dt` datetime(6) NOT NULL,
                                    `update_dt` datetime(6) NOT NULL,
                                    `status` enum('ACTIVE','WITHDRAWN') NOT NULL,
                                    `withdrawn_dt` datetime(6) DEFAULT NULL,
                                    `class_id` bigint(20) NOT NULL,
                                    `user_id` bigint(20) NOT NULL,
                                    PRIMARY KEY (`enroll_id`),
                                    KEY `FK9w5d6ctgq8lb3sb89e993xnhc` (`class_id`),
                                    KEY `FKpqmohpnw6rd2uwyuiof3bukrc` (`user_id`),
                                    CONSTRAINT `FK9w5d6ctgq8lb3sb89e993xnhc` FOREIGN KEY (`class_id`) REFERENCES `tbl_class` (`class_id`),
                                    CONSTRAINT `FKpqmohpnw6rd2uwyuiof3bukrc` FOREIGN KEY (`user_id`) REFERENCES `tbl_user` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


-- eduverse.tbl_class_invite definition

CREATE TABLE `tbl_class_invite` (
                                    `invite_id` bigint(20) NOT NULL AUTO_INCREMENT,
                                    `create_dt` datetime(6) NOT NULL,
                                    `update_dt` datetime(6) NOT NULL,
                                    `active_yn` bit(1) NOT NULL,
                                    `invite_cd` varchar(20) NOT NULL,
                                    `qr_cd` varchar(500) DEFAULT NULL,
                                    `class_id` bigint(20) NOT NULL,
                                    PRIMARY KEY (`invite_id`),
                                    KEY `FKi5mwyx9owhywoblq1kbhhs27d` (`class_id`),
                                    CONSTRAINT `FKi5mwyx9owhywoblq1kbhhs27d` FOREIGN KEY (`class_id`) REFERENCES `tbl_class` (`class_id`)
) ENGINE=InnoDB AUTO_INCREMENT=32 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


-- eduverse.tbl_curriculum_week definition

CREATE TABLE `tbl_curriculum_week` (
                                       `cur_week_id` bigint(20) NOT NULL AUTO_INCREMENT,
                                       `week_no` int(11) NOT NULL,
                                       `character_nm` varchar(50) DEFAULT NULL,
                                       `content` varchar(500) DEFAULT NULL,
                                       `create_dt` datetime(6) NOT NULL,
                                       `cur_lev` int(11) NOT NULL,
                                       `subtitle` varchar(200) DEFAULT NULL,
                                       `title` varchar(200) NOT NULL,
                                       `CUR_ID` bigint(20) NOT NULL,
                                       PRIMARY KEY (`cur_week_id`),
                                       UNIQUE KEY `UK_curriculum_week_cur_weekno` (`CUR_ID`,`week_no`),
                                       CONSTRAINT `FKnn48lo599x98mrd2whi69s0dj` FOREIGN KEY (`CUR_ID`) REFERENCES `tbl_curriculum` (`cur_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


-- eduverse.tbl_cycle definition

CREATE TABLE `tbl_cycle` (
                             `cur_week_id` bigint(20) NOT NULL,
                             `cycle_id` bigint(20) NOT NULL,
                             `create_dt` datetime(6) NOT NULL,
                             `update_dt` datetime(6) NOT NULL,
                             `cycle_title` varchar(500) DEFAULT NULL,
                             `file_nm` varchar(200) DEFAULT NULL,
                             `SYNTAX_ID` bigint(20) NOT NULL,
                             PRIMARY KEY (`cur_week_id`,`cycle_id`,`SYNTAX_ID`),
                             KEY `FKa6ea6jpb9uueylkb6hd0derwy` (`SYNTAX_ID`),
                             CONSTRAINT `FKa6ea6jpb9uueylkb6hd0derwy` FOREIGN KEY (`SYNTAX_ID`) REFERENCES `tbl_syntax` (`syntax_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


-- eduverse.tbl_email_verification definition

CREATE TABLE `tbl_email_verification` (
                                          `varification_id` bigint(20) NOT NULL,
                                          `attempt_count` tinyint(4) NOT NULL,
                                          `code` varchar(6) NOT NULL,
                                          `create_dt` datetime(6) NOT NULL,
                                          `email` varchar(200) NOT NULL,
                                          `expires_at` datetime(6) NOT NULL,
                                          `user_id` bigint(20) DEFAULT NULL,
                                          `verified_at` datetime(6) DEFAULT NULL,
                                          PRIMARY KEY (`varification_id`),
                                          KEY `FKlxx3y6tnu0emc209qknlaixbp` (`user_id`),
                                          CONSTRAINT `FKlxx3y6tnu0emc209qknlaixbp` FOREIGN KEY (`user_id`) REFERENCES `tbl_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


-- eduverse.tbl_feedback definition

CREATE TABLE `tbl_feedback` (
                                `feedback_id` bigint(20) NOT NULL,
                                `task_id` bigint(20) NOT NULL,
                                `create_dt` datetime(6) NOT NULL,
                                `update_dt` datetime(6) NOT NULL,
                                `character_img` varchar(500) NOT NULL,
                                `character_path` varchar(1000) NOT NULL,
                                `cur_id` bigint(20) DEFAULT NULL,
                                `cycle_id` bigint(20) DEFAULT NULL,
                                `feedback_content` varchar(1000) NOT NULL,
                                `feedback_type` enum('FAILURE_LOGICAL','FAILURE_RUNTIME','SUCCESS') NOT NULL,
                                `sub_title` varchar(255) NOT NULL,
                                `syntax_id` bigint(20) DEFAULT NULL,
                                `title` varchar(255) NOT NULL,
                                `week_no` int(11) DEFAULT NULL,
                                PRIMARY KEY (`feedback_id`,`task_id`),
                                KEY `FKlbs2xxhmewbmhiltalr0ivfcl` (`cur_id`,`cycle_id`,`syntax_id`,`task_id`,`week_no`),
                                CONSTRAINT `FKlbs2xxhmewbmhiltalr0ivfcl` FOREIGN KEY (`cur_id`, `cycle_id`, `syntax_id`, `task_id`, `week_no`) REFERENCES `tbl_task` (`cur_id`, `cycle_id`, `syntax_id`, `task_id`, `week_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


-- eduverse.tbl_hint definition

CREATE TABLE `tbl_hint` (
                            `hint_id` bigint(20) NOT NULL,
                            `task_id` bigint(20) NOT NULL,
                            `create_dt` datetime(6) NOT NULL,
                            `update_dt` datetime(6) NOT NULL,
                            `content` varchar(500) NOT NULL,
                            `cur_id` bigint(20) DEFAULT NULL,
                            `cycle_id` bigint(20) DEFAULT NULL,
                            `syntax_id` bigint(20) DEFAULT NULL,
                            `title` varchar(255) NOT NULL,
                            `video_url` varchar(255) DEFAULT NULL,
                            `week_no` int(11) DEFAULT NULL,
                            PRIMARY KEY (`hint_id`,`task_id`),
                            KEY `FKe0vcm5t7gkfcw06gbom8pdlyy` (`cur_id`,`cycle_id`,`syntax_id`,`task_id`,`week_no`),
                            CONSTRAINT `FKe0vcm5t7gkfcw06gbom8pdlyy` FOREIGN KEY (`cur_id`, `cycle_id`, `syntax_id`, `task_id`, `week_no`) REFERENCES `tbl_task` (`cur_id`, `cycle_id`, `syntax_id`, `task_id`, `week_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


-- eduverse.tbl_lecture definition

CREATE TABLE `tbl_lecture` (
                               `lecture_id` bigint(20) NOT NULL,
                               `task_id` bigint(20) NOT NULL,
                               `create_dt` datetime(6) NOT NULL,
                               `update_dt` datetime(6) NOT NULL,
                               `character_img` varchar(500) NOT NULL,
                               `character_path` varchar(500) NOT NULL,
                               `cur_id` bigint(20) DEFAULT NULL,
                               `cycle_id` bigint(20) DEFAULT NULL,
                               `key_takeaway` varchar(500) NOT NULL,
                               `sandbox_code` varchar(500) NOT NULL,
                               `syntax_id` bigint(20) DEFAULT NULL,
                               `title` varchar(100) NOT NULL,
                               `week_no` int(11) DEFAULT NULL,
                               PRIMARY KEY (`lecture_id`,`task_id`),
                               KEY `FK7kwbq9dekqfo01ihtyq2fq35f` (`cur_id`,`cycle_id`,`syntax_id`,`task_id`,`week_no`),
                               CONSTRAINT `FK7kwbq9dekqfo01ihtyq2fq35f` FOREIGN KEY (`cur_id`, `cycle_id`, `syntax_id`, `task_id`, `week_no`) REFERENCES `tbl_task` (`cur_id`, `cycle_id`, `syntax_id`, `task_id`, `week_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


-- eduverse.tbl_mentor_dialog definition

CREATE TABLE `tbl_mentor_dialog` (
                                     `cycle_id` bigint(20) NOT NULL,
                                     `dialog_id` bigint(20) NOT NULL,
                                     `task_id` bigint(20) NOT NULL,
                                     `create_dt` datetime(6) NOT NULL,
                                     `update_dt` datetime(6) NOT NULL,
                                     `content` varchar(1000) NOT NULL,
                                     `cur_id` bigint(20) DEFAULT NULL,
                                     `dialog_sort` int(11) NOT NULL,
                                     `dialog_type` int(11) NOT NULL,
                                     `mentor_type` int(11) NOT NULL,
                                     `syntax_id` bigint(20) DEFAULT NULL,
                                     `week_no` int(11) DEFAULT NULL,
                                     PRIMARY KEY (`cycle_id`,`dialog_id`,`task_id`),
                                     KEY `FKb29fpu336ns0iyqkje6f1ka4m` (`cur_id`,`cycle_id`,`syntax_id`,`task_id`,`week_no`),
                                     CONSTRAINT `FKb29fpu336ns0iyqkje6f1ka4m` FOREIGN KEY (`cur_id`, `cycle_id`, `syntax_id`, `task_id`, `week_no`) REFERENCES `tbl_task` (`cur_id`, `cycle_id`, `syntax_id`, `task_id`, `week_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


-- eduverse.tbl_notification definition

CREATE TABLE `tbl_notification` (
                                    `notification_id` bigint(20) NOT NULL AUTO_INCREMENT,
                                    `create_dt` datetime(6) DEFAULT NULL,
                                    `payload_content` varchar(500) DEFAULT NULL,
                                    `read_dt` datetime(6) DEFAULT NULL,
                                    `type` enum('ADMIN_TO_PROFESSOR_ACCOUNT_CREATED','ADMIN_TO_PROFESSOR_SYSTEM_ANNOUNCEMENT','ADMIN_TO_PROFESSOR_TEMP_PASSWORD','ADMIN_TO_STUDENT_SYSTEM_ANNOUNCEMENT','ADMIN_TO_STUDENT_TEMP_PASSWORD','PROFESSOR_TO_STUDENT_ASSIGNMENT_SUBMISSION','PROFESSOR_TO_STUDENT_SCORE_ENTRY','PROFESSOR_TO_STUDENT_SUBMISSION_DEADLINE','STUDENT_TO_ADMIN_ACCOUNT_REQUEST','STUDENT_TO_ADMIN_ERROR_REPORT','STUDENT_TO_PROFESSOR_ASSIGNMENT_COMPLETION','STUDENT_TO_PROFESSOR_ATTENDANCE_CHECK','STUDENT_TO_PROFESSOR_CURRICULUM_REGISTER','STUDENT_TO_PROFESSOR_GRADE_INQUIRY','STUDENT_TO_PROFESSOR_PROGRESS_INQUIRY','STUDENT_TO_PROFESSOR_QUESTION_ASKED') NOT NULL,
                                    `user_id` bigint(20) NOT NULL,
                                    PRIMARY KEY (`notification_id`),
                                    KEY `FK17xlvi4d2o1r18carkq5kmd3c` (`user_id`),
                                    CONSTRAINT `FK17xlvi4d2o1r18carkq5kmd3c` FOREIGN KEY (`user_id`) REFERENCES `tbl_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


-- eduverse.tbl_password_reset_request definition

CREATE TABLE `tbl_password_reset_request` (
                                              `request_id` bigint(20) NOT NULL AUTO_INCREMENT,
                                              `create_dt` datetime(6) NOT NULL,
                                              `update_dt` datetime(6) NOT NULL,
                                              `attempt_count` int(11) NOT NULL,
                                              `email` varchar(200) NOT NULL,
                                              `expires_at` datetime(6) NOT NULL,
                                              `reset_method` varchar(20) NOT NULL,
                                              `status` varchar(20) NOT NULL,
                                              `used_at` datetime(6) DEFAULT NULL,
                                              `verification_code` varchar(6) NOT NULL,
                                              `user_id` bigint(20) DEFAULT NULL,
                                              PRIMARY KEY (`request_id`),
                                              KEY `FK9w092ogavxn1kp1pc3kc7a7va` (`user_id`),
                                              CONSTRAINT `FK9w092ogavxn1kp1pc3kc7a7va` FOREIGN KEY (`user_id`) REFERENCES `tbl_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


-- eduverse.tbl_test_case definition

CREATE TABLE `tbl_test_case` (
                                 `cycle_id` bigint(20) NOT NULL,
                                 `task_id` bigint(20) NOT NULL,
                                 `test_case_id` bigint(20) NOT NULL,
                                 `create_dt` datetime(6) NOT NULL,
                                 `update_dt` datetime(6) NOT NULL,
                                 `cur_id` bigint(20) DEFAULT NULL,
                                 `expected_output` varchar(500) NOT NULL,
                                 `input_text` varchar(500) NOT NULL,
                                 `start_code` mediumtext NOT NULL,
                                 `syntax_id` bigint(20) DEFAULT NULL,
                                 `test_code` mediumtext NOT NULL,
                                 `week_no` int(11) DEFAULT NULL,
                                 `weight` int(11) NOT NULL,
                                 PRIMARY KEY (`cycle_id`,`task_id`,`test_case_id`),
                                 KEY `FKlqkg3aakb49f99j3hhqfw3gfc` (`cur_id`,`cycle_id`,`syntax_id`,`task_id`,`week_no`),
                                 CONSTRAINT `FKlqkg3aakb49f99j3hhqfw3gfc` FOREIGN KEY (`cur_id`, `cycle_id`, `syntax_id`, `task_id`, `week_no`) REFERENCES `tbl_task` (`cur_id`, `cycle_id`, `syntax_id`, `task_id`, `week_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


-- eduverse.tbl_weekly_session definition

CREATE TABLE `tbl_weekly_session` (
                                      `weekly_session_id` bigint(20) NOT NULL AUTO_INCREMENT,
                                      `create_dt` datetime(6) NOT NULL,
                                      `update_dt` datetime(6) NOT NULL,
                                      `auto_closed` bit(1) NOT NULL,
                                      `start_dt` datetime(6) DEFAULT NULL,
                                      `status` enum('COMPLETED','IN_PROGRESS','PENDING') NOT NULL,
                                      `week_no` int(11) NOT NULL,
                                      `INVITE_ID` bigint(20) NOT NULL,
                                      PRIMARY KEY (`weekly_session_id`),
                                      UNIQUE KEY `UK_weekly_session_invite_week` (`INVITE_ID`,`week_no`),
                                      CONSTRAINT `FK6xefgfvkw9i0grwhilsohl666` FOREIGN KEY (`INVITE_ID`) REFERENCES `tbl_class_invite` (`invite_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


-- eduverse.tbl_work_log definition

CREATE TABLE `tbl_work_log` (
                                `work_log_id` bigint(20) NOT NULL AUTO_INCREMENT,
                                `log_date` date NOT NULL,
                                `user_id` bigint(20) NOT NULL,
                                `weekly_session_id` bigint(20) NOT NULL,
                                `create_dt` datetime(6) NOT NULL,
                                `update_dt` datetime(6) NOT NULL,
                                `achievements` text DEFAULT NULL,
                                `content` text DEFAULT NULL,
                                `difficulty_level` int(11) DEFAULT NULL,
                                `notes` text DEFAULT NULL,
                                `work_hours` decimal(5,2) DEFAULT NULL,
                                PRIMARY KEY (`work_log_id`),
                                UNIQUE KEY `UK_work_log_date_user_session` (`log_date`,`user_id`,`weekly_session_id`),
                                KEY `FKjbwfe2p39125xnmfpgu9bn7cl` (`user_id`),
                                KEY `FKck8531rkiycie72yas9piab91` (`weekly_session_id`),
                                CONSTRAINT `FKck8531rkiycie72yas9piab91` FOREIGN KEY (`weekly_session_id`) REFERENCES `tbl_weekly_session` (`weekly_session_id`),
                                CONSTRAINT `FKjbwfe2p39125xnmfpgu9bn7cl` FOREIGN KEY (`user_id`) REFERENCES `tbl_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


-- eduverse.tbl_activity_monitor definition

CREATE TABLE `tbl_activity_monitor` (
                                        `monitor_id` bigint(20) NOT NULL AUTO_INCREMENT,
                                        `invite_id` bigint(20) NOT NULL,
                                        `weekly_session_id` bigint(20) NOT NULL,
                                        `create_dt` datetime(6) NOT NULL,
                                        `update_dt` datetime(6) NOT NULL,
                                        `last_action` enum('ACTIVE','CODE_RUN','CODE_SAVE','COMPLETED','HELP_NEEDED','HINT_VIEW','IDLE','LECTURE_VIEW','LOGIN','LOGOUT','PAGE_VIEW','QUESTION_SUBMIT','TEST_FAIL','TEST_PASS','TEST_RUN') NOT NULL,
                                        `last_seen_dt` datetime(6) NOT NULL,
                                        PRIMARY KEY (`monitor_id`),
                                        UNIQUE KEY `UK_activity_monitor_session_invite` (`weekly_session_id`,`invite_id`),
                                        KEY `FKh7okn69i7l3v8aga33b7e4qew` (`weekly_session_id`),
                                        CONSTRAINT `FKh7okn69i7l3v8aga33b7e4qew` FOREIGN KEY (`weekly_session_id`) REFERENCES `tbl_weekly_session` (`weekly_session_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


-- eduverse.tbl_class_submit definition

CREATE TABLE `tbl_class_submit` (
                                    `class_id` bigint(20) NOT NULL,
                                    `cycle_id` bigint(20) NOT NULL,
                                    `submit_id` bigint(20) NOT NULL,
                                    `task_id` bigint(20) NOT NULL,
                                    `weekly_session_id` bigint(20) NOT NULL,
                                    `create_dt` datetime(6) NOT NULL,
                                    `update_dt` datetime(6) NOT NULL,
                                    `cur_id` bigint(20) DEFAULT NULL,
                                    `detail_json` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL CHECK (json_valid(`detail_json`)),
                                    `is_first_eval` bit(1) NOT NULL,
                                    `result` bit(1) DEFAULT NULL,
                                    `score` decimal(5,2) DEFAULT NULL,
                                    `submit_at` datetime(6) NOT NULL,
                                    `submit_num` int(11) DEFAULT NULL,
                                    `submit_yn` bit(1) NOT NULL,
                                    `syntax_id` bigint(20) DEFAULT NULL,
                                    `week_no` int(11) DEFAULT NULL,
                                    PRIMARY KEY (`class_id`,`cycle_id`,`submit_id`,`task_id`,`weekly_session_id`),
                                    KEY `FKarxydk4qqqtjis72073lw4835` (`weekly_session_id`),
                                    KEY `FK6t1ve4d78xn81s8i9l4nnk5om` (`cur_id`,`cycle_id`,`syntax_id`,`task_id`,`week_no`),
                                    CONSTRAINT `FK6t1ve4d78xn81s8i9l4nnk5om` FOREIGN KEY (`cur_id`, `cycle_id`, `syntax_id`, `task_id`, `week_no`) REFERENCES `tbl_task` (`cur_id`, `cycle_id`, `syntax_id`, `task_id`, `week_no`),
                                    CONSTRAINT `FKarxydk4qqqtjis72073lw4835` FOREIGN KEY (`weekly_session_id`) REFERENCES `tbl_weekly_session` (`weekly_session_id`),
                                    CONSTRAINT `FKiarydmdd2v4i1if3vkxlx1fjh` FOREIGN KEY (`class_id`) REFERENCES `tbl_class` (`class_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


-- eduverse.tbl_code_peek_penalty definition

CREATE TABLE `tbl_code_peek_penalty` (
                                         `penalty_id` bigint(20) NOT NULL AUTO_INCREMENT,
                                         `invite_id` bigint(20) NOT NULL,
                                         `user_id` bigint(20) NOT NULL,
                                         `weekly_session_id` bigint(20) NOT NULL,
                                         `create_dt` datetime(6) NOT NULL,
                                         `update_dt` datetime(6) NOT NULL,
                                         `last_peek_dt` datetime(6) DEFAULT NULL,
                                         `peek_count` int(11) NOT NULL,
                                         `penalty_points` int(11) NOT NULL,
                                         PRIMARY KEY (`penalty_id`),
                                         UNIQUE KEY `UK_code_peek_penalty_user_session` (`user_id`,`weekly_session_id`,`invite_id`),
                                         KEY `FKs6ekq9rg5d0n6hjp6eu581lyv` (`user_id`),
                                         KEY `FKbiawasuq31q5t6q0jrmg9bld5` (`weekly_session_id`),
                                         CONSTRAINT `FKbiawasuq31q5t6q0jrmg9bld5` FOREIGN KEY (`weekly_session_id`) REFERENCES `tbl_weekly_session` (`weekly_session_id`),
                                         CONSTRAINT `FKs6ekq9rg5d0n6hjp6eu581lyv` FOREIGN KEY (`user_id`) REFERENCES `tbl_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


-- eduverse.tbl_code_snapshot definition

CREATE TABLE `tbl_code_snapshot` (
                                     `code_id` bigint(20) NOT NULL,
                                     `cycle_id` bigint(20) NOT NULL,
                                     `task_id` bigint(20) NOT NULL,
                                     `user_id` bigint(20) NOT NULL,
                                     `weekly_session_id` bigint(20) NOT NULL,
                                     `create_dt` datetime(6) NOT NULL,
                                     `update_dt` datetime(6) NOT NULL,
                                     `content` mediumtext NOT NULL,
                                     `cur_id` bigint(20) DEFAULT NULL,
                                     `lang` enum('CLANG','CSHARP','HTML','JAVA','JAVASCRIPT','PYTHON') NOT NULL,
                                     `save_at` datetime(6) NOT NULL,
                                     `syntax_id` bigint(20) DEFAULT NULL,
                                     `week_no` int(11) DEFAULT NULL,
                                     PRIMARY KEY (`code_id`,`cycle_id`,`task_id`,`user_id`,`weekly_session_id`),
                                     KEY `FKmss7lntb7oyj5yt33birgwc53` (`user_id`),
                                     KEY `FK1btj5rv98htdyewwwgt3b1u8j` (`weekly_session_id`),
                                     KEY `FKhakk0vf711nsc668sn0eoebyr` (`cur_id`,`cycle_id`,`syntax_id`,`task_id`,`week_no`),
                                     CONSTRAINT `FK1btj5rv98htdyewwwgt3b1u8j` FOREIGN KEY (`weekly_session_id`) REFERENCES `tbl_weekly_session` (`weekly_session_id`),
                                     CONSTRAINT `FKhakk0vf711nsc668sn0eoebyr` FOREIGN KEY (`cur_id`, `cycle_id`, `syntax_id`, `task_id`, `week_no`) REFERENCES `tbl_task` (`cur_id`, `cycle_id`, `syntax_id`, `task_id`, `week_no`),
                                     CONSTRAINT `FKmss7lntb7oyj5yt33birgwc53` FOREIGN KEY (`user_id`) REFERENCES `tbl_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


-- eduverse.tbl_progress definition

CREATE TABLE `tbl_progress` (
                                `progress_id` bigint(20) NOT NULL AUTO_INCREMENT,
                                `class_id` bigint(20) NOT NULL,
                                `weekly_session_id` bigint(20) NOT NULL,
                                `create_dt` datetime(6) NOT NULL,
                                `update_dt` datetime(6) NOT NULL,
                                `activity_status` enum('ACTIVE','COMPLETED','IDLE','NEED_HELP') DEFAULT NULL,
                                `completed_dt` datetime(6) DEFAULT NULL,
                                `last_activity_time` datetime(6) DEFAULT NULL,
                                `progress_pct` decimal(5,2) NOT NULL,
                                `test_fail_count` int(11) DEFAULT NULL,
                                `user_id` bigint(20) NOT NULL,
                                PRIMARY KEY (`progress_id`),
                                UNIQUE KEY `UK_progress_user_class_session` (`user_id`,`class_id`,`weekly_session_id`),
                                KEY `FK8hk6lhruei5nc1myolu20flh3` (`user_id`),
                                KEY `FK2deh35bt6vd96qpoxe4ooqeim` (`weekly_session_id`),
                                CONSTRAINT `FK2deh35bt6vd96qpoxe4ooqeim` FOREIGN KEY (`weekly_session_id`) REFERENCES `tbl_weekly_session` (`weekly_session_id`),
                                CONSTRAINT `FK8hk6lhruei5nc1myolu20flh3` FOREIGN KEY (`user_id`) REFERENCES `tbl_user` (`user_id`),
                                CONSTRAINT `FKeqif638ig9lqor4a2ewlfpoy0` FOREIGN KEY (`class_id`) REFERENCES `tbl_class` (`class_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


-- eduverse.tbl_question definition

CREATE TABLE `tbl_question` (
                                `question_id` bigint(20) NOT NULL AUTO_INCREMENT,
                                `class_id` bigint(20) NOT NULL,
                                `weekly_session_id` bigint(20) NOT NULL,
                                `create_dt` datetime(6) NOT NULL,
                                `update_dt` datetime(6) NOT NULL,
                                `content` varchar(500) NOT NULL,
                                `status` enum('ANSWERED','CLOSED','OPEN') NOT NULL,
                                `title` varchar(200) NOT NULL,
                                `urgency` enum('HIGH','LOW','MEDIUM') NOT NULL,
                                `user_id` bigint(20) NOT NULL,
                                PRIMARY KEY (`question_id`),
                                KEY `FKnfhxi16d0dbr0fovemahofw89` (`user_id`),
                                KEY `FK8eopp7d88bmbb2edby5mmih46` (`weekly_session_id`),
                                KEY `FK5xpvvxv4xomjicwx02cq82d5a` (`class_id`),
                                CONSTRAINT `FK5xpvvxv4xomjicwx02cq82d5a` FOREIGN KEY (`class_id`) REFERENCES `tbl_class` (`class_id`),
                                CONSTRAINT `FK8eopp7d88bmbb2edby5mmih46` FOREIGN KEY (`weekly_session_id`) REFERENCES `tbl_weekly_session` (`weekly_session_id`),
                                CONSTRAINT `FKnfhxi16d0dbr0fovemahofw89` FOREIGN KEY (`user_id`) REFERENCES `tbl_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


-- eduverse.tbl_answer definition

CREATE TABLE `tbl_answer` (
                              `answer_id` bigint(20) NOT NULL AUTO_INCREMENT,
                              `question_id` bigint(20) NOT NULL,
                              `create_dt` datetime(6) NOT NULL,
                              `update_dt` datetime(6) NOT NULL,
                              `content` varchar(500) NOT NULL,
                              `user_id` bigint(20) NOT NULL,
                              PRIMARY KEY (`answer_id`),
                              KEY `FKdkijd523elglmnbl7r6by2wpk` (`question_id`),
                              KEY `FKimmmty6f9rjrmykuvhtvuoeyk` (`user_id`),
                              CONSTRAINT `FKdkijd523elglmnbl7r6by2wpk` FOREIGN KEY (`question_id`) REFERENCES `tbl_question` (`question_id`),
                              CONSTRAINT `FKimmmty6f9rjrmykuvhtvuoeyk` FOREIGN KEY (`user_id`) REFERENCES `tbl_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;