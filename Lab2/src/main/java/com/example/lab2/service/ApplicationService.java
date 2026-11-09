package com.example.lab2.service;

import com.example.lab2.dto.ApplicationRequest;
import com.example.lab2.dto.ApplicationUpdateRequest;
import com.example.lab2.model.JobApplication;
import com.example.lab2.repository.ApplicationDao;
import com.example.lab2.repository.CandidateDao;
import com.example.lab2.repository.VacancyDao;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ApplicationService {

    private final ApplicationDao applicationDao;
    private final CandidateDao candidateDao;
    private final VacancyDao vacancyDao;

    public ApplicationService(
            ApplicationDao applicationDao,
            CandidateDao candidateDao,
            VacancyDao vacancyDao
    ) {
        this.applicationDao = applicationDao;
        this.candidateDao = candidateDao;
        this.vacancyDao = vacancyDao;
    }

    @Transactional(rollbackFor = Exception.class)
    public JobApplication apply(
            ApplicationRequest request,
            boolean simulateFailure
    ) {

        if (vacancyDao.read(request.vacancyId()).isEmpty()) {
            throw new ResourceNotFoundException(
                    "Вакансію з ID " + request.vacancyId() + " не знайдено"
            );
        }

        if (candidateDao.read(request.candidateId()).isEmpty()) {
            throw new ResourceNotFoundException(
                    "Кандидата з ID " + request.candidateId() + " не знайдено"
            );
        }

        JobApplication created = applicationDao.create(
                request.candidateId(),
                request.vacancyId(),
                request.coverLetter()
        );

        int changed = vacancyDao.incrementApplicationsCount(
                request.vacancyId()
        );

        if (changed != 1) {
            throw new IllegalStateException(
                    "Не вдалося оновити лічильник заявок вакансії"
            );
        }

        if (simulateFailure) {
            throw new TransactionDemoException(
                    "Тестова помилка після INSERT та UPDATE. " +
                            "Транзакцію має бути відкочено."
            );
        }

        return created;
    }

    public List<JobApplication> findAll(
            Long candidateId,
            Long vacancyId,
            String status
    ) {

        if (candidateId == null
                && vacancyId == null
                && (status == null || status.isBlank())) {

            return applicationDao.readAll();
        }

        return applicationDao.search(
                candidateId,
                vacancyId,
                status
        );
    }

    public JobApplication findById(long id) {

        return applicationDao.read(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Заявку з ID " + id + " не знайдено"
                        )
                );
    }

    @Transactional
    public JobApplication update(
            long id,
            ApplicationUpdateRequest request
    ) {

        if (!applicationDao.update(
                id,
                request.coverLetter(),
                request.status()
        )) {
            throw new ResourceNotFoundException(
                    "Заявку з ID " + id + " не знайдено"
            );
        }

        return findById(id);
    }

    @Transactional
    public void delete(long id) {

        JobApplication existing = findById(id);

        if (!applicationDao.delete(id)) {
            throw new ResourceNotFoundException(
                    "Заявку з ID " + id + " не знайдено"
            );
        }

        int changed = vacancyDao.decrementApplicationsCount(
                existing.vacancyId()
        );

        if (changed != 1) {
            throw new IllegalStateException(
                    "Не вдалося оновити лічильник заявок вакансії"
            );
        }
    }
}