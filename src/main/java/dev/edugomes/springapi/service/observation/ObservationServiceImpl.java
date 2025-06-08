package dev.edugomes.springapi.service.observation;

import dev.edugomes.springapi.domain.Observation;
import dev.edugomes.springapi.domain.ObservationImage;
import dev.edugomes.springapi.domain.Task;
import dev.edugomes.springapi.domain.User;
import dev.edugomes.springapi.dto.request.CreateObservationRequest;
import dev.edugomes.springapi.dto.request.UpdateObservationRequest;
import dev.edugomes.springapi.dto.response.ObservationResponse;
import dev.edugomes.springapi.exception.ObservationNotFoundException;
import dev.edugomes.springapi.exception.TaskNotFoundException;
import dev.edugomes.springapi.exception.UnauthorizedException;
import dev.edugomes.springapi.repository.ObservationRepository;
import dev.edugomes.springapi.repository.TaskRepository;
import dev.edugomes.springapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import dev.edugomes.springapi.mapper.CustomMapper;

@Service
@RequiredArgsConstructor
@Transactional
public class ObservationServiceImpl implements ObservationService {

    private final ObservationRepository observationRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;


    private User getUserByEmail(String userEmail) {
        return userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UnauthorizedException("User not authenticated or found: " + userEmail));
    }

    @Override
    @Transactional
    public ObservationResponse createObservation(CreateObservationRequest createObservationRequest, String userEmail) {
        User currentUser = getUserByEmail(userEmail);

        Task task = taskRepository.findById(createObservationRequest.getTaskId())
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
        return CustomMapper.toObservationResponse(savedObservation);
    }

    @Override
    public ObservationResponse getObservationById(Long id, String userEmail) {
        User currentUser = getUserByEmail(userEmail);

        Observation observation = observationRepository.findById(id)
                .orElseThrow(() -> new ObservationNotFoundException("Observation not found with ID: " + id));

        boolean hasAccess = observation.getTask().getProject().getTeam().getMembers().stream()
                .anyMatch(teamMember -> teamMember.getUser().getId().equals(currentUser.getId()));

        if (!hasAccess) {
            throw new UnauthorizedException("User is not authorized to view this observation.");
        }

        return CustomMapper.toObservationResponse(observation);
    }

    @Override
    @Transactional
    public ObservationResponse updateObservation(Long id, UpdateObservationRequest updateObservationRequest, String userEmail) {
        User currentUser = getUserByEmail(userEmail);

        Observation observation = observationRepository.findById(id)
                .orElseThrow(() -> new ObservationNotFoundException("Observation not found with ID: " + id));

        if (!observation.getUser().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("You are not authorized to update this observation.");
        }

        observation.setMessage(updateObservationRequest.getMessage());

        if (updateObservationRequest.getImageUrl() != null) {
            if (updateObservationRequest.getImageUrl().isEmpty()) {
                observation.setImage(null);
            } else {
                if (observation.getImage() == null) {
                    observation.setImage(ObservationImage.builder().imageUrl(updateObservationRequest.getImageUrl()).build());
                } else {
                    observation.getImage().setImageUrl(updateObservationRequest.getImageUrl());
                }
            }
        }

        Observation updatedObservation = observationRepository.save(observation);
        return CustomMapper.toObservationResponse(updatedObservation);
    }
}