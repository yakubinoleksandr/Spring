package com.example.lab2.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.format.DateTimeFormatter;

@Configuration
public class AppConfig {

    @Bean
    public DateTimeFormatter dateFormatter() {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    }
}
