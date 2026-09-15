package com.project.back_end.services;

import com.project.back_end.models.Doctor;
import com.project.back_end.repo.DoctorRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Service {

    private final TokenService tokenService;
    private final DoctorService doctorService;
    private final DoctorRepository doctorRepository;

    public Service(
            TokenService tokenService,
            DoctorService doctorService,
            DoctorRepository doctorRepository) {

        this.tokenService = tokenService;
        this.doctorService = doctorService;
        this.doctorRepository = doctorRepository;
    }

    public boolean validateToken(String token, String role) {
        return tokenService.validateToken(token, role);
    }

    public List<Doctor> filterDoctors(
            String name,
            String time,
            String specialty) {

        boolean hasName =
                name != null &&
                !name.isBlank() &&
                !name.equalsIgnoreCase("null");

        boolean hasTime =
                time != null &&
                !time.isBlank() &&
                !time.equalsIgnoreCase("null");

        boolean hasSpecialty =
                specialty != null &&
                !specialty.isBlank() &&
                !specialty.equalsIgnoreCase("null");

        if (hasName && hasTime && hasSpecialty) {
            return doctorService.filterDoctorsByNameSpecilityandTime(
                    name,
                    specialty,
                    time
            );
        }

        if (hasName && hasTime) {
            return doctorService.filterDoctorByNameAndTime(
                    name,
                    time
            );
        }

        if (hasName && hasSpecialty) {
            return doctorService.filterDoctorByNameAndSpecility(
                    name,
                    specialty
            );
        }

        if (hasTime && hasSpecialty) {
            return doctorService.filterDoctorByTimeAndSpecility(
                    time,
                    specialty
            );
        }

        if (hasName) {
            return doctorService.findDoctorByName(name);
        }

        if (hasSpecialty) {
            return doctorService.filterDoctorBySpecility(
                    specialty
            );
        }

        if (hasTime) {
            return doctorService.filterDoctorsByTime(time);
        }

        return doctorRepository.findAll();
    }
}