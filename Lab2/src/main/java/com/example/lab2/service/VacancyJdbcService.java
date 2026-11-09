package com.example.lab2.service;

import com.example.lab2.model.EmploymentType;
import com.example.lab2.model.Vacancy;

import java.util.List;

public interface VacancyJdbcService {

    List<Vacancy> findAll(
            String title,
            EmploymentType employmentType
    );

    Vacancy findById(long id);

    long create(Vacancy vacancy);

    Vacancy update(long id, Vacancy vacancy);

    void delete(long id);
}