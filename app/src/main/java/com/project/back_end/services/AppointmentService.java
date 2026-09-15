package com.project.back_end.services;

import com.project.back_end.models.Appointment;
import com.project.back_end.repo.AppointmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;

    public AppointmentService(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    @Transactional
    public int bookAppointment(Appointment appointment) {
        try {
            appointmentRepository.save(appointment);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    @Transactional
    public String updateAppointment(
            Long appointmentId,
            Long patientId,
            Appointment updatedAppointment) {

        Optional<Appointment> existing =
                appointmentRepository.findById(appointmentId);

        if (existing.isEmpty()) {
            return "Appointment not found";
        }

        Appointment appointment = existing.get();

        if (appointment.getPatient() == null ||
                !appointment.getPatient().getId().equals(patientId)) {
            return "Patient is not authorized to update this appointment";
        }

        appointment.setDoctor(updatedAppointment.getDoctor());
        appointment.setAppointmentTime(
                updatedAppointment.getAppointmentTime()
        );
        appointment.setStatus(updatedAppointment.getStatus());

        appointmentRepository.save(appointment);

        return "Appointment updated successfully";
    }

    @Transactional
    public String cancelAppointment(Long appointmentId, Long patientId) {

        Optional<Appointment> existing =
                appointmentRepository.findById(appointmentId);

        if (existing.isEmpty()) {
            return "Appointment not found";
        }

        Appointment appointment = existing.get();

        if (appointment.getPatient() == null ||
                !appointment.getPatient().getId().equals(patientId)) {
            return "Patient is not authorized to cancel this appointment";
        }

        appointmentRepository.delete(appointment);

        return "Appointment cancelled successfully";
    }

    @Transactional(readOnly = true)
    public List<Appointment> getAppointments(
            Long doctorId,
            LocalDate date,
            String patientName) {

        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.plusDays(1).atStartOfDay();

        if (patientName != null && !patientName.isBlank()) {
            return appointmentRepository
                    .findByDoctorIdAndPatient_NameContainingIgnoreCaseAndAppointmentTimeBetween(
                            doctorId,
                            patientName,
                            start,
                            end
                    );
        }

        return appointmentRepository
                .findByDoctorIdAndAppointmentTimeBetween(
                        doctorId,
                        start,
                        end
                );
    }

    @Transactional
    public void changeStatus(long appointmentId, int status) {
        appointmentRepository.updateStatus(status, appointmentId);
    }

    public List<Appointment> getAppointmentsByPatient(Long patientId) {
        return appointmentRepository.findByPatientId(patientId);
    }
}