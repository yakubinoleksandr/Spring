package com.example.lab2.controller;

import com.example.lab2.model.Vacancy;
import com.example.lab2.service.VacancyService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/vacancies")
public class VacancyController {

    private final VacancyService vacancyService;

    public VacancyController(VacancyService vacancyService) {
        this.vacancyService = vacancyService;
    }

    @GetMapping
    public String listVacancies(@RequestParam(value = "keyword", required = false) String keyword, Model model) {
        if (keyword != null && !keyword.isBlank()) {
            model.addAttribute("vacancies", vacancyService.search(keyword));
            model.addAttribute("keyword", keyword);
        } else {
            model.addAttribute("vacancies", vacancyService.getAllVacancies());
        }
        return "vacancies/list";
    }

    @GetMapping("/{id}")
    public String viewVacancy(@PathVariable("id") Long id, Model model) {
        Vacancy vacancy = vacancyService.getVacancyById(id);
        model.addAttribute("vacancy", vacancy);
        return "vacancies/view";
    }

    @GetMapping("/employer")
    public String employerDashboard(Model model) {
        model.addAttribute("vacancies", vacancyService.getAllVacancies());
        return "vacancies/employer-dashboard";
    }

    @GetMapping("/employer/new")
    public String showCreateForm(Model model) {
        model.addAttribute("vacancy", new Vacancy());
        return "vacancies/form";
    }

    @GetMapping("/employer/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        Vacancy vacancy = vacancyService.getVacancyById(id);
        model.addAttribute("vacancy", vacancy);
        return "vacancies/form";
    }

    @PostMapping("/employer/save")
    public String saveVacancy(@ModelAttribute("vacancy") Vacancy vacancy) {
        vacancyService.saveVacancy(vacancy);
        return "redirect:/vacancies/employer";
    }

    @GetMapping("/employer/delete/{id}")
    public String deleteVacancy(@PathVariable("id") Long id) {
        vacancyService.deleteVacancy(id);
        return "redirect:/vacancies/employer";
    }
}
