package com.cinemax.domain.qna.entity;

import com.cinemax.core.entity.BaseTimeEntity;
import com.cinemax.domain.user.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "TBL_ANSWER")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Answer extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ANSWER_ID")
    private Long answerId;

    @Column(name = "QUESTION_ID")
    private Long questionId;

    @Column(name = "CONTENT", nullable = false, length = 500)
    private String content;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", nullable = false)
    private User user;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "QUESTION_ID", insertable = false, updatable = false)
    private Question question;

    // Answer 생성 팩토리 메서드
    public static Answer create(Long questionId, User user, String content) {
        return Answer.builder()
                .questionId(questionId)
                .user(user)
                .content(content)
                .build();
    }

    // Answer 내용 수정
    public void updateContent(String content) {
        this.content = content;
    }
}
