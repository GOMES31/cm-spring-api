package dev.edugomes.springapi.service.observation;

import dev.edugomes.springapi.dto.request.CreateObservationRequest;
import dev.edugomes.springapi.dto.request.UpdateObservationRequest;
import dev.edugomes.springapi.dto.response.ObservationResponse;

public interface ObservationService {

    ObservationResponse createObservation(CreateObservationRequest createObservationRequest, String userEmail);

    ObservationResponse updateObservation(Long id, UpdateObservationRequest updateObservationRequest, String userEmail);

    ObservationResponse getObservationById(Long id, String userEmail);
}