package com.cinemax.global.enums;

import com.cinemax.domain.user.entity.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@RequiredArgsConstructor
public enum NotificationType {

    // ========== P0 (최우선) ==========

    // 관리자 -> 교수
    ADMIN_TO_PROFESSOR_ACCOUNT_CREATED(
            "ADMIN", "PROFESSOR", "계정 생성 알림",
            "교수 계정이 발급되었습니다. 초기 비밀번호는 보안을 위해서 변경하시기 바랍니다.",
            "P0"
    ),
    ADMIN_TO_PROFESSOR_TEMP_PASSWORD(
            "ADMIN", "PROFESSOR", "상태 변경 알림",
            "귀하의 계정 상태가 {변경/정지} 상태로 변경되었습니다. 문의가 필요하시면 관리자에게 문의하세요.",
            "P0"
    ),

    // 관리자 -> 학생
    ADMIN_TO_STUDENT_TEMP_PASSWORD(
            "ADMIN", "STUDENT", "상태 변경 알림",
            "계정이 {변경/정지}되었습니다. 관리자에게 문의하세요.",
            "P0"
    ),

    // 교수 -> 학생
    PROFESSOR_TO_STUDENT_SUBMISSION_DEADLINE(
            "PROFESSOR", "STUDENT", "수업 시간 알림",
            "수업 시간이 다가왔습니다.",
            "P0"
    ),

    // 교수 -> 학생
    PROFESSOR_TO_STUDENT_SCORE_ENTRY(
            "PROFESSOR", "STUDENT", "수업 종료 알림",
            "수업이 {N}시간 후 수업이 종료됩니다.",
            "P0"
    ),

    // 교수 -> 학생
    PROFESSOR_TO_STUDENT_ASSIGNMENT_SUBMISSION(
            "PROFESSOR", "STUDENT", "답변 완료 알림",
            "질문에 대한 답변이 등록되었습니다.",
            "P0"
    ),

    // 학생 -> 교수
    STUDENT_TO_PROFESSOR_CURRICULUM_REGISTER(
            "STUDENT", "PROFESSOR", "질문 등록 알림",
            "{이름}님이 새로운 질문을 등록하였습니다.",
            "P0"
    ),

    // 학생 -> 교수
    STUDENT_TO_PROFESSOR_ASSIGNMENT_COMPLETION(
            "STUDENT", "PROFESSOR", "학습 완료 알림",
            "{이름}님이 수업의 질문을 완료하였습니다.",
            "P0"
    ),

    // ========== P1 (중요) ==========

    // 학생 -> 교수
    STUDENT_TO_PROFESSOR_QUESTION_ASKED(
            "STUDENT", "PROFESSOR", "공지사항",
            "{중요} 다음 주 수업 시간이 변경되었습니다.",
            "P1"
    ),

    // 학생 -> 교수
    STUDENT_TO_PROFESSOR_ATTENDANCE_CHECK(
            "STUDENT", "PROFESSOR", "학습 완료 알림",
            "{이름}님이 3차시 학습을 마쳤습니다.",
            "P1"
    ),

    // 학생 -> 교수
    STUDENT_TO_PROFESSOR_PROGRESS_INQUIRY(
            "STUDENT", "PROFESSOR", "수업 참여 알림",
            "{이름}님이 수업에 참여하였습니다. (25명)",
            "P1"
    ),

    // 학생 -> 교수
    STUDENT_TO_PROFESSOR_GRADE_INQUIRY(
            "STUDENT", "PROFESSOR", "도움 요청 알림",
            "{이름}님이 특시 도움을 요청했습니다.",
            "P1"
    ),

    // ========== P2 (일반) ==========

    // 관리자 -> 교수
    ADMIN_TO_PROFESSOR_SYSTEM_ANNOUNCEMENT(
            "ADMIN", "PROFESSOR", "시스템 공지",
            "시스템 점검이 예정되어 있습니다. 불편을 드려 죄송합니다. (2025-11-15 02:00~04:00)",
            "P2"
    ),

    // 관리자 -> 학생
    ADMIN_TO_STUDENT_SYSTEM_ANNOUNCEMENT(
            "ADMIN", "STUDENT", "시스템 공지",
            "시스템 점검이 예정되어 있습니다. 불편을 드려 죄송합니다. (2025-11-15 02:00~04:00)",
            "P2"
    ),

    // 학생 -> 관리자
    STUDENT_TO_ADMIN_ACCOUNT_REQUEST(
            "STUDENT", "ADMIN", "계정 신청 요청",
            "학생 {이름}의 계정 신청을 요청했습니다.",
            "P2"
    ),

    // 학생 -> 관리자
    STUDENT_TO_ADMIN_ERROR_REPORT(
            "STUDENT", "ADMIN", "문의/신고",
            "시스템 오류에 대한 문의가 접수되었습니다.",
            "P2"
    );

    private final String sender;           // 송신자 역할
    private final String receiver;         // 수신자 역할
    private final String purpose;          // 목적
    private final String messageTemplate;  // 메시지 템플릿
    private final String priority;         // 개발 우선순위

    /**
     * 메시지 템플릿에 변수를 치환하여 실제 메시지 생성
     */
    public String formatMessage(Object... params) {
        String message = this.messageTemplate;

        if (params != null && params.length > 0) {
            for (Object param : params) {
                // 첫 번째 중괄호 패턴을 찾아서 치환
                message = message.replaceFirst("\\{[^}]+\\}", String.valueOf(param));
            }
        }

        return message;
    }

    /**
     * 시스템 점검 시간 포함 메시지 생성
     */
    public String formatMessageWithDateTime(LocalDateTime startTime, LocalDateTime endTime) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String timeRange = String.format("%s~%s", startTime.format(formatter), endTime.format(DateTimeFormatter.ofPattern("HH:mm")));

        return messageTemplate.replaceFirst("\\([^)]+\\)", String.format("(%s)", timeRange));
    }

    /**
     * 사용자 이름 포함 메시지 생성
     */
    public String formatMessageWithUser(User user) {
        return messageTemplate.replace("{이름}", user.getName());
    }

    /**
     * 숫자 정보 포함 메시지 생성
     */
    public String formatMessageWithCount(int count, String unit) {
        return messageTemplate
            .replaceFirst("\\{N\\}", String.valueOf(count))
            .replaceFirst("\\(25명\\)", String.format("(%d%s)", count, unit));
    }
}
