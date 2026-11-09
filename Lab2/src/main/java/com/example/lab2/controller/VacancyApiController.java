package com.example.lab2.controller;

import com.example.lab2.model.EmploymentType;
import com.example.lab2.model.Vacancy;
import com.example.lab2.service.VacancyJdbcService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import jakarta.validation.Valid;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/vacancies")
@Tag(
        name = "Vacancies",
        description = "CRUD операції над вакансіями через JdbcTemplate"
)
public class VacancyApiController {

    private final VacancyJdbcService vacancyService;

    public VacancyApiController(VacancyJdbcService vacancyService) {
        this.vacancyService = vacancyService;
    }

    @GetMapping
    @Operation(
            summary = "Отримати список вакансій",
            description = """
                    Повертає всі вакансії.
                    Можна виконати пошук за фрагментом назви
                    та типом зайнятості.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Список вакансій отримано"
            )
    })
    public List<Vacancy> findAll(

            @Parameter(
                    description = "Фрагмент назви вакансії"
            )
            @RequestParam(required = false)
            String title,

            @Parameter(
                    description = "FULL_TIME, PART_TIME, REMOTE або PROJECT"
            )
            @RequestParam(required = false)
            EmploymentType employmentType

    ) {
        return vacancyService.findAll(title, employmentType);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Отримати вакансію за ID",
            description = "Повертає дані конкретної вакансії за її первинним ключем."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Вакансію знайдено"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Вакансію не знайдено"
            )
    })
    public Vacancy findById(@PathVariable long id) {
        return vacancyService.findById(id);
    }

    @PostMapping
    @Operation(
            summary = "Створити вакансію",
            description = "Додає нову вакансію до PostgreSQL. ID генерується автоматично."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Вакансію створено"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Некоректні дані"
            )
    })
    public ResponseEntity<Vacancy> create(
            @Valid @RequestBody Vacancy vacancy
    ) {

        long id = vacancyService.create(vacancy);

        return ResponseEntity
                .created(URI.create("/api/v1/vacancies/" + id))
                .body(vacancyService.findById(id));
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Оновити вакансію",
            description = "Повністю оновлює редаговані поля вакансії за вказаним ID."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Вакансію оновлено"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Некоректні дані"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Вакансію не знайдено"
            )
    })
    public Vacancy update(
            @PathVariable long id,
            @Valid @RequestBody Vacancy vacancy
    ) {
        return vacancyService.update(id, vacancy);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Видалити вакансію",
            description = "Видаляє вакансію. Пов'язані заявки видаляються каскадно."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Вакансію видалено"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Вакансію не знайдено"
            )
    })
    public ResponseEntity<Void> delete(@PathVariable long id) {

        vacancyService.delete(id);

        return ResponseEntity.noContent().build();
    }
}