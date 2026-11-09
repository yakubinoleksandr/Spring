package com.example.lab2.service;

import com.example.lab2.model.EmploymentType;
import com.example.lab2.model.Vacancy;
import com.example.lab2.repository.VacancyDao;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class VacancyJdbcServiceImpl implements VacancyJdbcService {

    private final VacancyDao vacancyDao;

    public VacancyJdbcServiceImpl(VacancyDao vacancyDao) {
        this.vacancyDao = vacancyDao;
    }

    @Override
    public List<Vacancy> findAll(
            String title,
            EmploymentType employmentType
    ) {

        if ((title == null || title.isBlank())
                && employmentType == null) {

            return vacancyDao.readAll();
        }

        return vacancyDao.search(title, employmentType);
    }

    @Override
    public Vacancy findById(long id) {

        return vacancyDao.read(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Вакансію з ID " + id + " не знайдено"
                        )
                );
    }

    @Override
    @Transactional
    public long create(Vacancy vacancy) {
        return vacancyDao.create(vacancy);
    }

    @Override
    @Transactional
    public Vacancy update(long id, Vacancy vacancy) {

        if (!vacancyDao.update(id, vacancy)) {
            throw new ResourceNotFoundException(
                    "Вакансію з ID " + id + " не знайдено"
            );
        }

        return findById(id);
    }

    @Override
    @Transactional
    public void delete(long id) {

        if (!vacancyDao.delete(id)) {
            throw new ResourceNotFoundException(
                    "Вакансію з ID " + id + " не знайдено"
            );
        }
    }
}