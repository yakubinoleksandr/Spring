package com.example.lab2.jpa.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "candidates")
public class CandidateEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    @Column(name = "full_name", nullable = false, length = 160)
    public String fullName;
    @Column(nullable = false, unique = true, length = 190)
    public String email;
    @Column(nullable = false, columnDefinition = "text")
    public String skills;
    @Column(name = "created_at", nullable = false)
    public LocalDateTime createdAt;
    @OneToMany(mappedBy = "candidate")
    public List<ApplicationEntity> applications = new ArrayList<>();
}