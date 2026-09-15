package com.project.back_end.services;

import com.project.back_end.DTO.Login;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.DoctorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;
    private final TokenService tokenService;

    public DoctorService(
            DoctorRepository doctorRepository,
            AppointmentRepository appointmentRepository,
            TokenService tokenService) {

        this.doctorRepository = doctorRepository;
        this.appointmentRepository = appointmentRepository;
        this.tokenService = tokenService;
    }

    @Transactional(readOnly = true)
    public List<String> getDoctorAvailability(Long doctorId, LocalDate date) {

        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Doctor not found"));

        List<String> availableTimes = doctor.getAvailableTimes();

        if (availableTimes == null) {
            return new ArrayList<>();
        }

        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.plusDays(1).atStartOfDay();

        List<Appointment> appointments =
                appointmentRepository
                        .findByDoctorIdAndAppointmentTimeBetween(
                                doctorId, start, end);

        List<String> bookedTimes = appointments.stream()
                .map(a -> a.getAppointmentTime()
                        .toLocalTime()
                        .toString())
                .toList();

        return availableTimes.stream()
                .filter(time -> !bookedTimes.contains(time))
                .collect(Collectors.toList());
    }

    @Transactional
    public Doctor saveDoctor(Doctor doctor) {

        if (doctorRepository.findByEmail(doctor.getEmail()) != null) {
            return null;
        }

        return doctorRepository.save(doctor);
    }

    @Transactional
    public Doctor updateDoctor(Doctor doctor) {

        if (doctor.getId() == null ||
                !doctorRepository.existsById(doctor.getId())) {
            return null;
        }

        return doctorRepository.save(doctor);
    }

    @Transactional(readOnly = true)
    public List<Doctor> getDoctors() {
        return doctorRepository.findAll();
    }

    @Transactional
    public boolean deleteDoctor(Long id) {

        if (!doctorRepository.existsById(id)) {
            return false;
        }

        appointmentRepository.deleteAllByDoctorId(id);
        doctorRepository.deleteById(id);

        return true;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> validateDoctor(Login login) {

        Map<String, Object> response = new HashMap<>();

        Doctor doctor = doctorRepository.findByEmail(login.getEmail());

        if (doctor == null) {
            response.put("success", false);
            response.put("message", "Invalid email or password");
            return response;
        }

        if (doctor.getPassword() == null ||
                !doctor.getPassword().equals(login.getPassword())) {

            response.put("success", false);
            response.put("message", "Invalid email or password");
            return response;
        }

        String token = tokenService.generateToken(doctor.getEmail());

        response.put("success", true);
        response.put("message", "Login successful");
        response.put("token", token);
        response.put("doctor", doctor);

        return response;
    }

    @Transactional(readOnly = true)
    public List<Doctor> findDoctorByName(String name) {
        return doctorRepository.findByNameLike(name);
    }

    @Transactional(readOnly = true)
    public List<Doctor> filterDoctorsByNameSpecilityandTime(
            String name,
            String specialty,
            String time) {

        List<Doctor> doctors =
                doctorRepository
                        .findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(
                                name, specialty);

        return filterDoctorByTime(doctors, time);
    }

    public List<Doctor> filterDoctorByTime(
            List<Doctor> doctors,
            String time) {

        if (time == null || time.isBlank()) {
            return doctors;
        }

        boolean am = time.equalsIgnoreCase("AM");
        boolean pm = time.equalsIgnoreCase("PM");

        if (!am && !pm) {
            return doctors;
        }

        return doctors.stream()
                .filter(doctor ->
                        doctor.getAvailableTimes() != null &&
                        doctor.getAvailableTimes().stream().anyMatch(slot -> {
                            try {
                                LocalTime slotTime =
                                        LocalTime.parse(slot);

                                return am
                                        ? slotTime.isBefore(LocalTime.NOON)
                                        : !slotTime.isBefore(LocalTime.NOON);
                            } catch (Exception e) {
                                return false;
                            }
                        }))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<Doctor> filterDoctorByNameAndTime(
            String name,
            String time) {

        return filterDoctorByTime(
                doctorRepository.findByNameLike(name),
                time);
    }

    @Transactional(readOnly = true)
    public List<Doctor> filterDoctorByNameAndSpecility(
            String name,
            String specialty) {

        return doctorRepository
                .findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(
                        name, specialty);
    }

    @Transactional(readOnly = true)
    public List<Doctor> filterDoctorByTimeAndSpecility(
            String time,
            String specialty) {

        return filterDoctorByTime(
                doctorRepository.findBySpecialtyIgnoreCase(specialty),
                time);
    }

    @Transactional(readOnly = true)
    public List<Doctor> filterDoctorBySpecility(String specialty) {
        return doctorRepository.findBySpecialtyIgnoreCase(specialty);
    }

    @Transactional(readOnly = true)
    public List<Doctor> filterDoctorsByTime(String time) {
        return filterDoctorByTime(
                doctorRepository.findAll(),
                time);
    }
}