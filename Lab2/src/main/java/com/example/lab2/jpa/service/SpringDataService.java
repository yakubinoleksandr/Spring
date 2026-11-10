package com.example.lab2.jpa.service;

import com.example.lab2.jpa.dto.JpaDtos.*;
import com.example.lab2.jpa.entity.*;
import com.example.lab2.jpa.repository.*;
import com.example.lab2.model.EmploymentType;
import com.example.lab2.service.ResourceNotFoundException;
import com.example.lab2.service.TransactionDemoException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.StreamSupport;

@Service
public class SpringDataService {
    private final VacancyJpaRepository vacancies;
    private final CandidateJpaRepository candidates;
    private final ApplicationJpaRepository applications;

    public SpringDataService(VacancyJpaRepository vacancies, CandidateJpaRepository candidates,
                             ApplicationJpaRepository applications) {
        this.vacancies = vacancies;
        this.candidates = candidates;
        this.applications = applications;
    }

    @Transactional(readOnly = true)
    public List<VacancyView> allVacancies() {
        return StreamSupport.stream(vacancies.findAll().spliterator(), false).map(VacancyView::from).toList();
    }

    @Transactional(readOnly = true)
    public VacancyView vacancy(long id) { return VacancyView.from(requireVacancy(id)); }

    @Transactional(readOnly = true)
    public List<VacancyView> byType(EmploymentType type) {
        return vacancies.findByEmploymentTypeOrderByCreatedAtDesc(type).stream().map(VacancyView::from).toList();
    }

    @Transactional(readOnly = true)
    public List<VacancyView> byTitle(String title) {
        return vacancies.searchByTitle(title).stream().map(VacancyView::from).toList();
    }

    @Transactional(readOnly = true)
    public List<VacancyView> byMinimumSalary(BigDecimal minimum) {
        return vacancies.findByMinimumSalary(minimum).stream().map(VacancyView::from).toList();
    }

    @Transactional
    public VacancyView createVacancy(VacancyInput input) {
        VacancyEntity e = new VacancyEntity();
        copyVacancy(input, e);
        e.createdAt = LocalDateTime.now();
        return VacancyView.from(vacancies.save(e));
    }

    @Transactional
    public VacancyView updateVacancy(long id, VacancyInput input) {
        VacancyEntity e = requireVacancy(id);
        copyVacancy(input, e);
        return VacancyView.from(vacancies.save(e));
    }

    @Transactional
    public void deleteVacancy(long id) {
        VacancyEntity e = requireVacancy(id);
        // The PostgreSQL FK uses ON DELETE CASCADE; remove dependents explicitly
        // so the persistence context remains consistent.
        for (ApplicationEntity application : applications.findForVacancyWithRelations(id)) {
            applications.delete(application);
        }
        vacancies.delete(e);
    }

    @Transactional
    public CandidateView createCandidate(CandidateInput input) {
        CandidateEntity e = new CandidateEntity();
        e.fullName = input.fullName();
        e.email = input.email();
        e.skills = input.skills();
        e.createdAt = LocalDateTime.now();
        return CandidateView.from(candidates.save(e));
    }

    @Transactional(readOnly = true)
    public CandidateView candidate(long id) { return CandidateView.from(requireCandidate(id)); }

    @Transactional(readOnly = true)
    public List<CandidateView> allCandidates() {
        return StreamSupport.stream(candidates.findAll().spliterator(), false).map(CandidateView::from).toList();
    }

    @Transactional(readOnly = true)
    public CandidateView byEmail(String email) {
        return candidates.findByEmailIgnoreCase(email).map(CandidateView::from)
                .orElseThrow(() -> new ResourceNotFoundException("Кандидата не знайдено"));
    }

    @Transactional
    public ApplicationView apply(ApplicationInput input, boolean simulateFailure) {
        VacancyEntity vacancy = requireVacancy(input.vacancyId());
        CandidateEntity candidate = requireCandidate(input.candidateId());
        if (applications.existsByCandidateIdAndVacancyId(candidate.id, vacancy.id)) {
            throw new DataIntegrityViolationException("Кандидат уже подав заявку на цю вакансію");
        }
        ApplicationEntity e = new ApplicationEntity();
        e.vacancy = vacancy;
        e.candidate = candidate;
        e.coverLetter = input.coverLetter();
        e.status = "PENDING";
        e.createdAt = LocalDateTime.now();
        applications.save(e);
        vacancy.applicationsCount += 1;
        vacancies.save(vacancy);
        if (simulateFailure) {
            throw new TransactionDemoException("Тестова помилка: JPA-транзакцію відкочено");
        }
        return ApplicationView.from(e);
    }

    @Transactional(readOnly = true)
    public List<ApplicationView> applicationsForVacancy(long vacancyId) {
        requireVacancy(vacancyId);
        return applications.findForVacancyWithRelations(vacancyId).stream()
                .map(ApplicationView::from).toList();
    }

    private VacancyEntity requireVacancy(long id) {
        return vacancies.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Вакансію з ID " + id + " не знайдено"));
    }

    private CandidateEntity requireCandidate(long id) {
        return candidates.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Кандидата з ID " + id + " не знайдено"));
    }

    private void copyVacancy(VacancyInput input, VacancyEntity e) {
        e.title = input.title();
        e.description = input.description();
        e.requirements = input.requirements();
        e.salary = input.salary();
        e.employmentType = input.employmentType();
    }
}