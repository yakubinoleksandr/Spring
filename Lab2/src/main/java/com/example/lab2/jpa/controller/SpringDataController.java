package com.example.lab2.jpa.controller;

import com.example.lab2.jpa.dto.JpaDtos.*;
import com.example.lab2.jpa.service.SpringDataService;
import com.example.lab2.model.EmploymentType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.net.URI;
import java.util.List;

@RestController
@Validated
@RequestMapping("/api/v1/jpa")
@Tag(name = "Lab 6 — Spring Data JPA", description = "CrudRepository, JPQL, NamedQuery, Query Methods і транзакції")
public class SpringDataController {
    private final SpringDataService service;

    public SpringDataController(SpringDataService service) { this.service = service; }

    @GetMapping("/vacancies")
    @Operation(summary = "Усі вакансії", description = "Повертає вакансії з PostgreSQL через CrudRepository.findAll().")
    @ApiResponse(responseCode = "200", description = "Список отримано")
    public List<VacancyView> vacancies() { return service.allVacancies(); }

    @GetMapping("/vacancies/{id}")
    @Operation(summary = "Вакансія за ID", description = "Читає одну вакансію через CrudRepository.findById().")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Знайдено"),
            @ApiResponse(responseCode = "404", description = "Не знайдено")})
    public VacancyView vacancy(@Parameter(description = "Первинний ключ вакансії") @PathVariable long id) {
        return service.vacancy(id);
    }

    @GetMapping("/vacancies/by-type")
    @Operation(summary = "Пошук за типом зайнятості", description = "Spring Data JPA генерує запит за назвою методу findByEmploymentTypeOrderByCreatedAtDesc.")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Результати пошуку"),
            @ApiResponse(responseCode = "400", description = "Невідомий тип")})
    public List<VacancyView> byType(@Parameter(description = "FULL_TIME, PART_TIME, REMOTE, PROJECT") @RequestParam EmploymentType type) {
        return service.byType(type);
    }

    @GetMapping("/vacancies/by-title")
    @Operation(summary = "Пошук за назвою через JPQL", description = "Використовує явно заданий JPQL-запит у @Query, без урахування регістру.")
    @ApiResponse(responseCode = "200", description = "Знайдені вакансії або порожній масив")
    public List<VacancyView> byTitle(@Parameter(description = "Частина назви") @RequestParam String text) {
        return service.byTitle(text);
    }

    @GetMapping("/vacancies/by-min-salary")
    @Operation(summary = "Пошук за мінімальною зарплатою", description = "Виконує JPQL-запит @NamedQuery, описаний у VacancyEntity. NULL-зарплати не включаються.")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Знайдені вакансії"),
            @ApiResponse(responseCode = "400", description = "Некоректна мінімальна зарплата")})
    public List<VacancyView> bySalary(@Parameter(description = "Нижня межа зарплати, включно")
                                      @RequestParam @DecimalMin("0.0") BigDecimal minimum) {
        return service.byMinimumSalary(minimum);
    }

    @PostMapping("/vacancies")
    @Operation(summary = "Створити вакансію", description = "Створює запис через CrudRepository.save(); ID генерується PostgreSQL IDENTITY.")
    @ApiResponses({@ApiResponse(responseCode = "201", description = "Створено"),
            @ApiResponse(responseCode = "400", description = "Помилка валідації")})
    public ResponseEntity<VacancyView> createVacancy(@Valid @RequestBody VacancyInput input) {
        VacancyView created = service.createVacancy(input);
        return ResponseEntity.created(URI.create("/api/v1/jpa/vacancies/" + created.id())).body(created);
    }

    @PutMapping("/vacancies/{id}")
    @Operation(summary = "Оновити вакансію", description = "Змінює редаговані поля вакансії в транзакції; лічильник заявок і дата створення зберігаються.")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Оновлено"),
            @ApiResponse(responseCode = "400", description = "Помилка валідації"),
            @ApiResponse(responseCode = "404", description = "Не знайдено")})
    public VacancyView updateVacancy(@PathVariable long id, @Valid @RequestBody VacancyInput input) {
        return service.updateVacancy(id, input);
    }

    @DeleteMapping("/vacancies/{id}")
    @Operation(summary = "Видалити вакансію", description = "Видаляє вакансію разом із пов'язаними заявками у транзакції.")
    @ApiResponses({@ApiResponse(responseCode = "204", description = "Видалено"),
            @ApiResponse(responseCode = "404", description = "Не знайдено")})
    public ResponseEntity<Void> deleteVacancy(@PathVariable long id) {
        service.deleteVacancy(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/candidates")
    @Operation(summary = "Список кандидатів", description = "Викликає CrudRepository.findAll() для таблиці candidates.")
    @ApiResponse(responseCode = "200", description = "Список кандидатів")
    public List<CandidateView> candidates() { return service.allCandidates(); }

    @GetMapping("/candidates/{id}")
    @Operation(summary = "Кандидат за ID", description = "Читає кандидата за первинним ключем.")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Знайдено"),
            @ApiResponse(responseCode = "404", description = "Не знайдено")})
    public CandidateView candidate(@PathVariable long id) { return service.candidate(id); }

    @GetMapping("/candidates/by-email")
    @Operation(summary = "Пошук кандидата за email", description = "Використовує derived query findByEmailIgnoreCase.")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Знайдено"),
            @ApiResponse(responseCode = "404", description = "Не знайдено")})
    public CandidateView byEmail(@Parameter(description = "Email кандидата") @RequestParam String email) {
        return service.byEmail(email);
    }

    @PostMapping("/candidates")
    @Operation(summary = "Створити кандидата", description = "Додає кандидата, перевіряючи обов'язкові поля й унікальність email у PostgreSQL.")
    @ApiResponses({@ApiResponse(responseCode = "201", description = "Створено"),
            @ApiResponse(responseCode = "400", description = "Помилка валідації"),
            @ApiResponse(responseCode = "409", description = "Email уже існує")})
    public ResponseEntity<CandidateView> createCandidate(@Valid @RequestBody CandidateInput input) {
        CandidateView created = service.createCandidate(input);
        return ResponseEntity.created(URI.create("/api/v1/jpa/candidates/" + created.id())).body(created);
    }

    @GetMapping("/vacancies/{id}/applications")
    @Operation(summary = "Заявки на вакансію", description = "JPQL JOIN FETCH підвантажує кандидата й вакансію разом, уникаючи проблеми N+1.")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Список заявок"),
            @ApiResponse(responseCode = "404", description = "Вакансію не знайдено")})
    public List<ApplicationView> applications(@PathVariable long id) { return service.applicationsForVacancy(id); }

    @PostMapping("/applications")
    @Operation(summary = "Подати заявку транзакційно", description = "У межах однієї @Transactional операції зберігає заявку та збільшує applications_count. simulateFailure=true генерує помилку після змін і перевіряє rollback обох змін.")
    @ApiResponses({@ApiResponse(responseCode = "201", description = "Заявку створено"),
            @ApiResponse(responseCode = "400", description = "Некоректний запит"),
            @ApiResponse(responseCode = "404", description = "Вакансію чи кандидата не знайдено"),
            @ApiResponse(responseCode = "409", description = "Повторна заявка"),
            @ApiResponse(responseCode = "500", description = "Демонстраційна помилка — транзакцію відкочено")})
    public ResponseEntity<ApplicationView> apply(@Valid @RequestBody ApplicationInput input,
                                                 @Parameter(description = "true для перевірки відкату транзакції")
                                                 @RequestParam(defaultValue = "false") boolean simulateFailure) {
        ApplicationView created = service.apply(input, simulateFailure);
        return ResponseEntity.created(URI.create("/api/v1/applications/" + created.id())).body(created);
    }
}