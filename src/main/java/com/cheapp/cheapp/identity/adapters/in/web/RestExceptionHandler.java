package com.cheapp.cheapp.identity.adapters.in.web;

import com.cheapp.cheapp.identity.application.exception.ConflictException;
import com.cheapp.cheapp.identity.application.exception.NotFoundException;
import com.cheapp.cheapp.identity.application.exception.UnauthorizedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;

@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<?> handleNotFound(NotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error("NOT_FOUND", ex.getMessage()));
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<?> handleConflict(ConflictException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error("CONFLICT", ex.getMessage()));
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<?> handleUnauthorized(UnauthorizedException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error("UNAUTHORIZED", ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidation(MethodArgumentNotValidException ex) {
        var body = error("VALIDATION_ERROR", "Datos inválidos");
        var details = new LinkedHashMap<String, String>();
        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            details.put(fe.getField(), fe.getDefaultMessage());
        }
        body.put("details", details);
        return ResponseEntity.badRequest().body(body);
    }

    private static LinkedHashMap<String, Object> error(String code, String message) {
        var map = new LinkedHashMap<String, Object>();
        map.put("code", code);
        map.put("message", message);
        return map;
    }
}
