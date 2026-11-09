package com.example.lab2.repository;

import com.example.lab2.model.JobApplication;

import java.util.List;
import java.util.Optional;

public interface ApplicationDao {

    JobApplication create(
            long candidateId,
            long vacancyId,
            String coverLetter
    );

    Optional<JobApplication> read(long id);

    List<JobApplication> readAll();

    List<JobApplication> search(
            Long candidateId,
            Long vacancyId,
            String status
    );

    boolean update(
            long id,
            String coverLetter,
            String status
    );

    boolean delete(long id);
}