package com.example.lab2.controller;

import com.example.lab2.dto.ApiError;
import com.example.lab2.service.ResourceNotFoundException;
import com.example.lab2.service.TransactionDemoException;

import jakarta.servlet.http.HttpServletRequest;

import java.time.Instant;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> notFound(
            ResourceNotFoundException ex,
            HttpServletRequest request
    ) {

        return response(
                HttpStatus.NOT_FOUND,
                ex.getMessage(),
                request
        );
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<ApiError> duplicate(
            DuplicateKeyException ex,
            HttpServletRequest request
    ) {

        return response(
                HttpStatus.CONFLICT,
                "Запис із таким унікальним значенням уже існує",
                request
        );
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> integrity(
            DataIntegrityViolationException ex,
            HttpServletRequest request
    ) {

        return response(
                HttpStatus.CONFLICT,
                "Порушено обмеження цілісності даних у БД",
                request
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> validation(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {

        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error ->
                        error.getField() + ": " + error.getDefaultMessage()
                )
                .findFirst()
                .orElse("Некоректні вхідні дані");

        return response(
                HttpStatus.BAD_REQUEST,
                message,
                request
        );
    }

    @ExceptionHandler(TransactionDemoException.class)
    public ResponseEntity<ApiError> transactionDemo(
            TransactionDemoException ex,
            HttpServletRequest request
    ) {

        return response(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ex.getMessage(),
                request
        );
    }

    private ResponseEntity<ApiError> response(
            HttpStatus status,
            String message,
            HttpServletRequest request
    ) {

        ApiError body = new ApiError(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                request.getRequestURI()
        );

        return ResponseEntity
                .status(status)
                .body(body);
    }
}