package com.cinemax.domain.qna.mapper;

import com.cinemax.core.config.GlobalMapperConfig;
import com.cinemax.domain.qna.dto.AnswerRequest;
import com.cinemax.domain.qna.dto.AnswerResponse;
import com.cinemax.domain.qna.entity.Answer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * Answer 엔티티와 DTO 간 변환을 위한 Mapper
 */
@Mapper(config = GlobalMapperConfig.class)
public interface AnswerMapper {

    /**
     * Answer Entity -> AnswerResponse DTO 변환
     */
    @Mapping(source = "answerId", target = "answerId")
    @Mapping(source = "questionId", target = "questionId")
    @Mapping(source = "user.userId", target = "userId")
    @Mapping(source = "user.name", target = "userName")
    @Mapping(source = "user.email", target = "userEmail")
    AnswerResponse toDto(Answer entity);

    /**
     * List 변환을 위한 메서드
     */
    List<AnswerResponse> toDto(List<Answer> entities);
}
