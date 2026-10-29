package com.salesianostriana.dam.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.Map;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class VentaErrors {
    @ExceptionHandler(NoSuchElementException.class)
    ResponseEntity<Map<String,String>> missingResource() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("code", "not_found"));
    }
    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity<Map<String,String>> invalidRequest() {
        return ResponseEntity.badRequest().body(Map.of("code", "invalid_request"));
    }
}
