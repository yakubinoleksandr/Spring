package com.example.lab2.jpa.entity;

import com.example.lab2.model.EmploymentType;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "vacancies")
@NamedQuery(name = "VacancyEntity.findByMinimumSalary",
        query = "select v from VacancyEntity v where v.salary >= :minimum order by v.salary desc")
public class VacancyEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    @Column(nullable = false, length = 160)
    public String title;
    @Column(nullable = false, columnDefinition = "text")
    public String description;
    @Column(nullable = false, columnDefinition = "text")
    public String requirements;
    @Column(precision = 12, scale = 2)
    public BigDecimal salary;
    @Enumerated(EnumType.STRING)
    @Column(name = "employment_type", nullable = false, length = 30)
    public EmploymentType employmentType;
    @Column(name = "created_at", nullable = false)
    public LocalDateTime createdAt;
    @Column(name = "applications_count", nullable = false)
    public int applicationsCount;
    @OneToMany(mappedBy = "vacancy")
    public List<ApplicationEntity> applications = new ArrayList<>();
}