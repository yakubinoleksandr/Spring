
package com.example.lab2.controller;

import com.example.lab2.model.EmploymentType;
import com.example.lab2.model.Vacancy;
import com.example.lab2.repository.VacancyRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonpatch.JsonPatch;


import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/vacancies")
@Tag(
        name = "Vacancies",
        description = "REST API для керування вакансіями"
)
public class VacancyRestController {

    private final VacancyRepository repository;

    public VacancyRestController(VacancyRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    @Operation(
            summary = "Отримати вакансії",
            description = "Повертає список вакансій з фільтрацією та пагінацією."
    )
    public ResponseEntity<Map<String, Object>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) EmploymentType employmentType,
            @RequestParam(required = false) Double minSalary,
            @RequestParam(required = false) Double maxSalary,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        if (page < 0 || size < 1 || size > 100
                || (minSalary != null && minSalary < 0)
                || (maxSalary != null && maxSalary < 0)
                || (minSalary != null && maxSalary != null
                && minSalary > maxSalary)) {
            return ResponseEntity.badRequest().build();
        }

        List<Vacancy> filtered = repository.findAll().stream()
                .filter(v -> keyword == null || keyword.isBlank()
                        || contains(v.getTitle(), keyword)
                        || contains(v.getDescription(), keyword)
                        || contains(v.getRequirements(), keyword))
                .filter(v -> employmentType == null
                        || employmentType == v.getEmploymentType())
                .filter(v -> minSalary == null
                        || (v.getSalary() != null
                        && v.getSalary() >= minSalary))
                .filter(v -> maxSalary == null
                        || (v.getSalary() != null
                        && v.getSalary() <= maxSalary))
                .sorted(Comparator.comparing(Vacancy::getId))
                .collect(Collectors.toList());

        int from = Math.min(page * size, filtered.size());
        int to = Math.min(from + size, filtered.size());

        Map<String, Object> body = new LinkedHashMap<>();

        body.put("content", filtered.subList(from, to));
        body.put("page", page);
        body.put("size", size);
        body.put("totalElements", filtered.size());
        body.put(
                "totalPages",
                (int) Math.ceil((double) filtered.size() / size)
        );

        return ResponseEntity.ok(body);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Отримати вакансію за ID",
            description = "Повертає одну вакансію за її ідентифікатором."
    )
    public ResponseEntity<Vacancy> getById(
            @PathVariable Long id) {

        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(
            summary = "Створити вакансію",
            description = "Створює нову вакансію."
    )
    public ResponseEntity<Vacancy> create(
            @RequestBody Vacancy vacancy) {

        if (!valid(vacancy)) {
            return ResponseEntity.badRequest().build();
        }

        vacancy.setId(null);

        if (vacancy.getCreatedAt() == null) {
            vacancy.setCreatedAt(
                    java.time.LocalDateTime.now()
            );
        }

        Vacancy saved = repository.save(vacancy);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(saved);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Повністю оновити вакансію",
            description = "Замінює дані вакансії за ID."
    )
    public ResponseEntity<Vacancy> update(
            @PathVariable Long id,
            @RequestBody Vacancy input) {

        if (repository.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        if (!valid(input)) {
            return ResponseEntity.badRequest().build();
        }

        input.setId(id);

        Vacancy saved = repository.save(input);

        return ResponseEntity.ok(saved);
    }

    @PatchMapping(
            value = "/{id}",
            consumes = {
                    "application/merge-patch+json",
                    "application/json"
            }
    )
    @Operation(
            summary = "Частково оновити вакансію",
            description = "Змінює лише передані поля вакансії."
    )
    public ResponseEntity<Vacancy> patch(
            @PathVariable Long id,
            @RequestBody Map<String, Object> patch) {

        Optional<Vacancy> optional =
                repository.findById(id);

        if (optional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Vacancy v = optional.get();

        try {
            if (patch.containsKey("title")) {
                v.setTitle((String) patch.get("title"));
            }

            if (patch.containsKey("description")) {
                v.setDescription(
                        (String) patch.get("description")
                );
            }

            if (patch.containsKey("requirements")) {
                v.setRequirements(
                        (String) patch.get("requirements")
                );
            }

            if (patch.containsKey("salary")) {
                v.setSalary(
                        patch.get("salary") == null
                                ? null
                                : ((Number) patch.get("salary"))
                                .doubleValue()
                );
            }

            if (patch.containsKey("employmentType")) {
                v.setEmploymentType(
                        patch.get("employmentType") == null
                                ? null
                                : EmploymentType.valueOf(
                                (String) patch.get("employmentType")
                        )
                );
            }

            if (!valid(v)) {
                return ResponseEntity.badRequest().build();
            }

            return ResponseEntity.ok(repository.save(v));

        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Видалити вакансію",
            description = "Видаляє вакансію за її ID."
    )
    public ResponseEntity<Void> delete(
            @PathVariable Long id) {

        if (repository.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        repository.deleteById(id);

        return ResponseEntity.noContent().build();
    }

    private boolean valid(Vacancy v) {
        return v != null
                && v.getTitle() != null
                && !v.getTitle().isBlank()
                && v.getDescription() != null
                && !v.getDescription().isBlank()
                && v.getRequirements() != null
                && !v.getRequirements().isBlank()
                && v.getEmploymentType() != null
                && (v.getSalary() == null
                || v.getSalary() >= 0);
    }

    private boolean contains(
            String value,
            String keyword) {

        return value != null
                && value.toLowerCase(Locale.ROOT)
                .contains(
                        keyword.toLowerCase(Locale.ROOT)
                );
    }


    @PatchMapping(
            value = "/{id}",
            consumes = "application/json-patch+json"
    )
    @Operation(
            summary = "JSON Patch вакансії",
            description = "Часткове оновлення вакансії відповідно до RFC 6902."
    )
    public ResponseEntity<Vacancy> jsonPatch(
            @PathVariable Long id,
            @RequestBody JsonPatch patch) {

        Optional<Vacancy> optional = repository.findById(id);

        if (optional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        try {
            ObjectMapper mapper = new ObjectMapper();

            Vacancy vacancy = optional.get();

            JsonNode original = mapper.valueToTree(vacancy);

            JsonNode patched = patch.apply(original);

            Vacancy updated = mapper.treeToValue(
                    patched,
                    Vacancy.class
            );

            updated.setId(id);

            if (!valid(updated)) {
                return ResponseEntity.badRequest().build();
            }

            return ResponseEntity.ok(repository.save(updated));

        } catch (Exception ex) {
            return ResponseEntity.badRequest().build();
        }
    }

}
