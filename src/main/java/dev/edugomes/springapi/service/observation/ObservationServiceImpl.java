package dev.edugomes.springapi.service.observation;

import dev.edugomes.springapi.domain.Observation;
import dev.edugomes.springapi.domain.ObservationImage;
import dev.edugomes.springapi.domain.ProjectTask;
import dev.edugomes.springapi.domain.User;
import dev.edugomes.springapi.dto.request.CreateObservationRequest;
import dev.edugomes.springapi.dto.request.UpdateObservationRequest;
import dev.edugomes.springapi.dto.response.ObservationResponse;
import dev.edugomes.springapi.exception.ObservationNotFoundException;
import dev.edugomes.springapi.exception.TaskNotFoundException;
import dev.edugomes.springapi.exception.UnauthorizedException;
import dev.edugomes.springapi.repository.ObservationRepository;
import dev.edugomes.springapi.repository.ProjectRepository;
import dev.edugomes.springapi.repository.TaskRepository;
import dev.edugomes.springapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ObservationServiceImpl implements ObservationService {

    private final ObservationRepository observationRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;

    private User getCurrentAuthenticatedUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username;
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new UnauthorizedException("User not authenticated"));
    }

    private ObservationResponse mapToObservationResponse(Observation observation) {
        ObservationResponse.ImageInfo imageInfo = null;
        if (observation.getImage() != null) {
            imageInfo = ObservationResponse.ImageInfo.builder()
                    .id(observation.getImage().getId())
                    .imageUrl(observation.getImage().getImageUrl())
                    .uploadedAt(observation.getImage().getUploadedAt())
                    .build();
        }

        return ObservationResponse.builder()
                .id(observation.getId())
                .message(observation.getMessage())
                .createdAt(observation.getCreatedAt())
                .task(ObservationResponse.TaskInfo.builder()
                        .id(observation.getTask().getId())
                        .title(observation.getTask().getTitle())
                        .build())
                .user(ObservationResponse.UserInfo.builder()
                        .id(observation.getUser().getId())
                        .name(observation.getUser().getName())
                        .email(observation.getUser().getEmail())
                        .build())
                .image(imageInfo)
                .build();
    }

    @Override
    @Transactional
    public ObservationResponse createObservation(CreateObservationRequest createObservationRequest) {
        User currentUser = getCurrentAuthenticatedUser();

        ProjectTask task = taskRepository.findById(createObservationRequest.getTaskId())
                .orElseThrow(() -> new TaskNotFoundException("Task not found with ID: " + createObservationRequest.getTaskId()));

        boolean isUserPartOfTeam = task.getProject().getTeam().getMembers().stream()
                .anyMatch(teamMember -> teamMember.getUser().getId().equals(currentUser.getId()));

        if (!isUserPartOfTeam) {
            throw new UnauthorizedException("User is not a member of the team associated with this task's project.");
        }

        ObservationImage observationImage = null;
        if (createObservationRequest.getImageUrl() != null && !createObservationRequest.getImageUrl().isEmpty()) {
            observationImage = ObservationImage.builder()
                    .imageUrl(createObservationRequest.getImageUrl())
                    .build();
        }

        Observation observation = Observation.builder()
                .task(task)
                .user(currentUser)
                .message(createObservationRequest.getMessage())
                .image(observationImage)
                .build();

        Observation savedObservation = observationRepository.save(observation);
        return mapToObservationResponse(savedObservation);
    }

    @Override
    public ObservationResponse getObservationById(Long id) {
        Observation observation = observationRepository.findById(id)
                .orElseThrow(() -> new ObservationNotFoundException("Observation not found with ID: " + id));
        return mapToObservationResponse(observation);
    }

    @Override
    public List<ObservationResponse> getAllObservations() {
        return observationRepository.findAll().stream()
                .map(this::mapToObservationResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ObservationResponse> getObservationsByTaskId(Long taskId) {
        if (!taskRepository.existsById(taskId)) {
            throw new TaskNotFoundException("Task not found with ID: " + taskId);
        }
        return observationRepository.findByTaskIdOrderByCreatedAtDesc(taskId).stream()
                .map(this::mapToObservationResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ObservationResponse> getObservationsByUserId(Long userId) {
        return observationRepository.findByUserId(userId).stream()
                .map(this::mapToObservationResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ObservationResponse updateObservation(Long id, UpdateObservationRequest updateObservationRequest) {
        User currentUser = getCurrentAuthenticatedUser();

        Observation observation = observationRepository.findById(id)
                .orElseThrow(() -> new ObservationNotFoundException("Observation not found with ID: " + id));

        if (!observation.getUser().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("You are not authorized to update this observation.");
        }

        observation.setMessage(updateObservationRequest.getMessage());

        if (updateObservationRequest.getImageUrl() != null && !updateObservationRequest.getImageUrl().isEmpty()) {
            if (observation.getImage() == null) {
                observation.setImage(ObservationImage.builder().imageUrl(updateObservationRequest.getImageUrl()).build());
            } else {
                observation.getImage().setImageUrl(updateObservationRequest.getImageUrl());
            }
        } else if (updateObservationRequest.getImageUrl() != null && updateObservationRequest.getImageUrl().isEmpty()) {
            observation.setImage(null);
        }

        Observation updatedObservation = observationRepository.save(observation);
        return mapToObservationResponse(updatedObservation);
    }

    @Override
    @Transactional
    public void deleteObservation(Long id) {
        User currentUser = getCurrentAuthenticatedUser();

        Observation observation = observationRepository.findById(id)
                .orElseThrow(() -> new ObservationNotFoundException("Observation not found with ID: " + id));

        if (!observation.getUser().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("You are not authorized to delete this observation.");
        }

        observationRepository.delete(observation);
    }
}