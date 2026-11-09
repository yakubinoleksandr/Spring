package com.example.lab2.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record ApplicationRequest(

        @NotNull
        @Positive
        Long candidateId,

        @NotNull
        @Positive
        Long vacancyId,

        @Size(max = 4000)
        String coverLetter

) {
}