package com.cinemax.domain.classes.service;

import com.cinemax.domain.classes.dto.ClassEnrollResponse;
import com.cinemax.domain.classes.dto.ClassInviteResponse;
import com.cinemax.domain.classes.dto.ClassRequest;
import com.cinemax.domain.classes.dto.ClassResponse;
import com.cinemax.domain.classes.dto.ClassScheduleResponse;
import com.cinemax.domain.classes.dto.ClassWithStudentsResponse;
import com.cinemax.domain.user.entity.User;

import java.util.List;

/**
 * 수업 서비스 인터페이스
 */
public interface ClassService {

    // 수업 생성
    ClassResponse createClass(ClassRequest request, String professorEmail);

    // 수업 조회
    ClassResponse getClass(Long classId);

    // 교수자 ID로 수업 목록 조회
    List<ClassResponse> getProfessorClasses(Long professorId);

    // 교수자 ID로 수업 목록 + 학생 목록 조회
    List<ClassWithStudentsResponse> getProfessorClassesWithStudents(Long professorId);

    // 활성화된 수업 조회
    List<ClassResponse> getActiveClasses();

    // 수업 수정
    ClassResponse updateClass(Long classId, ClassRequest request);

    // 수업 비활성화
    void deactivateClass(Long classId);

    // 수업 활성화
    void activateClass(Long classId);

    // 수업 삭제
    void deleteClass(Long classId);

    // 수업 초대 코드 생성
    ClassInviteResponse createInviteCode(Long classId);

    // 활성화된 초대 코드 조회
    ClassInviteResponse getActiveInvite(Long classId);

    // 초대 코드로 수업 조회
    ClassResponse getClassByInviteCode(String inviteCode);

    // 초대 ID로 수업 조회
    ClassResponse getClassByInviteId(Long inviteId);

    // 수강 신청 (컨트롤러용)
    ClassEnrollResponse enrollClass(Long classId, User student);

    // 수강 신청 취소 (컨트롤러용)
    void cancelEnrollment(Long classId, Long studentId, String withdrawReason);

    // 수업의 수강생 목록 조회
    List<ClassEnrollResponse> getClassEnrollments(Long classId);

    // 학생 ID로 수강 수업 목록 조회
    List<ClassResponse> getStudentClasses(Long studentId);

    // 상태별 수강생 조회
    List<ClassEnrollResponse> getEnrollmentsByStatus(Long classId, com.cinemax.global.enums.EnrollStatus status);

    // 초대 코드로 참여(재참여 포함)
    ClassEnrollResponse joinByInviteCode(String inviteCode, String studentEmail);

    // 초대 코드 재발급(기존 무효화 후 신규 활성화)
    ClassInviteResponse rotateInviteCode(Long classId);

    // 활성 초대코드를 기반으로 QR PNG 바이트 생성
    byte[] getInviteQrImage(Long classId, String baseUrl, Integer size);

    // 교수자 ID로 수업 일정 조회 (주차별 세션 포함)
    List<ClassScheduleResponse> getClassSchedule(Long professorId);

    // 교수자의 당일 수업 목록 조회 (오늘 진행 중인 수업)
    List<ClassScheduleResponse> getTodayClasses(Long professorId);

    // 관리자용 - 전체 수업 목록 조회 (필터링 가능)
    List<ClassResponse> getAllClasses(Boolean isActive, Long professorId);

    // 학생이 과거에 수강했던 삭제된 수업 목록 조회 (읽기 전용)
    List<ClassResponse> getDeletedClasses(Long studentId);
}
