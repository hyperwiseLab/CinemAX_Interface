package com.cinemax.domain.qna.mapper;

import com.cinemax.core.config.GlobalMapperConfig;
import com.cinemax.domain.qna.dto.QuestionRequest;
import com.cinemax.domain.qna.dto.QuestionResponse;
import com.cinemax.domain.qna.entity.Question;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

/**
 * Question 엔티티와 DTO 간 변환을 위한 Mapper
 */
@Mapper(config = GlobalMapperConfig.class, uses = {AnswerMapper.class})
public interface QuestionMapper {

    /**
     * Question Entity -> QuestionResponse DTO 변환 (답변 포함)
     */
    @Mapping(source = "questionId", target = "questionId")
    @Mapping(source = "classId", target = "classId")
    @Mapping(source = "classEntity.classNm", target = "classNm")
    @Mapping(source = "classEntity.year", target = "year")
    @Mapping(source = "classEntity.term", target = "semester")
    @Mapping(source = "weeklySessionId", target = "weeklySessionId")
    @Mapping(source = "weekNo", target = "weekNo")
    @Mapping(source = "weeklySession.inviteId", target = "inviteId")
    @Mapping(source = "user.userId", target = "userId")
    @Mapping(source = "user.name", target = "userName")
    @Mapping(source = "user.email", target = "userEmail")
    @Mapping(source = "answers", target = "answers")  // AnswerMapper 자동 사용
    @Mapping(expression = "java(entity.getAnswers().size())", target = "answerCount")
    QuestionResponse toDto(Question entity);

    /**
     * Question Entity -> QuestionResponse DTO 변환 (답변 제외)
     */
    @Mapping(source = "questionId", target = "questionId")
    @Mapping(source = "classId", target = "classId")
    @Mapping(source = "classEntity.classNm", target = "classNm")
    @Mapping(source = "classEntity.year", target = "year")
    @Mapping(source = "classEntity.term", target = "semester")
    @Mapping(source = "weeklySessionId", target = "weeklySessionId")
    @Mapping(source = "weekNo", target = "weekNo")
    @Mapping(source = "weeklySession.inviteId", target = "inviteId")
    @Mapping(source = "user.userId", target = "userId")
    @Mapping(source = "user.name", target = "userName")
    @Mapping(source = "user.email", target = "userEmail")
    @Mapping(target = "answers", ignore = true)
    @Mapping(expression = "java(entity.getAnswers().size())", target = "answerCount")
    @Named("withoutAnswers")
    QuestionResponse toDtoWithoutAnswers(Question entity);
}
