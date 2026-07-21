package com.cinemax.domain.weeklySession.mapper;

import com.cinemax.core.config.GlobalMapperConfig;
import com.cinemax.domain.classes.entity.ClassEntity;
import com.cinemax.domain.classes.entity.ClassInvite;
import com.cinemax.domain.weeklySession.dto.WeeklySessionRequest;
import com.cinemax.domain.weeklySession.dto.WeeklySessionResponse;
import com.cinemax.domain.weeklySession.entity.WeeklySession;
import org.hibernate.Hibernate;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

/**
 * WeeklySession 엔티티와 DTO 간의 변환을 담당하는 MapStruct 매퍼
 * Entity ↔ DTO 변환 로직을 중앙화하여 관심사 분리
 */
@Mapper(config = GlobalMapperConfig.class)
public interface WeeklySessionMapper {

    // WeeklySession Entity -> WeeklySessionRequest DTO 변환
    @Mapping(source = "inviteId", target = "inviteId")
    @Mapping(source = "weekNo", target = "weekNo")
    WeeklySessionRequest toDto(WeeklySession entity);

    List<WeeklySessionRequest> toDto(List<WeeklySession> entities);

    // WeeklySession Entity -> WeeklySessionResponse DTO 변환
    @Mapping(source = "weeklySessionId", target = "weeklySessionId")
    @Mapping(source = "inviteId", target = "inviteId")
    @Mapping(source = "weekNo", target = "weekNo")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "startDt", target = "startDt")
    @Mapping(source = "updateDt", target = "endDt")  // 종료 시간은 updateDt 사용
    @Mapping(source = "autoClosed", target = "autoClosed")
    @Mapping(source = "createDt", target = "createDt")
    @Mapping(source = "updateDt", target = "updateDt")
    @Mapping(target = "classId", ignore = true)   // fillClassInfo 에서 지연로딩 안전하게 채움
    @Mapping(target = "classNm", ignore = true)
    WeeklySessionResponse toResponse(WeeklySession entity);

    List<WeeklySessionResponse> toResponse(List<WeeklySession> entities);

    // 반 정보는 연관이 "이미 초기화된 경우에만" 채운다.
    // 컨트롤러(트랜잭션 밖)에서 변환될 수 있어 지연 프록시에 접근하면 LazyInitializationException 이 나기 때문.
    // fetch join 으로 조회한 경로(/weekly-sessions/status/{status})는 값이 채워지고, 그 외 경로는 null (프론트 폴백).
    @AfterMapping
    default void fillClassInfo(WeeklySession entity,
                               @MappingTarget WeeklySessionResponse.WeeklySessionResponseBuilder builder) {
        ClassInvite invite = entity.getClassInvite();
        if (invite == null || !Hibernate.isInitialized(invite)) {
            return;
        }
        ClassEntity classEntity = invite.getClassEntity();
        if (classEntity == null || !Hibernate.isInitialized(classEntity)) {
            return;
        }
        builder.classId(classEntity.getClassId());
        builder.classNm(classEntity.getClassNm());
    }

    // WeeklySessionRequest DTO -> WeeklySession Entity 변환
    @Mapping(source = "inviteId", target = "inviteId")
    @Mapping(source = "weekNo", target = "weekNo")
    @Mapping(target = "weeklySessionId", ignore = true)
    @Mapping(target = "classInvite", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "startDt", ignore = true)
    @Mapping(target = "autoClosed", ignore = true)
    @Mapping(target = "activityMonitors", ignore = true)
    WeeklySession toEntity(WeeklySessionRequest dto);

    List<WeeklySession> toEntity(List<WeeklySessionRequest> dtos);
}
