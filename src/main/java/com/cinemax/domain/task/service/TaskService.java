package com.cinemax.domain.task.service;

import com.cinemax.domain.task.dto.TaskRequest;
import com.cinemax.domain.task.dto.TaskResponse;
import com.cinemax.global.enums.TaskMode;

import java.util.List;

/**
 * Task 서비스 인터페이스
 */
public interface TaskService {

    // Task 생성
    TaskResponse createTask(TaskRequest request);

    // Task 조회 (단일)
    TaskResponse getTask(Long taskId);

    // 모든 Task 조회
    List<TaskResponse> getAllTasks();

    // Cycle별 Task 조회
    List<TaskResponse> getTasksByCycleId(Long cycleId);

    // Curriculum별 Task 조회
    List<TaskResponse> getTasksByCurId(Long curId);

    // 주차별 Task 조회
    List<TaskResponse> getTasksByWeek(Long curId, Integer weekNo);

    // Curriculum과 Cycle로 Task 조회
    List<TaskResponse> getTasksByCurIdAndCycleId(Long curId, Long cycleId);

    // Task Mode로 조회
    List<TaskResponse> getTasksByMode(TaskMode taskMode);

    // Task 수정
    TaskResponse updateTask(Long taskId, TaskRequest request);

    // Task 삭제
    void deleteTask(Long taskId);
}
