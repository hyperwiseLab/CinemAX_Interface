package com.cinemax.domain.notice.dto;

import com.cinemax.domain.notice.entity.Notice;
import com.cinemax.domain.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "공지사항 응답 DTO")
public class NoticeResponse {

    @Schema(description = "공지사항 ID")
    private Long noticeId;

    @Schema(description = "작성자 ID")
    private Long authorId;

    @Schema(description = "작성자 정보")
    private AuthorInfo author;

    @Schema(description = "수업 ID")
    private Long classId;

    @Schema(description = "공지사항 제목")
    private String title;

    @Schema(description = "공지사항 내용")
    private String content;

    @Schema(description = "중요 공지사항 여부")
    private Boolean isImportant;

    @Schema(description = "조회수")
    private Long viewCount;

    @Schema(description = "생성일시")
    private LocalDateTime createDt;

    @Schema(description = "수정일시")
    private LocalDateTime updateDt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "작성자 정보")
    public static class AuthorInfo {
        @Schema(description = "사용자 ID")
        private Long userId;

        @Schema(description = "이름")
        private String name;

        @Schema(description = "이메일")
        private String email;

        public static AuthorInfo from(User user) {
            if (user == null) {
                return null;
            }
            return AuthorInfo.builder()
                    .userId(user.getUserId())
                    .name(user.getName())
                    .email(user.getEmail())
                    .build();
        }
    }

    /**
     * Entity를 DTO로 변환
     */
    public static NoticeResponse from(Notice notice) {
        return NoticeResponse.builder()
                .noticeId(notice.getNoticeId())
                .authorId(notice.getAuthorId())
                .author(AuthorInfo.from(notice.getAuthor()))
                .classId(notice.getClassId())
                .title(notice.getTitle())
                .content(notice.getContent())
                .isImportant(notice.getIsImportant())
                .viewCount(notice.getViewCount())
                .createDt(notice.getCreateDt())
                .updateDt(notice.getUpdateDt())
                .build();
    }
}
