package com.cinemax.domain.notice.service;

import com.cinemax.domain.notice.dto.NoticeRequest;
import com.cinemax.domain.notice.dto.NoticeResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Notice Service Interface
 */
public interface NoticeService {

    /**
     * 공지사항 생성
     */
    NoticeResponse createNotice(Long authorId, NoticeRequest request);

    /**
     * 공지사항 수정
     */
    NoticeResponse updateNotice(Long noticeId, Long authorId, NoticeRequest request);

    /**
     * 공지사항 삭제
     */
    void deleteNotice(Long noticeId, Long authorId);

    /**
     * 공지사항 단건 조회 (조회수 증가)
     */
    NoticeResponse getNoticeById(Long noticeId);

    /**
     * 전체 공지사항 목록 조회 (페이징)
     */
    Page<NoticeResponse> getAllNotices(Pageable pageable);

    /**
     * 수업별 공지사항 목록 조회 (페이징)
     */
    Page<NoticeResponse> getNoticesByClass(Long classId, Pageable pageable);

    /**
     * 중요 공지사항 목록 조회
     */
    List<NoticeResponse> getImportantNotices();

    /**
     * 수업별 중요 공지사항 조회
     */
    List<NoticeResponse> getImportantNoticesByClass(Long classId);

    /**
     * 공지사항 제목 검색 (페이징)
     */
    Page<NoticeResponse> searchNotices(String keyword, Pageable pageable);

    /**
     * 수업별 공지사항 제목 검색 (페이징)
     */
    Page<NoticeResponse> searchNoticesByClass(Long classId, String keyword, Pageable pageable);

    /**
     * 작성자별 공지사항 조회
     */
    List<NoticeResponse> getNoticesByAuthor(Long authorId);
}
