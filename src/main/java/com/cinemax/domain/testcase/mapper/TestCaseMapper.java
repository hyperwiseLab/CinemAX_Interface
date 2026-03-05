package com.cinemax.domain.testcase.mapper;

import com.cinemax.core.config.GlobalMapperConfig;
import com.cinemax.domain.testcase.dto.TestCaseRequest;
import com.cinemax.domain.testcase.dto.TestCaseResponse;
import com.cinemax.domain.testcase.entity.TestCase;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * TestCase 엔티티와 DTO 간의 변환을 담당하는 MapStruct 매퍼
 */
@Mapper(config = GlobalMapperConfig.class)
public interface TestCaseMapper {

    // TestCase Entity -> TestCaseRequest DTO 변환
    @Mapping(source = "taskId", target = "taskId")
    @Mapping(source = "cycleId", target = "cycleId")
    @Mapping(target = "curId", ignore = true)  // Request에만 존재
    @Mapping(target = "weekNo", ignore = true)  // Request에만 존재
    @Mapping(target = "syntaxId", ignore = true)  // Request에만 존재
    @Mapping(source = "startCode", target = "startCode")
    @Mapping(source = "testCode", target = "testCode")
    @Mapping(source = "inputText", target = "inputText")
    @Mapping(source = "expectedOutput", target = "expectedOutput")
    @Mapping(source = "weight", target = "weight")
    TestCaseRequest toDto(TestCase entity);

    List<TestCaseRequest> toDto(List<TestCase> entities);

    // TestCase Entity -> TestCaseResponse DTO 변환 (전체 정보 포함)
    @Mapping(source = "testCaseId", target = "testCaseId")
    @Mapping(source = "taskId", target = "taskId")
    @Mapping(source = "cycleId", target = "cycleId")
    @Mapping(source = "startCode", target = "startCode")
    @Mapping(source = "testCode", target = "testCode")
    @Mapping(source = "inputText", target = "inputText")
    @Mapping(source = "expectedOutput", target = "expectedOutput")
    @Mapping(source = "weight", target = "weight")
    @Mapping(source = "createDt", target = "createDt")
    @Mapping(source = "updateDt", target = "updateDt")
    TestCaseResponse toResponse(TestCase entity);

    // TestCase Entity -> TestCaseResponse DTO 변환 (공개 정보만 - testCode 제외)
    @Mapping(source = "testCaseId", target = "testCaseId")
    @Mapping(source = "taskId", target = "taskId")
    @Mapping(source = "cycleId", target = "cycleId")
    @Mapping(source = "startCode", target = "startCode")
    @Mapping(target = "testCode", ignore = true)  // 학생에게 공개하지 않음
    @Mapping(source = "inputText", target = "inputText")
    @Mapping(source = "expectedOutput", target = "expectedOutput")
    @Mapping(source = "weight", target = "weight")
    @Mapping(target = "createDt", ignore = true)
    @Mapping(target = "updateDt", ignore = true)
    TestCaseResponse toPublicResponse(TestCase entity);

    // TestCaseRequest DTO -> TestCase Entity 변환
    @Mapping(source = "taskId", target = "taskId")
    @Mapping(source = "cycleId", target = "cycleId")
    @Mapping(source = "startCode", target = "startCode")
    @Mapping(source = "testCode", target = "testCode")
    @Mapping(source = "inputText", target = "inputText")
    @Mapping(source = "expectedOutput", target = "expectedOutput")
    @Mapping(source = "weight", target = "weight")
    TestCase toEntity(TestCaseRequest dto);
}
