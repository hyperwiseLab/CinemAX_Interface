package com.cinemax.domain.classes.service.impl;

import com.cinemax.core.exception.BusinessException;
import com.cinemax.core.exception.ResourceNotFoundException;
import com.cinemax.core.error.enums.ErrorCode;
import com.cinemax.domain.classes.dto.ClassEnrollResponse;
import com.cinemax.domain.classes.dto.ClassInviteResponse;
import com.cinemax.domain.classes.dto.ClassRequest;
import com.cinemax.domain.classes.dto.ClassResponse;
import com.cinemax.domain.classes.dto.ClassScheduleResponse;
import com.cinemax.domain.classes.dto.ClassWithStudentsResponse;
import com.cinemax.domain.classes.entity.ClassEntity;
import com.cinemax.domain.classes.entity.ClassEnroll;
import com.cinemax.domain.classes.entity.ClassInvite;
import com.cinemax.domain.classes.repository.ClassEntityRepository;
import com.cinemax.domain.classes.repository.ClassEnrollRepository;
import com.cinemax.domain.classes.repository.ClassInviteRepository;
import com.cinemax.domain.classes.service.ClassService;
import com.cinemax.domain.curriculum.entity.Curriculum;
import com.cinemax.domain.curriculum.repository.CurriculumRepository;
import com.cinemax.domain.user.entity.User;
import com.cinemax.domain.user.repository.UserRepository;
import com.cinemax.domain.weeklySession.entity.WeeklySession;
import com.cinemax.domain.weeklySession.repository.WeeklySessionRepository;
import com.cinemax.domain.weeklySession.service.WeeklySessionService;
import com.cinemax.global.enums.EnrollStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.io.ByteArrayOutputStream;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 수업 서비스 구현체
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClassServiceImpl implements ClassService {

    private static final String NOT_FOUND_CLASS = "수업을 찾을 수 없습니다. ID: ";
    private static final String NOT_FOUND_CURRICULUM = "커리큘럼을 찾을 수 없습니다. ID: ";

    private final ClassEntityRepository classEntityRepository;
    private final ClassInviteRepository classInviteRepository;
    private final ClassEnrollRepository classEnrollRepository;
    private final UserRepository userRepository;
    private final WeeklySessionRepository weeklySessionRepository;
    private final WeeklySessionService weeklySessionService;
    private final CurriculumRepository curriculumRepository;
    private final com.cinemax.domain.quiz.repository.QuizRepository quizRepository;
    private final com.cinemax.domain.quiz.repository.QuizSubmissionRepository quizSubmissionRepository;

    // 수업 생성
    @Override
    @Transactional
    public ClassResponse createClass(ClassRequest request, String professorEmail) {

        if (professorEmail == null || professorEmail.isBlank()) {
            throw new IllegalArgumentException("교수자 이메일이 필요합니다.");
        }

        User professor = userRepository.findByEmail(professorEmail)
                .orElseThrow(() -> new ResourceNotFoundException("교수자 정보를 찾을 수 없습니다: " + professorEmail));

        Curriculum curriculum = curriculumRepository.findById(request.getCurId())
                .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_CURRICULUM + request.getCurId()));

        // 수업 생성
        ClassEntity classEntity = ClassEntity.builder()
                .classNm(request.getClassNm())
                .description(request.getDescription())
                .year(request.getYear())
                .term(request.getTerm())
                .useYn(request.getUseYn())
                .user(professor)
                .curriculum(curriculum)
                .build();

        ClassEntity savedClass = classEntityRepository.save(classEntity);

        return ClassResponse.from(savedClass);
    }

    // 수업 조회
    @Override
    public ClassResponse getClass(Long classId) {

        ClassEntity classEntity = classEntityRepository.findById(classId)
                .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_CLASS + classId));

        // 현재 활성화된 초대 코드의 inviteId 조회
        Long inviteId = classInviteRepository.findCurrentActiveByClassId(classId)
                .map(ClassInvite::getInviteId)
                .orElse(null);

        return ClassResponse.from(classEntity, inviteId);
    }

    // 교수자 수업 목록 조회
    @Override
    public List<ClassResponse> getProfessorClasses(Long professorId) {

        List<ClassEntity> classes = classEntityRepository.findByUserId(professorId);

        // N+1 문제 방지를 위해 일괄 조회
        List<Long> classIds = classes.stream().map(ClassEntity::getClassId).toList();
        Map<Long, Long> classIdToInviteIdMap = classInviteRepository.findActiveByClassIds(classIds).stream()
                .collect(java.util.stream.Collectors.toMap(
                        invite -> invite.getClassEntity().getClassId(),
                        ClassInvite::getInviteId,
                        (existing, replacement) -> existing
                ));

        return classes.stream()
                .map(classEntity -> ClassResponse.from(classEntity, classIdToInviteIdMap.get(classEntity.getClassId())))
                .toList();
    }

    // 교수자 수업 목록 + 학생 목록 조회
    @Override
    public List<ClassWithStudentsResponse> getProfessorClassesWithStudents(Long professorId) {

        List<ClassEntity> classes = classEntityRepository.findByUserId(professorId);

        // N+1 문제 방지를 위해 일괄 조회
        List<Long> classIds = classes.stream().map(ClassEntity::getClassId).toList();

        // 초대 코드 일괄 조회
        Map<Long, Long> classIdToInviteIdMap = classInviteRepository.findActiveByClassIds(classIds).stream()
                .collect(java.util.stream.Collectors.toMap(
                        invite -> invite.getClassEntity().getClassId(),
                        ClassInvite::getInviteId,
                        (existing, replacement) -> existing
                ));

        // 각 수업의 학생 목록 일괄 조회
        Map<Long, List<ClassEnrollResponse>> classIdToStudentsMap = new java.util.HashMap<>();
        for (Long classId : classIds) {
            List<ClassEnroll> enrollments = classEnrollRepository.findByClassEntityClassId(classId);
            List<ClassEnrollResponse> students = enrollments.stream()
                    .map(ClassEnrollResponse::from)
                    .toList();
            classIdToStudentsMap.put(classId, students);
        }

        return classes.stream()
                .map(classEntity -> {
                    ClassResponse classResponse = ClassResponse.from(
                            classEntity,
                            classIdToInviteIdMap.get(classEntity.getClassId())
                    );
                    List<ClassEnrollResponse> students = classIdToStudentsMap.getOrDefault(
                            classEntity.getClassId(),
                            new java.util.ArrayList<>()
                    );
                    return ClassWithStudentsResponse.of(classResponse, students);
                })
                .toList();
    }

    // 활성화된 수업 목록 조회
    @Override
    public List<ClassResponse> getActiveClasses() {

        List<ClassEntity> classes = classEntityRepository.findByUseYn(true);

        // N+1 문제 방지를 위해 일괄 조회
        List<Long> classIds = classes.stream().map(ClassEntity::getClassId).toList();
        Map<Long, Long> classIdToInviteIdMap = classInviteRepository.findActiveByClassIds(classIds).stream()
                .collect(Collectors.toMap(invite -> invite.getClassEntity().getClassId(), ClassInvite::getInviteId,
                        (existing, replacement) -> existing
                ));

        return classes.stream()
                .map(classEntity -> ClassResponse.from(classEntity, classIdToInviteIdMap.get(classEntity.getClassId())))
                .toList();
    }

    // 학생 수강 수업 목록 조회
    @Override
    public List<ClassResponse> getStudentClasses(Long studentId) {

        List<ClassEnroll> enrollments = classEnrollRepository.findActiveEnrollmentsByUserId(studentId);
        List<ClassEntity> classes = enrollments.stream()
                .map(ClassEnroll::getClassEntity)
                .toList();

        // N+1 문제 방지를 위해 일괄 조회
        List<Long> classIds = classes.stream().map(ClassEntity::getClassId).toList();
        Map<Long, Long> classIdToInviteIdMap = classInviteRepository.findActiveByClassIds(classIds).stream()
                .collect(Collectors.toMap(invite -> invite.getClassEntity().getClassId(), ClassInvite::getInviteId,
                        (existing, replacement) -> existing
                ));

        return classes.stream()
                .map(classEntity -> ClassResponse.from(classEntity, classIdToInviteIdMap.get(classEntity.getClassId())))
                .toList();
    }

    // 수업 수정
    @Override
    @Transactional
    public ClassResponse updateClass(Long classId, ClassRequest request) {

        ClassEntity classEntity = classEntityRepository.findById(classId)
                .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_CLASS + classId));

        Curriculum curriculum = curriculumRepository.findById(request.getCurId())
                .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_CURRICULUM + request.getCurId()));

        // 수업 정보 수정
        classEntity.updateInfo(request.getClassNm(), request.getDescription(),
                             request.getYear(), request.getTerm(), request.getUseYn(), curriculum);

        // currentInviteId가 요청에 포함된 경우 업데이트
        if (request.getCurrentInviteId() != null && !request.getCurrentInviteId().isBlank()) {
            // 요청된 초대 코드가 존재하는지 확인
            ClassInvite invite = classInviteRepository.findByInviteCd(request.getCurrentInviteId())
                    .orElseThrow(() -> new ResourceNotFoundException("초대 코드를 찾을 수 없습니다: " + request.getCurrentInviteId()));
            
            // 해당 초대 코드가 이 수업의 것인지 확인
            if (!invite.getClassEntity().getClassId().equals(classId)) {
                throw new BusinessException(ErrorCode.DATA_INTEGRITY_VIOLATION, "해당 초대 코드는 이 수업에 속하지 않습니다.");
            }
            
            classEntity.updateCurrentInviteId(request.getCurrentInviteId());
        } else if (classEntity.getCurrentInviteId() == null || classEntity.getCurrentInviteId().isBlank()) {
            // currentInviteId가 없으면 초대 코드 자동 생성
            // 기존 활성화된 초대 코드 비활성화
            classInviteRepository.deactivateAllByClassId(classId);

            String inviteCode = generateInviteCode();
            String qrCode = generateQrCode(inviteCode);

            ClassInvite classInvite = ClassInvite.builder()
                    .inviteCd(inviteCode)
                    .qrCd(qrCode)
                    .activeYn(true)
                    .classEntity(classEntity)
                    .build();

            ClassInvite savedInvite = classInviteRepository.save(classInvite);

            classEntity.updateCurrentInviteId(inviteCode);

            // 커리큘럼의 CurriculumWeek 정보를 기반으로 WeeklySession 자동 생성
            weeklySessionService.createWeeklySessionsForInvite(savedInvite.getInviteId());
        }

        return ClassResponse.from(classEntity);
    }

    // 수업 비활성화
    @Override
    @Transactional
    public void deactivateClass(Long classId) {

        ClassEntity classEntity = classEntityRepository.findById(classId)
                .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_CLASS + classId));

        classEntity.deactivate();
    }

    // 수업 활성화
    @Override
    @Transactional
    public void activateClass(Long classId) {

        ClassEntity classEntity = classEntityRepository.findById(classId)
                .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_CLASS + classId));

        classEntity.activate();
    }

    // 수업 삭제
    @Override
    @Transactional
    public void deleteClass(Long classId) {

        ClassEntity classEntity = classEntityRepository.findById(classId)
                .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_CLASS + classId));

        // 반의 주차별 퀴즈 및 제출 기록 삭제 (WeeklySession 삭제 전에 정리)
        quizSubmissionRepository.deleteByClassId(classId);
        quizRepository.deleteAll(quizRepository.findByClassId(classId));

        // 연관된 초대 코드 및 주차별 세션 삭제
        List<ClassInvite> invites = classInviteRepository.findByClassEntityClassId(classId);
        for (ClassInvite invite : invites) {
            // WeeklySession은 ClassInvite의 cascade로 자동 삭제됨
            classInviteRepository.delete(invite);
        }

        // ClassEnroll과 ClassSubmit은 ClassEntity의 cascade로 자동 삭제됨
        classEntityRepository.delete(classEntity);
    }

    // 수업 초대 코드 생성
    @Override
    @Transactional
    public ClassInviteResponse createInviteCode(Long classId) {

        ClassEntity classEntity = classEntityRepository.findById(classId)
                .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_CLASS + classId));

        // 기존 활성화된 초대 코드 비활성화
        classInviteRepository.deactivateAllByClassId(classId);

        // 새로운 초대 코드 생성 (영대문자+숫자 6자리)
        String inviteCode = generateInviteCode();
        // 초대 코드 기반 QR 콘텐츠(조인 URI)
        String qrCode = generateQrCode(inviteCode);

        ClassInvite classInvite = ClassInvite.builder()
                .inviteCd(inviteCode)
                .qrCd(qrCode)
                .activeYn(true)
                .classEntity(classEntity)
                .build();

        ClassInvite savedInvite = classInviteRepository.save(classInvite);

        // 수업의 현재 초대 코드 업데이트
        classEntity.updateCurrentInviteId(inviteCode);

        // 커리큘럼의 CurriculumWeek 정보를 기반으로 WeeklySession 자동 생성
        weeklySessionService.createWeeklySessionsForInvite(savedInvite.getInviteId());

        return ClassInviteResponse.from(savedInvite);
    }

    // 활성화된 초대 코드 조회
    @Override
    public ClassInviteResponse getActiveInvite(Long classId) {

        ClassInvite classInvite = classInviteRepository.findCurrentActiveByClassId(classId)
                .orElseThrow(() -> new ResourceNotFoundException("활성화된 초대 코드를 찾을 수 없습니다. classId: " + classId));

        return ClassInviteResponse.from(classInvite);
    }

    // 초대 코드 재발급(기존 비활성화 후 신규 발급)
    @Override
    @Transactional
    public ClassInviteResponse rotateInviteCode(Long classId) {
        return createInviteCode(classId);
    }

    // 초대 코드로 수업 조회
    @Override
    public ClassResponse getClassByInviteCode(String inviteCode) {

        ClassInvite classInvite = classInviteRepository.findActiveByInviteCd(inviteCode)
                .orElseThrow(() -> new ResourceNotFoundException("유효하지 않은 초대 코드입니다: " + inviteCode));

        return ClassResponse.from(classInvite.getClassEntity(), classInvite.getInviteId());
    }

    // 초대 ID로 수업 조회
    @Override
    public ClassResponse getClassByInviteId(Long inviteId) {

        ClassInvite classInvite = classInviteRepository.findById(inviteId)
                .orElseThrow(() -> new ResourceNotFoundException("유효하지 않은 초대 ID입니다: " + inviteId));

        return ClassResponse.from(classInvite.getClassEntity(), classInvite.getInviteId());
    }

    // 초대 코드로 참여(재참여 포함)
    @Override
    @Transactional
    public ClassEnrollResponse joinByInviteCode(String inviteCode, String studentEmail) {
        if (studentEmail == null || studentEmail.isBlank()) {
            throw new IllegalArgumentException("학생 이메일이 필요합니다.");
        }
        User student = userRepository.findByEmail(studentEmail)
                .orElseThrow(() -> new ResourceNotFoundException("학생 정보를 찾을 수 없습니다: " + studentEmail));

        // 활성 초대 코드 확인
        ClassInvite classInvite = classInviteRepository.findActiveByInviteCd(inviteCode)
                .orElseThrow(() -> new ResourceNotFoundException("유효하지 않은 초대 코드입니다: " + inviteCode));

        Long classId = classInvite.getClassEntity().getClassId();

        // 삭제된 수업인지 확인
        validateClassNotDeleted(classId);

        // 기존 수강 기록 조회(상태 무관)
        var existingOpt = classEnrollRepository.findByClassEntityClassIdAndUserUserId(classId, student.getUserId());

        if (existingOpt.isPresent()) {
            ClassEnroll existing = existingOpt.get();
            if (existing.getStatus() == EnrollStatus.ACTIVE) {
                throw new BusinessException(ErrorCode.DATA_ALREADY_EXISTS, "이미 수강 중인 수업입니다.");
            }
            // 재참여: WITHDRAWN -> ACTIVE 복원
            existing.reactivate();
            ClassEnroll saved = classEnrollRepository.save(existing);
            return ClassEnrollResponse.from(saved);
        }

        // 신규 참여
        ClassEnroll enrollment = ClassEnroll.builder()
                .classEntity(classInvite.getClassEntity())
                .user(student)
                .status(EnrollStatus.ACTIVE)
                .build();

        ClassEnroll savedEnrollment = classEnrollRepository.save(enrollment);
        return ClassEnrollResponse.from(savedEnrollment);
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] getInviteQrImage(Long classId, String baseUrl, Integer size) {
        ClassInvite classInvite = classInviteRepository.findCurrentActiveByClassId(classId)
                .orElseThrow(() -> new ResourceNotFoundException("활성화된 초대 코드를 찾을 수 없습니다. classId: " + classId));

        String inviteCode = classInvite.getInviteCd();
        String link = (baseUrl != null && !baseUrl.isBlank() ? baseUrl.replaceAll("/$", "") : "") + "/join/" + inviteCode;

        int qrSize = (size != null && size >= 128 && size <= 1024) ? size : 300;

        try {
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
            hints.put(EncodeHintType.MARGIN, 1);

            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(link, BarcodeFormat.QR_CODE, qrSize, qrSize, hints);
            BufferedImage image = MatrixToImageWriter.toBufferedImage(bitMatrix);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            return baos.toByteArray();
        } catch (WriterException | IOException e) {
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "QR 코드 생성 중 오류가 발생했습니다.");
        }
    }

    // 수강 신청
    @Override
    @Transactional
    public ClassEnrollResponse enrollClass(Long classId, User student) {

        // 삭제된 수업인지 확인
        validateClassNotDeleted(classId);

        // 수업 조회
        ClassEntity classEntity = classEntityRepository.findById(classId)
                .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_CLASS + classId));

        // 이미 수강 신청했는지 확인
        if (classEnrollRepository.existsActiveByClassIdAndUserId(classId, student.getUserId())) {
            throw new BusinessException(ErrorCode.DATA_ALREADY_EXISTS, "이미 수강 신청한 수업입니다.");
        }

        // 수강 신청 생성
        ClassEnroll enrollment = ClassEnroll.builder()
                .classEntity(classEntity)
                .user(student)
                .status(EnrollStatus.ACTIVE)
                .build();

        ClassEnroll savedEnrollment = classEnrollRepository.save(enrollment);
        return ClassEnrollResponse.from(savedEnrollment);
    }

    // 수강 신청 취소
    @Override
    @Transactional
    public void cancelEnrollment(Long classId, Long studentId, String withdrawReason) {

        // 삭제된 수업인지 확인
        validateClassNotDeleted(classId);

        ClassEnroll enrollment = classEnrollRepository.findByClassEntityClassIdAndUserUserId(classId, studentId)
                .orElseThrow(() -> new ResourceNotFoundException("수강 신청을 찾을 수 없습니다."));

        if (withdrawReason != null && !withdrawReason.isBlank()) {
            enrollment.withdraw(withdrawReason);
        } else {
            enrollment.withdraw();
        }
        classEnrollRepository.save(enrollment);
    }

    // 수강생 목록 조회
    @Override
    public List<ClassEnrollResponse> getClassEnrollments(Long classId) {

        List<ClassEnroll> enrollments = classEnrollRepository.findActiveEnrollmentsByClassId(classId);
        return enrollments.stream()
                .map(ClassEnrollResponse::from)
                .toList();
    }

    // 상태별 수강생 조회
    @Override
    public List<ClassEnrollResponse> getEnrollmentsByStatus(Long classId, EnrollStatus status) {

        List<ClassEnroll> enrollments = classEnrollRepository.findByClassEntityClassIdAndStatus(classId, status);
        return enrollments.stream()
                .map(ClassEnrollResponse::from)
                .toList();
    }

    // 초대 코드 생성
    private String generateInviteCode() {
        final String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder(6);

        for (int i = 0; i < 6; i++) {
            int idx = (int) (Math.random() * alphabet.length());
            sb.append(alphabet.charAt(idx));
        }

        return sb.toString();
    }

    // QR 코드 생성
    private String generateQrCode(String inviteCode) {
        return "/join/" + inviteCode;
    }

    // 교수자 ID로 수업 일정 조회 (주차별 세션 포함)
    @Override
    public List<ClassScheduleResponse> getClassSchedule(Long professorId) {

        // 교수자 존재 여부 확인
        if (!userRepository.existsById(professorId)) {
            throw new ResourceNotFoundException("User", "professorId", professorId);
        }

        // 교수자의 모든 수업 조회
        List<ClassEntity> classes = classEntityRepository.findByUserId(professorId);

        // 각 수업의 활성 초대 코드를 통해 주차별 세션 조회
        List<ClassScheduleResponse> schedules = new java.util.ArrayList<>();
        for (ClassEntity classEntity : classes) {
            List<ClassInvite> activeInvites = classInviteRepository.findActiveByClassId(classEntity.getClassId());

            for (ClassInvite invite : activeInvites) {
                List<WeeklySession> weeklySessions = weeklySessionRepository.findByInviteIdOrderByWeekNo(invite.getInviteId());

                for (WeeklySession session : weeklySessions) {
                    schedules.add(ClassScheduleResponse.from(classEntity, session));
                }
            }
        }

        return schedules;
    }

    // 교수자의 당일 수업 목록 조회 (오늘 진행 중인 수업)
    @Override
    public List<ClassScheduleResponse> getTodayClasses(Long professorId) {

        // 교수자 존재 여부 확인
        if (!userRepository.existsById(professorId)) {
            throw new ResourceNotFoundException("User", "professorId", professorId);
        }

        // 오늘 진행 중인 세션 조회
        LocalDateTime today = LocalDateTime.now();
        List<WeeklySession> todaySessions = weeklySessionRepository.findTodaySessionsByProfessorId(professorId, today);

        List<ClassScheduleResponse> todaySchedules = new java.util.ArrayList<>();
        for (WeeklySession session : todaySessions) {
            ClassInvite invite = session.getClassInvite();
            if (invite != null) {
                ClassEntity classEntity = invite.getClassEntity();
                if (classEntity != null) {
                    todaySchedules.add(ClassScheduleResponse.from(classEntity, session));
                }
            }
        }

        return todaySchedules;
    }

    // 관리자용 - 전체 수업 목록 조회 (필터링 가능)
    @Override
    @Transactional(readOnly = true)
    public List<ClassResponse> getAllClasses(Boolean isActive, Long professorId) {
        List<ClassEntity> classes;

        if (isActive != null && professorId != null) {
            // 활성화 상태와 교수자 ID 모두 필터링
            classes = classEntityRepository.findByProfessorUserIdAndIsActive(professorId, isActive);
        } else if (isActive != null) {
            // 활성화 상태만 필터링
            classes = classEntityRepository.findByUseYn(isActive);
        } else if (professorId != null) {
            // 교수자 ID만 필터링
            classes = classEntityRepository.findByProfessorUserId(professorId);
        } else {
            // 필터 없음 - 전체 조회
            classes = classEntityRepository.findAll();
        }

        // N+1 문제 방지를 위해 일괄 조회
        List<Long> classIds = classes.stream().map(ClassEntity::getClassId).toList();
        Map<Long, Long> classIdToInviteIdMap = classInviteRepository.findActiveByClassIds(classIds).stream()
                .collect(java.util.stream.Collectors.toMap(
                        invite -> invite.getClassEntity().getClassId(),
                        ClassInvite::getInviteId,
                        (existing, replacement) -> existing
                ));

        return classes.stream()
                .map(classEntity -> ClassResponse.from(classEntity, classIdToInviteIdMap.get(classEntity.getClassId())))
                .toList();
    }

    // 학생이 과거에 수강했던 삭제된 수업 목록 조회 (읽기 전용)
    @Override
    @Transactional(readOnly = true)
    public List<ClassResponse> getDeletedClasses(Long studentId) {

        List<ClassEnroll> deletedEnrollments = classEnrollRepository.findDeletedClassesByUserId(studentId);
        List<ClassEntity> classes = deletedEnrollments.stream()
                .map(ClassEnroll::getClassEntity)
                .toList();

        // N+1 문제 방지를 위해 일괄 조회
        List<Long> classIds = classes.stream().map(ClassEntity::getClassId).toList();
        Map<Long, Long> classIdToInviteIdMap = classInviteRepository.findActiveByClassIds(classIds).stream()
                .collect(java.util.stream.Collectors.toMap(
                        invite -> invite.getClassEntity().getClassId(),
                        ClassInvite::getInviteId,
                        (existing, replacement) -> existing
                ));

        return classes.stream()
                .map(classEntity -> ClassResponse.from(classEntity, classIdToInviteIdMap.get(classEntity.getClassId())))
                .toList();
    }

    /**
     * 수업이 삭제되었는지 확인하는 유틸리티 메서드
     * 삭제된 수업(useYn=false)에 대한 쓰기 작업을 차단하기 위해 사용
     */
    private void validateClassNotDeleted(Long classId) {
        ClassEntity classEntity = classEntityRepository.findById(classId)
                .orElseThrow(() -> new ResourceNotFoundException("수업을 찾을 수 없습니다. ID: " + classId));

        if (!classEntity.getUseYn()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE,
                "삭제된 수업입니다. 읽기 전용으로만 접근 가능합니다.");
        }
    }
}
