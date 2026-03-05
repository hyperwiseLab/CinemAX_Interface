package com.cinemax.domain.notice.repository;

import com.cinemax.domain.notice.entity.Notice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {

    // 공지사항 ID로 조회 (작성자 정보 포함)
    @Query("SELECT n FROM Notice n LEFT JOIN FETCH n.author WHERE n.noticeId = :noticeId AND n.useYn = true")
    Optional<Notice> findByIdWithAuthor(@Param("noticeId") Long noticeId);

    // 전체 공지사항 목록 조회 (페이징)
    @Query("SELECT n FROM Notice n LEFT JOIN FETCH n.author WHERE n.useYn = true ORDER BY n.isImportant DESC, n.createDt DESC")
    Page<Notice> findAllNotices(Pageable pageable);

    // 수업별 공지사항 목록 조회 (페이징)
    @Query("SELECT n FROM Notice n LEFT JOIN FETCH n.author WHERE n.classId = :classId AND n.useYn = true ORDER BY n.isImportant DESC, n.createDt DESC")
    Page<Notice> findByClassId(@Param("classId") Long classId, Pageable pageable);

    // 중요 공지사항 목록 조회 (최신 순)
    @Query("SELECT n FROM Notice n LEFT JOIN FETCH n.author WHERE n.isImportant = true AND n.useYn = true ORDER BY n.createDt DESC")
    List<Notice> findImportantNotices();

    // 수업별 중요 공지사항 조회
    @Query("SELECT n FROM Notice n LEFT JOIN FETCH n.author WHERE n.classId = :classId AND n.isImportant = true AND n.useYn = true ORDER BY n.createDt DESC")
    List<Notice> findImportantNoticesByClass(@Param("classId") Long classId);

    // 제목으로 검색 (페이징)
    @Query("SELECT n FROM Notice n LEFT JOIN FETCH n.author WHERE n.title LIKE CONCAT('%', :keyword, '%') AND n.useYn = true ORDER BY n.isImportant DESC, n.createDt DESC")
    Page<Notice> searchByTitle(@Param("keyword") String keyword, Pageable pageable);

    // 수업별 제목 검색 (페이징)
    @Query("SELECT n FROM Notice n LEFT JOIN FETCH n.author WHERE n.classId = :classId AND n.title LIKE CONCAT('%', :keyword, '%') AND n.useYn = true ORDER BY n.isImportant DESC, n.createDt DESC")
    Page<Notice> searchByTitleAndClass(@Param("classId") Long classId, @Param("keyword") String keyword, Pageable pageable);

    // 작성자별 공지사항 조회
    @Query("SELECT n FROM Notice n WHERE n.authorId = :authorId AND n.useYn = true ORDER BY n.createDt DESC")
    List<Notice> findByAuthorId(@Param("authorId") Long authorId);

    // 전체 공지사항 수 조회 (관리자 대시보드용)
    Long countByUseYn(Boolean useYn);
}
