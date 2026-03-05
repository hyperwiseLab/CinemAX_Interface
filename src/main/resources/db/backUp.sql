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
                            `cycle_id` bigint(20) NOT NULL,
                            `task_id` bigint(20) NOT NULL,
                            `create_dt` datetime(6) NOT NULL,
                            `update_dt` datetime(6) NOT NULL,
                            `config_json` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL CHECK (json_valid(`config_json`)),
                            `order_no` int(11) NOT NULL,
                            `task_mode` enum('ADVANCE','EASY') DEFAULT NULL,
                            PRIMARY KEY (`cycle_id`,`task_id`)
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
                            PRIMARY KEY (`user_id`),
                            UNIQUE KEY `UKnpn1wf1yu1g5rjohbek375pp1` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


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
                              `sub_title` varchar(255) NOT NULL,
                              `title` varchar(255) NOT NULL,
                              `cycle_id` bigint(20) DEFAULT NULL,
                              PRIMARY KEY (`assign_id`,`task_id`),
                              KEY `FKbs2p9thgv2523rxjeamaa5hyg` (`task_id`,`cycle_id`),
                              CONSTRAINT `FKbs2p9thgv2523rxjeamaa5hyg` FOREIGN KEY (`task_id`, `cycle_id`) REFERENCES `tbl_task` (`cycle_id`, `task_id`)
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
                             `brief_id` varchar(255) NOT NULL,
                             `task_id` bigint(20) NOT NULL,
                             `create_dt` datetime(6) NOT NULL,
                             `update_dt` datetime(6) NOT NULL,
                             `brief_content` varchar(1000) NOT NULL,
                             `character_img` varchar(500) NOT NULL,
                             `character_path` varchar(1000) NOT NULL,
                             `sub_title` varchar(255) NOT NULL,
                             `title` varchar(255) NOT NULL,
                             `cycle_id` bigint(20) DEFAULT NULL,
                             PRIMARY KEY (`brief_id`,`task_id`),
                             KEY `FK1bq5ndsx1xdquebcb8kw05ta0` (`task_id`,`cycle_id`),
                             CONSTRAINT `FK1bq5ndsx1xdquebcb8kw05ta0` FOREIGN KEY (`task_id`, `cycle_id`) REFERENCES `tbl_task` (`cycle_id`, `task_id`)
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
                             `user_id` bigint(20) NOT NULL,
                             PRIMARY KEY (`class_id`),
                             KEY `FKkc6jdw531j8gdbgj1ks6x3hfg` (`user_id`),
                             CONSTRAINT `FKkc6jdw531j8gdbgj1ks6x3hfg` FOREIGN KEY (`user_id`) REFERENCES `tbl_user` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


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
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


-- eduverse.tbl_curriculum_week definition

CREATE TABLE `tbl_curriculum_week` (
                                       `week_no` int(11) NOT NULL,
                                       `character_nm` varchar(50) DEFAULT NULL,
                                       `content` varchar(500) DEFAULT NULL,
                                       `create_dt` datetime(6) NOT NULL,
                                       `cur_lev` int(11) NOT NULL,
                                       `subtitle` varchar(200) DEFAULT NULL,
                                       `title` varchar(200) NOT NULL,
                                       `CUR_ID` bigint(20) NOT NULL,
                                       PRIMARY KEY (`CUR_ID`,`week_no`),
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
                                `feedback_id` varchar(255) NOT NULL,
                                `task_id` bigint(20) NOT NULL,
                                `create_dt` datetime(6) NOT NULL,
                                `update_dt` datetime(6) NOT NULL,
                                `character_img` varchar(500) NOT NULL,
                                `character_path` varchar(1000) NOT NULL,
                                `feedback_content` varchar(1000) NOT NULL,
                                `feedback_type` enum('FAILURE_LOGICAL','FAILURE_RUNTIME','SUCCESS') NOT NULL,
                                `sub_title` varchar(255) NOT NULL,
                                `title` varchar(255) NOT NULL,
                                `cycle_id` bigint(20) DEFAULT NULL,
                                PRIMARY KEY (`feedback_id`,`task_id`),
                                KEY `FKpt05l5il1u4av9igns9b9ldxn` (`task_id`,`cycle_id`),
                                CONSTRAINT `FKpt05l5il1u4av9igns9b9ldxn` FOREIGN KEY (`task_id`, `cycle_id`) REFERENCES `tbl_task` (`cycle_id`, `task_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


-- eduverse.tbl_hint definition

CREATE TABLE `tbl_hint` (
                            `hint_id` bigint(20) NOT NULL,
                            `task_id` bigint(20) NOT NULL,
                            `create_dt` datetime(6) NOT NULL,
                            `update_dt` datetime(6) NOT NULL,
                            `content` varchar(500) NOT NULL,
                            `title` varchar(255) NOT NULL,
                            `video_url` varchar(255) DEFAULT NULL,
                            `cycle_id` bigint(20) DEFAULT NULL,
                            PRIMARY KEY (`hint_id`,`task_id`),
                            KEY `FKf4xas788wee4aowv7b9b4t709` (`task_id`,`cycle_id`),
                            CONSTRAINT `FKf4xas788wee4aowv7b9b4t709` FOREIGN KEY (`task_id`, `cycle_id`) REFERENCES `tbl_task` (`cycle_id`, `task_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


-- eduverse.tbl_lecture definition

CREATE TABLE `tbl_lecture` (
                               `lecture_id` bigint(20) NOT NULL,
                               `task_id` bigint(20) NOT NULL,
                               `create_dt` datetime(6) NOT NULL,
                               `update_dt` datetime(6) NOT NULL,
                               `character_img` varchar(500) NOT NULL,
                               `character_path` varchar(500) NOT NULL,
                               `key_takeaway` varchar(500) NOT NULL,
                               `sandbox_code` varchar(500) NOT NULL,
                               `title` varchar(100) NOT NULL,
                               `cycle_id` bigint(20) DEFAULT NULL,
                               PRIMARY KEY (`lecture_id`,`task_id`),
                               KEY `FK7ssyekqssejbjwd6wie6tmf9x` (`task_id`,`cycle_id`),
                               CONSTRAINT `FK7ssyekqssejbjwd6wie6tmf9x` FOREIGN KEY (`task_id`, `cycle_id`) REFERENCES `tbl_task` (`cycle_id`, `task_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


-- eduverse.tbl_mentor_dialog definition

CREATE TABLE `tbl_mentor_dialog` (
                                     `cycle_id` bigint(20) NOT NULL,
                                     `dialog_id` bigint(20) NOT NULL,
                                     `task_id` bigint(20) NOT NULL,
                                     `create_dt` datetime(6) NOT NULL,
                                     `update_dt` datetime(6) NOT NULL,
                                     `content` varchar(1000) NOT NULL,
                                     `dialog_sort` int(11) NOT NULL,
                                     `dialog_type` int(11) NOT NULL,
                                     `mentor_type` int(11) NOT NULL,
                                     PRIMARY KEY (`cycle_id`,`dialog_id`,`task_id`),
                                     KEY `FKfxh7640wehtna467wiwgmdamd` (`task_id`,`cycle_id`),
                                     CONSTRAINT `FKfxh7640wehtna467wiwgmdamd` FOREIGN KEY (`task_id`, `cycle_id`) REFERENCES `tbl_task` (`cycle_id`, `task_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


-- eduverse.tbl_notification definition

CREATE TABLE `tbl_notification` (
                                    `notification_id` bigint(20) NOT NULL AUTO_INCREMENT,
                                    `create_dt` datetime(6) DEFAULT NULL,
                                    `payload_content` varchar(500) DEFAULT NULL,
                                    `read_dt` datetime(6) DEFAULT NULL,
                                    `type` int(11) NOT NULL,
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
                                 `expected_output` varchar(500) NOT NULL,
                                 `input_text` varchar(500) NOT NULL,
                                 `start_code` mediumtext NOT NULL,
                                 `test_code` mediumtext NOT NULL,
                                 `weight` int(11) NOT NULL,
                                 PRIMARY KEY (`cycle_id`,`task_id`,`test_case_id`),
                                 KEY `FK63x9mc43qxodig59n028dep8n` (`task_id`,`cycle_id`),
                                 CONSTRAINT `FK63x9mc43qxodig59n028dep8n` FOREIGN KEY (`task_id`, `cycle_id`) REFERENCES `tbl_task` (`cycle_id`, `task_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


-- eduverse.tbl_weekly_session definition

CREATE TABLE `tbl_weekly_session` (
                                      `weekly_session_id` bigint(20) NOT NULL,
                                      `create_dt` datetime(6) NOT NULL,
                                      `update_dt` datetime(6) NOT NULL,
                                      `auto_closed` bit(1) NOT NULL,
                                      `start_dt` datetime(6) DEFAULT NULL,
                                      `status` enum('COMPLETED','IN_PROGRESS','PENDING') NOT NULL,
                                      `week_no` int(11) NOT NULL,
                                      `INVITE_ID` bigint(20) NOT NULL,
                                      PRIMARY KEY (`INVITE_ID`,`weekly_session_id`),
                                      CONSTRAINT `FK6xefgfvkw9i0grwhilsohl666` FOREIGN KEY (`INVITE_ID`) REFERENCES `tbl_class_invite` (`invite_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


-- eduverse.tbl_work_log definition

CREATE TABLE `tbl_work_log` (
                                `user_id` bigint(20) NOT NULL,
                                `work_log_id` bigint(20) NOT NULL,
                                `completed_task` varchar(255) DEFAULT NULL,
                                `content` varchar(500) NOT NULL,
                                `difficulties` varchar(500) DEFAULT NULL,
                                `improvements` varchar(500) DEFAULT NULL,
                                `self_evaluation` int(11) NOT NULL,
                                `submit_dt` datetime(6) DEFAULT NULL,
                                `update_dt` datetime(6) DEFAULT NULL,
                                `week_number` int(11) NOT NULL,
                                `log_date` date NOT NULL,
                                `weekly_session_id` bigint(20) NOT NULL,
                                `create_dt` datetime(6) NOT NULL,
                                `achievements` text DEFAULT NULL,
                                `difficulty_level` int(11) DEFAULT NULL,
                                `notes` text DEFAULT NULL,
                                `work_hours` decimal(5,2) DEFAULT NULL,
                                `invite_id` bigint(20) DEFAULT NULL,
                                PRIMARY KEY (`user_id`,`work_log_id`),
                                KEY `FKck8531rkiycie72yas9piab91` (`weekly_session_id`,`invite_id`),
                                CONSTRAINT `FKck8531rkiycie72yas9piab91` FOREIGN KEY (`weekly_session_id`, `invite_id`) REFERENCES `tbl_weekly_session` (`INVITE_ID`, `weekly_session_id`),
                                CONSTRAINT `FKjbwfe2p39125xnmfpgu9bn7cl` FOREIGN KEY (`user_id`) REFERENCES `tbl_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


-- eduverse.tbl_activity_monitor definition

CREATE TABLE `tbl_activity_monitor` (
                                        `invite_id` bigint(20) NOT NULL,
                                        `monitor_id` bigint(20) NOT NULL,
                                        `weekly_session_id` bigint(20) NOT NULL,
                                        `create_dt` datetime(6) NOT NULL,
                                        `update_dt` datetime(6) NOT NULL,
                                        `last_action` enum('ACTIVE','COMPLETED','HELP_NEEDED','IDLE') NOT NULL,
                                        `last_seen_dt` datetime(6) NOT NULL,
                                        PRIMARY KEY (`invite_id`,`monitor_id`,`weekly_session_id`),
                                        KEY `FKh7okn69i7l3v8aga33b7e4qew` (`weekly_session_id`,`invite_id`),
                                        CONSTRAINT `FKh7okn69i7l3v8aga33b7e4qew` FOREIGN KEY (`weekly_session_id`, `invite_id`) REFERENCES `tbl_weekly_session` (`INVITE_ID`, `weekly_session_id`)
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
                                    `detail_json` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL CHECK (json_valid(`detail_json`)),
                                    `is_first_eval` bit(1) NOT NULL,
                                    `result` bit(1) DEFAULT NULL,
                                    `score` decimal(5,2) DEFAULT NULL,
                                    `submit_at` datetime(6) NOT NULL,
                                    `submit_num` int(11) DEFAULT NULL,
                                    `submit_yn` bit(1) NOT NULL,
                                    `invite_id` bigint(20) DEFAULT NULL,
                                    PRIMARY KEY (`class_id`,`cycle_id`,`submit_id`,`task_id`,`weekly_session_id`),
                                    KEY `FKevn3u3s9vxf9y7wkminsdlumr` (`task_id`,`cycle_id`),
                                    KEY `FKarxydk4qqqtjis72073lw4835` (`weekly_session_id`,`invite_id`),
                                    CONSTRAINT `FKarxydk4qqqtjis72073lw4835` FOREIGN KEY (`weekly_session_id`, `invite_id`) REFERENCES `tbl_weekly_session` (`INVITE_ID`, `weekly_session_id`),
                                    CONSTRAINT `FKevn3u3s9vxf9y7wkminsdlumr` FOREIGN KEY (`task_id`, `cycle_id`) REFERENCES `tbl_task` (`cycle_id`, `task_id`),
                                    CONSTRAINT `FKiarydmdd2v4i1if3vkxlx1fjh` FOREIGN KEY (`class_id`) REFERENCES `tbl_class` (`class_id`)
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
                                     `lang` enum('CLANG','CSHARP','HTML','JAVA','JAVASCRIPT','PYTHON') NOT NULL,
                                     `save_at` datetime(6) NOT NULL,
                                     `invite_id` bigint(20) DEFAULT NULL,
                                     PRIMARY KEY (`code_id`,`cycle_id`,`task_id`,`user_id`,`weekly_session_id`),
                                     KEY `FKgkrvkfkl1pw3lg781jq7ki5ga` (`task_id`,`cycle_id`),
                                     KEY `FKmss7lntb7oyj5yt33birgwc53` (`user_id`),
                                     KEY `FK1btj5rv98htdyewwwgt3b1u8j` (`weekly_session_id`,`invite_id`),
                                     CONSTRAINT `FK1btj5rv98htdyewwwgt3b1u8j` FOREIGN KEY (`weekly_session_id`, `invite_id`) REFERENCES `tbl_weekly_session` (`INVITE_ID`, `weekly_session_id`),
                                     CONSTRAINT `FKgkrvkfkl1pw3lg781jq7ki5ga` FOREIGN KEY (`task_id`, `cycle_id`) REFERENCES `tbl_task` (`cycle_id`, `task_id`),
                                     CONSTRAINT `FKmss7lntb7oyj5yt33birgwc53` FOREIGN KEY (`user_id`) REFERENCES `tbl_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


-- eduverse.tbl_progress definition

CREATE TABLE `tbl_progress` (
                                `class_id` bigint(20) NOT NULL,
                                `progress_id` bigint(20) NOT NULL,
                                `weekly_session_id` bigint(20) NOT NULL,
                                `create_dt` datetime(6) NOT NULL,
                                `update_dt` datetime(6) NOT NULL,
                                `completed_dt` datetime(6) DEFAULT NULL,
                                `progress_pct` decimal(5,2) NOT NULL,
                                `user_id` bigint(20) NOT NULL,
                                `invite_id` bigint(20) DEFAULT NULL,
                                `activity_status` enum('ACTIVE','COMPLETED','IDLE','NEED_HELP') DEFAULT NULL,
                                `last_activity_time` datetime(6) DEFAULT NULL,
                                `test_fail_count` int(11) DEFAULT NULL,
                                PRIMARY KEY (`class_id`,`progress_id`,`weekly_session_id`),
                                KEY `FK8hk6lhruei5nc1myolu20flh3` (`user_id`),
                                KEY `FK2deh35bt6vd96qpoxe4ooqeim` (`weekly_session_id`,`invite_id`),
                                CONSTRAINT `FK2deh35bt6vd96qpoxe4ooqeim` FOREIGN KEY (`weekly_session_id`, `invite_id`) REFERENCES `tbl_weekly_session` (`INVITE_ID`, `weekly_session_id`),
                                CONSTRAINT `FK8hk6lhruei5nc1myolu20flh3` FOREIGN KEY (`user_id`) REFERENCES `tbl_user` (`user_id`),
                                CONSTRAINT `FKeqif638ig9lqor4a2ewlfpoy0` FOREIGN KEY (`class_id`) REFERENCES `tbl_class` (`class_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


-- eduverse.tbl_question definition

CREATE TABLE `tbl_question` (
                                `class_id` bigint(20) NOT NULL,
                                `invite_id` bigint(20) NOT NULL,
                                `question_id` bigint(20) NOT NULL,
                                `weekly_session_id` bigint(20) NOT NULL,
                                `create_dt` datetime(6) NOT NULL,
                                `update_dt` datetime(6) NOT NULL,
                                `content` varchar(500) NOT NULL,
                                `status` enum('ANSWERED','CLOSED','OPEN') NOT NULL,
                                `title` varchar(200) NOT NULL,
                                `urgency` enum('HIGH','LOW','MEDIUM') NOT NULL,
                                `user_id` bigint(20) NOT NULL,
                                PRIMARY KEY (`class_id`,`invite_id`,`question_id`,`weekly_session_id`),
                                KEY `FKnfhxi16d0dbr0fovemahofw89` (`user_id`),
                                KEY `FK8eopp7d88bmbb2edby5mmih46` (`weekly_session_id`,`invite_id`),
                                CONSTRAINT `FK5xpvvxv4xomjicwx02cq82d5a` FOREIGN KEY (`class_id`) REFERENCES `tbl_class` (`class_id`),
                                CONSTRAINT `FK8eopp7d88bmbb2edby5mmih46` FOREIGN KEY (`weekly_session_id`, `invite_id`) REFERENCES `tbl_weekly_session` (`INVITE_ID`, `weekly_session_id`),
                                CONSTRAINT `FKnfhxi16d0dbr0fovemahofw89` FOREIGN KEY (`user_id`) REFERENCES `tbl_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


-- eduverse.tbl_answer definition

CREATE TABLE `tbl_answer` (
                              `answer_id` bigint(20) NOT NULL,
                              `question_id` bigint(20) NOT NULL,
                              `create_dt` datetime(6) NOT NULL,
                              `update_dt` datetime(6) NOT NULL,
                              `content` varchar(500) NOT NULL,
                              `class_id` bigint(20) DEFAULT NULL,
                              `weekly_session_id` bigint(20) DEFAULT NULL,
                              `invite_id` bigint(20) DEFAULT NULL,
                              PRIMARY KEY (`answer_id`,`question_id`),
                              KEY `FKdkijd523elglmnbl7r6by2wpk` (`question_id`,`class_id`,`weekly_session_id`,`invite_id`),
                              CONSTRAINT `FKdkijd523elglmnbl7r6by2wpk` FOREIGN KEY (`question_id`, `class_id`, `weekly_session_id`, `invite_id`) REFERENCES `tbl_question` (`class_id`, `invite_id`, `question_id`, `weekly_session_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;