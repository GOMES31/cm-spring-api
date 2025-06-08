package dev.edugomes.springapi.service.task;

import dev.edugomes.springapi.dto.request.CreateTaskRequest;
import dev.edugomes.springapi.dto.request.UpdateTaskRequest;
import dev.edugomes.springapi.dto.response.TaskResponse;
import dev.edugomes.springapi.dto.response.TaskResponse.ObservationInfo;

import java.util.List;

public interface TaskService {

    TaskResponse createTask(CreateTaskRequest createTaskRequest, String userEmail);

    TaskResponse updateTask(Long id, UpdateTaskRequest updateTaskRequest, String userEmail);

    TaskResponse getTaskById(Long id, String userEmail);

    List<ObservationInfo> getObservationsForTask(Long taskId, String userEmail);
}