package com.example.lab2.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public class Candidate {

    private Long id;

    @NotBlank(message = "Ім'я кандидата обов'язкове")
    @Size(max = 160)
    private String fullName;

    @NotBlank(message = "Email обов'язковий")
    @Email(message = "Некоректний формат email")
    @Size(max = 190)
    private String email;

    @NotBlank(message = "Необхідно вказати навички")
    private String skills;

    private LocalDateTime createdAt;

    public Candidate() {
    }

    public Candidate(
            Long id,
            String fullName,
            String email,
            String skills,
            LocalDateTime createdAt
    ) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.skills = skills;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}