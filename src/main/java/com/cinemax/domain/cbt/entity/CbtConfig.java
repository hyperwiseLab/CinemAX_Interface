package com.cinemax.domain.cbt.entity;

import com.cinemax.core.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 반별 CBT 설정. 반 하나 = 자격증 하나이므로 CLASS_ID 당 1행.
 */
@Entity
@Table(name = "TBL_CBT_CONFIG",
        uniqueConstraints = @UniqueConstraint(name = "UK_CBT_CONFIG_CLASS", columnNames = "CLASS_ID"))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CbtConfig extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CONFIG_ID")
    private Long configId;

    // 반/세션 삭제를 막지 않도록 단순 참조값으로만 저장 (FK 제약 없음)
    @Column(name = "CLASS_ID", nullable = false)
    private Long classId;

    // 최종 합격 점수 (총점 100점 기준)
    @Column(name = "PASS_SCORE", nullable = false)
    private Double passScore;

    @Builder
    public CbtConfig(Long classId, Double passScore) {
        this.classId = classId;
        this.passScore = passScore;
    }

    public static CbtConfig create(Long classId, Double passScore) {
        return CbtConfig.builder()
                .classId(classId)
                .passScore(passScore)
                .build();
    }

    public void updatePassScore(Double passScore) {
        this.passScore = passScore;
    }
}
