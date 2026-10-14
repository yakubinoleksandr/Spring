package com.example.lab2.controller;

import com.example.lab2.model.EmploymentType;
import com.example.lab2.model.Vacancy;
import com.example.lab2.repository.VacancyRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/vacancies")
public class VacancyController {

    private final VacancyRepository vacancyRepository;

    public VacancyController(VacancyRepository vacancyRepository) {
        this.vacancyRepository = vacancyRepository;
    }

    @GetMapping
    public String listVacancies(@RequestParam(value = "keyword", required = false) String keyword, Model model) {
        if (keyword != null && !keyword.isBlank()) {
            model.addAttribute("vacancies", vacancyRepository.searchByKeyword(keyword));
            model.addAttribute("keyword", keyword);
        } else {
            model.addAttribute("vacancies", vacancyRepository.findAll());
        }
        return "vacancies/list";
    }

    @GetMapping("/{id}")
    public String viewVacancy(@PathVariable("id") Long id, Model model) {
        Vacancy vacancy = vacancyRepository.findById(id).orElse(null);
        model.addAttribute("vacancy", vacancy);
        return "vacancies/view";
    }

    @GetMapping("/employer")
    public String employerDashboard(Model model) {
        model.addAttribute("vacancies", vacancyRepository.findAll());
        return "vacancies/employer-dashboard";
    }

    @GetMapping("/employer/new")
    public String showCreateForm(Model model) {
        model.addAttribute("vacancy", new Vacancy());
        model.addAttribute("employmentTypes", EmploymentType.values());
        return "vacancies/form";
    }

    @GetMapping("/employer/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        Vacancy vacancy = vacancyRepository.findById(id).orElse(null);
        model.addAttribute("vacancy", vacancy);
        model.addAttribute("employmentTypes", EmploymentType.values());
        return "vacancies/form";
    }

    @PostMapping("/employer/save")
    public String saveVacancy(@ModelAttribute("vacancy") Vacancy vacancy) {
        vacancyRepository.save(vacancy);
        return "redirect:/vacancies/employer";
    }

    @GetMapping("/employer/delete/{id}")
    public String deleteVacancy(@PathVariable("id") Long id) {
        vacancyRepository.deleteById(id);
        return "redirect:/vacancies/employer";
    }
}