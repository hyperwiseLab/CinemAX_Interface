package com.cinemax.domain.notice.service.impl;

import com.cinemax.core.exception.ResourceNotFoundException;
import com.cinemax.domain.notice.dto.NoticeRequest;
import com.cinemax.domain.notice.dto.NoticeResponse;
import com.cinemax.domain.notice.entity.Notice;
import com.cinemax.domain.notice.repository.NoticeRepository;
import com.cinemax.domain.notice.service.NoticeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class NoticeServiceImpl implements NoticeService {

    private final NoticeRepository noticeRepository;

    @Override
    @Transactional
    public NoticeResponse createNotice(Long authorId, NoticeRequest request) {
        log.info("공지사항 생성 시작: authorId={}, classId={}", authorId, request.getClassId());

        // Notice 생성
        Notice notice = Notice.create(
                authorId,
                request.getClassId(),
                request.getTitle(),
                request.getContent(),
                request.getIsImportant()
        );

        // 저장
        Notice savedNotice = noticeRepository.save(notice);

        log.info("공지사항 생성 완료: noticeId={}", savedNotice.getNoticeId());
        return NoticeResponse.from(savedNotice);
    }

    @Override
    @Transactional
    public NoticeResponse updateNotice(Long noticeId, Long authorId, NoticeRequest request) {
        log.info("공지사항 수정 시작: noticeId={}, authorId={}", noticeId, authorId);

        // Notice 조회
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new ResourceNotFoundException("Notice", "noticeId", noticeId));

        // 권한 확인 (작성자만 수정 가능)
        if (!notice.getAuthorId().equals(authorId)) {
            throw new IllegalStateException("공지사항 수정 권한이 없습니다");
        }

        // 정보 수정
        notice.updateInfo(request.getTitle(), request.getContent(), request.getIsImportant());

        log.info("공지사항 수정 완료: noticeId={}", noticeId);
        return NoticeResponse.from(notice);
    }

    @Override
    @Transactional
    public void deleteNotice(Long noticeId, Long authorId) {
        log.info("공지사항 삭제 시작: noticeId={}, authorId={}", noticeId, authorId);

        // Notice 조회
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new ResourceNotFoundException("Notice", "noticeId", noticeId));

        // 권한 확인 (작성자만 삭제 가능)
        if (!notice.getAuthorId().equals(authorId)) {
            throw new IllegalStateException("공지사항 삭제 권한이 없습니다");
        }

        // Soft delete
        notice.delete();

        log.info("공지사항 삭제 완료: noticeId={}", noticeId);
    }

    @Override
    @Transactional
    public NoticeResponse getNoticeById(Long noticeId) {
        log.info("공지사항 조회 시작: noticeId={}", noticeId);

        // Notice 조회 (작성자 정보 포함)
        Notice notice = noticeRepository.findByIdWithAuthor(noticeId)
                .orElseThrow(() -> new ResourceNotFoundException("Notice", "noticeId", noticeId));

        // 조회수 증가
        notice.increaseViewCount();

        log.info("공지사항 조회 완료: noticeId={}, viewCount={}", noticeId, notice.getViewCount());
        return NoticeResponse.from(notice);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NoticeResponse> getAllNotices(Pageable pageable) {
        log.info("전체 공지사항 목록 조회: page={}, size={}", pageable.getPageNumber(), pageable.getPageSize());

        Page<Notice> notices = noticeRepository.findAllNotices(pageable);

        log.info("전체 공지사항 목록 조회 완료: totalElements={}", notices.getTotalElements());
        return notices.map(NoticeResponse::from);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NoticeResponse> getNoticesByClass(Long classId, Pageable pageable) {
        log.info("수업별 공지사항 목록 조회: classId={}, page={}", classId, pageable.getPageNumber());

        Page<Notice> notices = noticeRepository.findByClassId(classId, pageable);

        log.info("수업별 공지사항 목록 조회 완료: classId={}, totalElements={}", classId, notices.getTotalElements());
        return notices.map(NoticeResponse::from);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NoticeResponse> getImportantNotices() {
        log.info("중요 공지사항 목록 조회");

        List<Notice> notices = noticeRepository.findImportantNotices();

        log.info("중요 공지사항 목록 조회 완료: count={}", notices.size());
        return notices.stream()
                .map(NoticeResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<NoticeResponse> getImportantNoticesByClass(Long classId) {
        log.info("수업별 중요 공지사항 조회: classId={}", classId);

        List<Notice> notices = noticeRepository.findImportantNoticesByClass(classId);

        log.info("수업별 중요 공지사항 조회 완료: classId={}, count={}", classId, notices.size());
        return notices.stream()
                .map(NoticeResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NoticeResponse> searchNotices(String keyword, Pageable pageable) {
        log.info("공지사항 검색: keyword={}, page={}", keyword, pageable.getPageNumber());

        Page<Notice> notices = noticeRepository.searchByTitle(keyword, pageable);

        log.info("공지사항 검색 완료: keyword={}, totalElements={}", keyword, notices.getTotalElements());
        return notices.map(NoticeResponse::from);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NoticeResponse> searchNoticesByClass(Long classId, String keyword, Pageable pageable) {
        log.info("수업별 공지사항 검색: classId={}, keyword={}", classId, keyword);

        Page<Notice> notices = noticeRepository.searchByTitleAndClass(classId, keyword, pageable);

        log.info("수업별 공지사항 검색 완료: classId={}, keyword={}, totalElements={}",
                classId, keyword, notices.getTotalElements());
        return notices.map(NoticeResponse::from);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NoticeResponse> getNoticesByAuthor(Long authorId) {
        log.info("작성자별 공지사항 조회: authorId={}", authorId);

        List<Notice> notices = noticeRepository.findByAuthorId(authorId);

        log.info("작성자별 공지사항 조회 완료: authorId={}, count={}", authorId, notices.size());
        return notices.stream()
                .map(NoticeResponse::from)
                .collect(Collectors.toList());
    }
}
