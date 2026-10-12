package com.example.consolehelloworld;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ConsoleHelloWorldApplication implements CommandLineRunner {

    public static void main(String[] args) {
        System.out.println("Begin of main");
        SpringApplication.run(ConsoleHelloWorldApplication.class, args);
        System.out.println("End of main");
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Hello from Spring Boot!");
    }
}
