package com.example.lab2.controller;

import com.example.lab2.model.Candidate;
import com.example.lab2.service.CandidateService;

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
@RequestMapping("/api/v1/candidates")
@Tag(
        name = "Candidates",
        description = "CRUD операції над кандидатами через JdbcClient"
)
public class CandidateApiController {

    private final CandidateService candidateService;

    public CandidateApiController(CandidateService candidateService) {
        this.candidateService = candidateService;
    }

    @GetMapping
    @Operation(
            summary = "Отримати кандидатів",
            description = "Повертає список кандидатів або шукає за ім'ям, email чи навичками."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Список кандидатів отримано"
            )
    })
    public List<Candidate> findAll(

            @Parameter(description = "Текст для пошуку кандидата")
            @RequestParam(required = false)
            String q

    ) {
        return candidateService.findAll(q);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Отримати кандидата за ID",
            description = "Повертає кандидата за його первинним ключем."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Кандидата знайдено"),
            @ApiResponse(responseCode = "404", description = "Кандидата не знайдено")
    })
    public Candidate findById(@PathVariable long id) {
        return candidateService.findById(id);
    }

    @PostMapping
    @Operation(
            summary = "Створити кандидата",
            description = "Додає нового кандидата. ID генерується PostgreSQL."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Кандидата створено"),
            @ApiResponse(responseCode = "400", description = "Некоректні дані"),
            @ApiResponse(responseCode = "409", description = "Email уже використовується")
    })
    public ResponseEntity<Candidate> create(
            @Valid @RequestBody Candidate candidate
    ) {

        long id = candidateService.create(candidate);

        return ResponseEntity
                .created(URI.create("/api/v1/candidates/" + id))
                .body(candidateService.findById(id));
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Оновити кандидата",
            description = "Повністю оновлює ім'я, email та навички кандидата."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Кандидата оновлено"),
            @ApiResponse(responseCode = "404", description = "Кандидата не знайдено"),
            @ApiResponse(responseCode = "409", description = "Email уже використовується")
    })
    public Candidate update(
            @PathVariable long id,
            @Valid @RequestBody Candidate candidate
    ) {
        return candidateService.update(id, candidate);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Видалити кандидата",
            description = "Видаляє кандидата та його заявки."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Кандидата видалено"),
            @ApiResponse(responseCode = "404", description = "Кандидата не знайдено")
    })
    public ResponseEntity<Void> delete(@PathVariable long id) {

        candidateService.delete(id);

        return ResponseEntity.noContent().build();
    }
}