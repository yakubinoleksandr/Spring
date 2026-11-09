package com.example.lab2.repository;

import com.example.lab2.model.Vacancy;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class VacancyRepository {

    private final VacancyDao vacancyDao;

    public VacancyRepository(VacancyDao vacancyDao) {
        this.vacancyDao = vacancyDao;
    }

    public List<Vacancy> findAll() {
        return vacancyDao.readAll();
    }

    public Optional<Vacancy> findById(Long id) {
        return vacancyDao.read(id);
    }

    public Vacancy save(Vacancy vacancy) {

        if (vacancy.getId() == null) {

            long generatedId = vacancyDao.create(vacancy);

            vacancy.setId(generatedId);

        } else {

            vacancyDao.update(vacancy.getId(), vacancy);
        }

        return vacancy;
    }

    public void deleteById(Long id) {
        vacancyDao.delete(id);
    }

    public List<Vacancy> searchByKeyword(String keyword) {
        return vacancyDao.searchByKeyword(keyword);
    }
}