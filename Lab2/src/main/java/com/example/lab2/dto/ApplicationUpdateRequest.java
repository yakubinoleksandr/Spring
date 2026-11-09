package com.example.lab2.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ApplicationUpdateRequest(

        @Size(max = 4000)
        String coverLetter,

        @NotBlank
        @Pattern(
                regexp = "PENDING|REVIEWED|ACCEPTED|REJECTED",
                message = "Недопустимий статус заявки"
        )
        String status

) {
}