package com.cinemax.domain.task.service.impl;

import com.cinemax.core.exception.ResourceNotFoundException;
import com.cinemax.domain.task.dto.TaskRequest;
import com.cinemax.domain.task.dto.TaskResponse;
import com.cinemax.domain.task.entity.Task;
import com.cinemax.domain.task.mapper.TaskMapper;
import com.cinemax.domain.task.repository.TaskRepository;
import com.cinemax.domain.task.service.TaskService;
import com.cinemax.global.enums.TaskMode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Task 서비스 구현체
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TaskServiceImpl implements TaskService {

    private static final String NOT_FOUND_TASK = "Task를 찾을 수 없습니다. ";

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;

    // Task 생성 시작
    @Override
    @Transactional
    public TaskResponse createTask(TaskRequest request) {

        Task task = Task.create(
                request.getCycleId(),
                request.getCurId(),
                request.getWeekNo(),
                request.getTaskTitle(),
                request.getTaskMode(),
                request.getStartCode(),
                request.getConfigJson(),
                request.getOrderNo()
        );

        Task savedTask = taskRepository.save(task);

        return taskMapper.toDto(savedTask);
    }

    @Override
    public TaskResponse getTask(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_TASK + taskId));

        return taskMapper.toDto(task);
    }

    @Override
    public List<TaskResponse> getAllTasks() {
        List<Task> tasks = taskRepository.findAll();
        return taskMapper.toDto(tasks);
    }

    @Override
    public List<TaskResponse> getTasksByCycleId(Long cycleId) {
        List<Task> tasks = taskRepository.findByCycleId(cycleId);
        return taskMapper.toDto(tasks);
    }

    @Override
    public List<TaskResponse> getTasksByCurId(Long curId) {
        List<Task> tasks = taskRepository.findByCurId(curId);
        return taskMapper.toDto(tasks);
    }

    @Override
    public List<TaskResponse> getTasksByWeek(Long curId, Integer weekNo) {
        List<Task> tasks = taskRepository.findByWeek(curId, weekNo);
        return taskMapper.toDto(tasks);
    }

    @Override
    public List<TaskResponse> getTasksByCurIdAndCycleId(Long curId, Long cycleId) {
        List<Task> tasks = taskRepository.findByCurIdAndCycleId(curId, cycleId);
        return taskMapper.toDto(tasks);
    }

    @Override
    public List<TaskResponse> getTasksByMode(TaskMode taskMode) {
        List<Task> tasks = taskRepository.findByTaskMode(taskMode);
        return taskMapper.toDto(tasks);
    }

    // Task 수정 시작
    @Override
    @Transactional
    public TaskResponse updateTask(Long taskId, TaskRequest request) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_TASK + taskId));

        task.updateInfo(request.getTaskTitle(), request.getTaskMode(), request.getStartCode(), request.getConfigJson(), request.getOrderNo());

        return taskMapper.toDto(task);
    }

    // Task 삭제 시작
    @Override
    @Transactional
    public void deleteTask(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_TASK + taskId));

        taskRepository.delete(task);
    }
}


