package com.example.lab2.controller;

import com.example.lab2.dto.ApplicationRequest;
import com.example.lab2.dto.ApplicationUpdateRequest;
import com.example.lab2.model.JobApplication;
import com.example.lab2.service.ApplicationService;

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
@RequestMapping("/api/v1/applications")
@Tag(
        name = "Applications",
        description = "Операції над заявками та демонстрація транзакцій"
)
public class ApplicationApiController {

    private final ApplicationService applicationService;

    public ApplicationApiController(
            ApplicationService applicationService
    ) {
        this.applicationService = applicationService;
    }

    @GetMapping
    @Operation(
            summary = "Отримати заявки",
            description = """
                    Повертає всі заявки або виконує фільтрацію
                    за ID кандидата, ID вакансії та статусом.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Заявки отримано")
    })
    public List<JobApplication> findAll(

            @Parameter(description = "ID кандидата")
            @RequestParam(required = false)
            Long candidateId,

            @Parameter(description = "ID вакансії")
            @RequestParam(required = false)
            Long vacancyId,

            @Parameter(
                    description = "PENDING, REVIEWED, ACCEPTED або REJECTED"
            )
            @RequestParam(required = false)
            String status

    ) {
        return applicationService.findAll(
                candidateId,
                vacancyId,
                status
        );
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Отримати заявку за ID",
            description = "Повертає заявку з назвою вакансії та ім'ям кандидата."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Заявку знайдено"),
            @ApiResponse(responseCode = "404", description = "Заявку не знайдено")
    })
    public JobApplication findById(@PathVariable long id) {
        return applicationService.findById(id);
    }

    @PostMapping
    @Operation(
            summary = "Подати заявку на вакансію",
            description = """
                    В одній транзакції створює заявку та збільшує
                    лічильник applications_count відповідної вакансії.
                    Параметр simulateFailure=true навмисно генерує
                    помилку після SQL-змін для перевірки rollback.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Заявку створено"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Некоректні дані"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Вакансію або кандидата не знайдено"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Повторна заявка на ту саму вакансію"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Демонстраційна помилка, транзакцію відкочено"
            )
    })
    public ResponseEntity<JobApplication> apply(

            @Valid @RequestBody
            ApplicationRequest request,

            @Parameter(
                    description = "true — примусово викликати помилку після SQL-змін"
            )
            @RequestParam(defaultValue = "false")
            boolean simulateFailure

    ) {

        JobApplication created = applicationService.apply(
                request,
                simulateFailure
        );

        return ResponseEntity
                .created(URI.create(
                        "/api/v1/applications/" + created.id()
                ))
                .body(created);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Оновити заявку",
            description = "Змінює супровідний лист і статус заявки."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Заявку оновлено"),
            @ApiResponse(responseCode = "400", description = "Некоректні дані"),
            @ApiResponse(responseCode = "404", description = "Заявку не знайдено")
    })
    public JobApplication update(
            @PathVariable long id,
            @Valid @RequestBody ApplicationUpdateRequest request
    ) {

        return applicationService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Видалити заявку",
            description = """
                    Видаляє заявку та зменшує лічильник заявок
                    відповідної вакансії в одній транзакції.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Заявку видалено"),
            @ApiResponse(responseCode = "404", description = "Заявку не знайдено")
    })
    public ResponseEntity<Void> delete(@PathVariable long id) {

        applicationService.delete(id);

        return ResponseEntity.noContent().build();
    }
}