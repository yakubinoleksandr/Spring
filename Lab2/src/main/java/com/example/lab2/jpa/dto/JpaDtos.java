package com.example.lab2.jpa.dto;

import com.example.lab2.jpa.entity.*;
import com.example.lab2.model.EmploymentType;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public final class JpaDtos {
    private JpaDtos() {}

    public record VacancyInput(
            @NotBlank @Size(max = 160) String title,
            @NotBlank String description,
            @NotBlank String requirements,
            @DecimalMin("0.0") BigDecimal salary,
            @NotNull EmploymentType employmentType) {}

    public record VacancyView(Long id, String title, String description,
                              String requirements, BigDecimal salary, EmploymentType employmentType,
                              LocalDateTime createdAt, int applicationsCount) {
        public static VacancyView from(VacancyEntity e) {
            return new VacancyView(e.id, e.title, e.description, e.requirements,
                    e.salary, e.employmentType, e.createdAt, e.applicationsCount);
        }
    }

    public record CandidateInput(@NotBlank @Size(max = 160) String fullName,
                                 @NotBlank @Email String email, @NotBlank String skills) {}

    public record CandidateView(Long id, String fullName, String email, String skills,
                                LocalDateTime createdAt) {
        public static CandidateView from(CandidateEntity e) {
            return new CandidateView(e.id, e.fullName, e.email, e.skills, e.createdAt);
        }
    }

    public record ApplicationInput(@NotNull Long candidateId, @NotNull Long vacancyId,
                                   @NotNull String coverLetter) {}

    public record ApplicationView(Long id, Long vacancyId, String vacancyTitle,
                                  Long candidateId, String candidateName, String coverLetter,
                                  String status, LocalDateTime createdAt) {
        public static ApplicationView from(ApplicationEntity e) {
            return new ApplicationView(e.id, e.vacancy.id, e.vacancy.title,
                    e.candidate.id, e.candidate.fullName, e.coverLetter, e.status, e.createdAt);
        }
    }
}