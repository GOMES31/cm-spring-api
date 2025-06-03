package dev.edugomes.springapi.service.task;

import dev.edugomes.springapi.dto.request.CreateTaskRequest;
import dev.edugomes.springapi.dto.request.UpdateTaskRequest;
import dev.edugomes.springapi.dto.response.TaskResponse;

import java.util.List;

public interface TaskService {

    TaskResponse createTask(CreateTaskRequest createTaskRequest, String userEmail);

    TaskResponse getTaskById(Long id, String userEmail);

    List<TaskResponse> getAllTasksForUser(String userEmail);

    List<TaskResponse> getTasksByProject(Long projectId, String userEmail);

    List<TaskResponse> getTasksAssignedToUser(String userEmail);

    TaskResponse updateTask(Long id, UpdateTaskRequest updateTaskRequest, String userEmail);

    void deleteTask(Long id, String userEmail);
}