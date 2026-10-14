package com.example.lab2.model;

public enum EmploymentType {
    FULL_TIME("Повна зайнятість"),
    PART_TIME("Часткова зайнятість"),
    REMOTE("Віддалена робота"),
    PROJECT("Проєктна робота");

    private final String title;

    EmploymentType(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}

