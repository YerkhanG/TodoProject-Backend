package com.example.prac1.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import org.slf4j.Logger; // Import Logger
import org.slf4j.LoggerFactory; // Import LoggerFactory

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class); // Initialize Logger

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<String> handleAccessDeniedException(AccessDeniedException ex) {
        logger.warn("Access Denied: {}", ex.getMessage()); // Use warn or info for denied access
        return new ResponseEntity<>("Forbidden: " + ex.getMessage(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        ex.getBindingResult().getFieldError();
        String errorMessage = ex.getBindingResult().getFieldError().getDefaultMessage();
        logger.warn("Validation Error: {}", errorMessage);
        return new ResponseEntity<>("Bad Request: " + errorMessage, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        logger.warn("Malformed JSON or unreadable HTTP message: {}", ex.getMessage());
        return new ResponseEntity<>("Bad Request: Malformed JSON or invalid request body.", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleAllExceptions(Exception ex) {
        // --- CRUCIAL CHANGE HERE: Log the full stack trace ---
        logger.error("An unexpected error occurred during request processing.", ex);
        return new ResponseEntity<>("An unexpected internal server error occurred. Please try again later.", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}