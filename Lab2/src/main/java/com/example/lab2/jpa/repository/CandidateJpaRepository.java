package com.example.lab2.jpa.repository;

import com.example.lab2.jpa.entity.CandidateEntity;
import org.springframework.data.repository.CrudRepository;
import java.util.Optional;

public interface CandidateJpaRepository extends CrudRepository<CandidateEntity, Long> {
    Optional<CandidateEntity> findByEmailIgnoreCase(String email);
}