package com.cinemax.domain.file.entity;

import com.cinemax.core.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "TBL_FILE")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class File extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "FILE_ID")
    private Long fileId;

    @Column(name = "ORG_FILE_NM", nullable = false, length = 200)
    private String orgFileNm;

    @Column(name = "SAVE_FILE_NM", nullable = false, length = 200)
    private String saveFileNm;

    @Column(name = "USE_YN", nullable = false)
    private Boolean useYn = true;
}
