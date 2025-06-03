package dev.edugomes.springapi.service.observation;

import dev.edugomes.springapi.dto.request.CreateObservationRequest;
import dev.edugomes.springapi.dto.request.UpdateObservationRequest;
import dev.edugomes.springapi.dto.response.ObservationResponse;

import java.util.List;

public interface ObservationService {

    ObservationResponse createObservation(CreateObservationRequest createObservationRequest);

    ObservationResponse getObservationById(Long id);

    List<ObservationResponse> getAllObservations();

    List<ObservationResponse> getObservationsByTaskId(Long taskId);

    List<ObservationResponse> getObservationsByUserId(Long userId);

    ObservationResponse updateObservation(Long id, UpdateObservationRequest updateObservationRequest);

    void deleteObservation(Long id);
}