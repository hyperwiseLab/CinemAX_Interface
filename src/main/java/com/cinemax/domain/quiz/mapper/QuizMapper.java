package com.cinemax.domain.quiz.mapper;

import com.cinemax.core.config.GlobalMapperConfig;
import com.cinemax.domain.quiz.dto.QuizOptionResponse;
import com.cinemax.domain.quiz.dto.QuizQuestionResponse;
import com.cinemax.domain.quiz.dto.QuizResponse;
import com.cinemax.domain.quiz.entity.Quiz;
import com.cinemax.domain.quiz.entity.QuizOption;
import com.cinemax.domain.quiz.entity.QuizQuestion;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Quiz 엔티티 -> DTO 변환 Mapper
 */
@Mapper(config = GlobalMapperConfig.class)
public interface QuizMapper {

    @Mapping(source = "quizId", target = "quizId")
    @Mapping(source = "classId", target = "classId")
    @Mapping(source = "weekNo", target = "weekNo")
    @Mapping(source = "weeklySessionId", target = "weeklySessionId")
    @Mapping(source = "title", target = "title")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "createdBy", target = "createdBy")
    @Mapping(source = "questions", target = "questions")
    QuizResponse toDto(Quiz entity);

    @Mapping(source = "questionId", target = "questionId")
    @Mapping(source = "type", target = "type")
    @Mapping(source = "orderNo", target = "orderNo")
    @Mapping(source = "content", target = "content")
    @Mapping(source = "answer", target = "answer")
    @Mapping(source = "explanation", target = "explanation")
    @Mapping(source = "score", target = "score")
    @Mapping(source = "options", target = "options")
    QuizQuestionResponse toDto(QuizQuestion entity);

    @Mapping(source = "optionId", target = "optionId")
    @Mapping(source = "orderNo", target = "orderNo")
    @Mapping(source = "content", target = "content")
    @Mapping(source = "isCorrect", target = "isCorrect")
    QuizOptionResponse toDto(QuizOption entity);
}
