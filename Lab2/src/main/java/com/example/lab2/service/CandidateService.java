package com.example.lab2.service;

import com.example.lab2.model.Candidate;

import java.util.List;

public interface CandidateService {

    List<Candidate> findAll(String query);

    Candidate findById(long id);

    long create(Candidate candidate);

    Candidate update(long id, Candidate candidate);

    void delete(long id);
}