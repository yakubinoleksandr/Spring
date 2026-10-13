package com.example.lab2.service;

import com.example.lab2.model.Vacancy;
import com.example.lab2.repository.VacancyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class VacancyService {

    @Autowired
    private VacancyRepository vacancyRepository;

    @Autowired
    private DateTimeFormatter dateFormatter;

    private SearchSessionCounter searchSessionCounter;

    @Autowired
    public void setSearchSessionCounter(SearchSessionCounter searchSessionCounter) {
        this.searchSessionCounter = searchSessionCounter;
    }

    public List<Vacancy> getAllVacancies() {
        return vacancyRepository.findAll();
    }

    public Vacancy getVacancyById(Long id) {
        return vacancyRepository.findById(id).orElse(null);
    }

    public void saveVacancy(Vacancy vacancy) {
        vacancyRepository.save(vacancy);
    }

    public void deleteVacancy(Long id) {
        vacancyRepository.deleteById(id);
    }

    public List<Vacancy> search(String keyword) {
        int currentCount = searchSessionCounter.incrementAndGet();
        System.out.println("Лічильник виконання пошуків в поточному об'єкті: " + currentCount);
        return vacancyRepository.searchByKeyword(keyword);
    }
}
