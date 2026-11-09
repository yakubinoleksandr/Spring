package com.example.lab2.repository;

import com.example.lab2.model.EmploymentType;
import com.example.lab2.model.Vacancy;

import java.util.List;
import java.util.Optional;

public interface VacancyDao {

    long create(Vacancy vacancy);

    Optional<Vacancy> read(long id);

    List<Vacancy> readAll();

    boolean update(long id, Vacancy vacancy);

    boolean delete(long id);

    List<Vacancy> search(String title, EmploymentType employmentType);

    List<Vacancy> searchByKeyword(String keyword);

    int incrementApplicationsCount(long vacancyId);

    int decrementApplicationsCount(long vacancyId);
}