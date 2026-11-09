package com.example.lab2.model;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public class Vacancy {

    private Long id;

    @NotBlank(message = "Назва вакансії не може бути порожньою")
    @Size(max = 160, message = "Назва не може бути довшою за 160 символів")
    private String title;

    @NotBlank(message = "Опис вакансії обов'язковий")
    private String description;

    @NotBlank(message = "Вимоги до кандидата обов'язкові")
    private String requirements;

    @DecimalMin(value = "0.0", message = "Зарплата не може бути від'ємною")
    private Double salary;

    @NotNull(message = "Тип зайнятості обов'язковий")
    private EmploymentType employmentType;

    private LocalDateTime createdAt;

    private int applicationsCount;

    public Vacancy() {
    }

    public Vacancy(
            Long id,
            String title,
            String description,
            String requirements,
            Double salary,
            EmploymentType employmentType
    ) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.requirements = requirements;
        this.salary = salary;
        this.employmentType = employmentType;
        this.createdAt = LocalDateTime.now();
    }

    public Vacancy(
            Long id,
            String title,
            String description,
            String requirements,
            Double salary,
            EmploymentType employmentType,
            LocalDateTime createdAt,
            int applicationsCount
    ) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.requirements = requirements;
        this.salary = salary;
        this.employmentType = employmentType;
        this.createdAt = createdAt;
        this.applicationsCount = applicationsCount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRequirements() {
        return requirements;
    }

    public void setRequirements(String requirements) {
        this.requirements = requirements;
    }

    public Double getSalary() {
        return salary;
    }

    public void setSalary(Double salary) {
        this.salary = salary;
    }

    public EmploymentType getEmploymentType() {
        return employmentType;
    }

    public void setEmploymentType(EmploymentType employmentType) {
        this.employmentType = employmentType;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public int getApplicationsCount() {
        return applicationsCount;
    }

    public void setApplicationsCount(int applicationsCount) {
        this.applicationsCount = applicationsCount;
    }
}