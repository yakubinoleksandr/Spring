package com.example.lab2.service;

import com.example.lab2.model.Candidate;
import com.example.lab2.repository.CandidateDao;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CandidateServiceImpl implements CandidateService {

    private final CandidateDao candidateDao;

    public CandidateServiceImpl(CandidateDao candidateDao) {
        this.candidateDao = candidateDao;
    }

    @Override
    public List<Candidate> findAll(String query) {

        if (query == null || query.isBlank()) {
            return candidateDao.readAll();
        }

        return candidateDao.search(query);
    }

    @Override
    public Candidate findById(long id) {

        return candidateDao.read(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Кандидата з ID " + id + " не знайдено"
                        )
                );
    }

    @Override
    @Transactional
    public long create(Candidate candidate) {
        return candidateDao.create(candidate);
    }

    @Override
    @Transactional
    public Candidate update(long id, Candidate candidate) {

        if (!candidateDao.update(id, candidate)) {
            throw new ResourceNotFoundException(
                    "Кандидата з ID " + id + " не знайдено"
            );
        }

        return findById(id);
    }

    @Override
    @Transactional
    public void delete(long id) {

        if (!candidateDao.delete(id)) {
            throw new ResourceNotFoundException(
                    "Кандидата з ID " + id + " не знайдено"
            );
        }
    }
}