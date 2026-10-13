package com.example.lab2.repository;

import com.example.lab2.model.Vacancy;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class VacancyRepository {
    private final Map<Long, Vacancy> storage = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(0);

    public VacancyRepository() {
        save(new Vacancy(null, "Java Developer", "Розробка Spring Boot сервісів", "Java 17, Spring, PostgreSQL", 2500.0));
        save(new Vacancy(null, "Frontend Engineer", "Створення UI на React", "React, TypeScript, CSS", 2200.0));
        save(new Vacancy(null, "QA Automation", "Автоматизація тестування", "Java, Selenium, JUnit", 1800.0));
    }

    public List<Vacancy> findAll() {
        return new ArrayList<>(storage.values());
    }

    public Optional<Vacancy> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    public Vacancy save(Vacancy vacancy) {
        if (vacancy.getId() == null) {
            vacancy.setId(idGenerator.incrementAndGet());
        }
        storage.put(vacancy.getId(), vacancy);
        return vacancy;
    }

    public void deleteById(Long id) {
        storage.remove(id);
    }

    public List<Vacancy> searchByKeyword(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return findAll();
        }
        String lowerKeyword = keyword.toLowerCase();
        return storage.values().stream()
                .filter(v -> v.getTitle().toLowerCase().contains(lowerKeyword)
                        || v.getDescription().toLowerCase().contains(lowerKeyword)
                        || v.getRequirements().toLowerCase().contains(lowerKeyword))
                .collect(Collectors.toList());
    }
}
