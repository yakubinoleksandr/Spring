package com.example.lab2.model;

import java.time.LocalDateTime;

public record JobApplication(
        Long id,
        Long vacancyId,
        String vacancyTitle,
        Long candidateId,
        String candidateName,
        String coverLetter,
        String status,
        LocalDateTime createdAt
) {
}