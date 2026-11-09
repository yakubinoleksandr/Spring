package com.example.lab2.repository;

import com.example.lab2.model.Candidate;

import java.util.List;
import java.util.Optional;

public interface CandidateDao {

    long create(Candidate candidate);

    Optional<Candidate> read(long id);

    List<Candidate> readAll();

    boolean update(long id, Candidate candidate);

    boolean delete(long id);

    List<Candidate> search(String query);
}