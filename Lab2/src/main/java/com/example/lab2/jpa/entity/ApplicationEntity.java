package com.example.lab2.jpa.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "applications", uniqueConstraints = @UniqueConstraint(columnNames = {"candidate_id", "vacancy_id"}))
public class ApplicationEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vacancy_id", nullable = false)
    public VacancyEntity vacancy;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "candidate_id", nullable = false)
    public CandidateEntity candidate;
    @Column(name = "cover_letter", nullable = false, columnDefinition = "text")
    public String coverLetter;
    @Column(nullable = false, length = 30)
    public String status;
    @Column(name = "created_at", nullable = false)
    public LocalDateTime createdAt;
}