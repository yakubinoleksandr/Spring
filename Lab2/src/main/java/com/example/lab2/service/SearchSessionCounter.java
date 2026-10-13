package com.example.lab2.service;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class SearchSessionCounter {
    private int searchCount = 0;

    public int incrementAndGet() {
        return ++searchCount;
    }
}