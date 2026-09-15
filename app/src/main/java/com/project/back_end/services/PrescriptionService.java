package com.project.back_end.services;

import com.project.back_end.models.Prescription;
import com.project.back_end.repo.PrescriptionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;

    public PrescriptionService(PrescriptionRepository prescriptionRepository) {
        this.prescriptionRepository = prescriptionRepository;
    }

    public ResponseEntity<?> savePrescription(Prescription prescription) {
        try {
            List<Prescription> existing =
                    prescriptionRepository.findByAppointmentId(
                            prescription.getAppointmentId()
                    );

            if (!existing.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of(
                                "message",
                                "Prescription already exists for this appointment"
                        ));
            }

            Prescription saved =
                    prescriptionRepository.save(prescription);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Prescription saved successfully");
            response.put("prescription", saved);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(response);

        } catch (Exception e) {
            return ResponseEntity.status(
                            HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "message",
                            "Unable to save prescription"
                    ));
        }
    }

    public ResponseEntity<?> getPrescription(Long appointmentId) {
        try {
            List<Prescription> prescriptions =
                    prescriptionRepository.findByAppointmentId(
                            appointmentId
                    );

            if (prescriptions.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of(
                                "message",
                                "Prescription not found"
                        ));
            }

            return ResponseEntity.ok(
                    Map.of("prescriptions", prescriptions)
            );

        } catch (Exception e) {
            return ResponseEntity.status(
                            HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "message",
                            "Unable to retrieve prescription"
                    ));
        }
    }
}