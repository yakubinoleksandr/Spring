package com.example.lab2.model;

import java.time.LocalDateTime;

public class Vacancy {
    private Long id;
    private String title;
    private String description;
    private String requirements;
    private Double salary;
    private LocalDateTime createdAt;

    public Vacancy() {}

    public Vacancy(Long id, String title, String description, String requirements, Double salary) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.requirements = requirements;
        this.salary = salary;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getRequirements() { return requirements; }
    public void setRequirements(String requirements) { this.requirements = requirements; }

    public Double getSalary() { return salary; }
    public void setSalary(Double salary) { this.salary = salary; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}