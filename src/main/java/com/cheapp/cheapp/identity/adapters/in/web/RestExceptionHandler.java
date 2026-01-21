package com.cheapp.cheapp.identity.adapters.in.web;

import com.cheapp.cheapp.identity.application.exception.ConflictException;
import com.cheapp.cheapp.identity.application.exception.NotFoundException;
import com.cheapp.cheapp.identity.application.exception.UnauthorizedException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.LinkedHashMap;

@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<?> handleNotFound(NotFoundException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error("NOT_FOUND", ex.getMessage(), HttpStatus.NOT_FOUND, request));
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<?> handleConflict(ConflictException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error("CONFLICT", ex.getMessage(), HttpStatus.CONFLICT, request));
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<?> handleUnauthorized(UnauthorizedException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error("UNAUTHORIZED", ex.getMessage(), HttpStatus.UNAUTHORIZED, request));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error("FORBIDDEN", "Acceso denegado", HttpStatus.FORBIDDEN, request));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<?> handleAuth(AuthenticationException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error("UNAUTHORIZED", "No autenticado", HttpStatus.UNAUTHORIZED, request));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        var body = error("VALIDATION_ERROR", "Datos inválidos", HttpStatus.BAD_REQUEST, request);
        var details = new LinkedHashMap<String, String>();
        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            details.put(fe.getField(), fe.getDefaultMessage());
        }
        body.put("details", details);
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneric(Exception ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(error("INTERNAL_ERROR", "Error interno", HttpStatus.INTERNAL_SERVER_ERROR, request));
    }

    private static LinkedHashMap<String, Object> error(String code, String message, HttpStatus status, HttpServletRequest request) {
        var map = new LinkedHashMap<String, Object>();
        map.put("code", code);
        map.put("message", message);
        map.put("status", status.value());
        map.put("timestamp", Instant.now().toString());
        map.put("path", request.getRequestURI());
        String traceId = request.getHeader("X-Trace-Id");
        if (traceId != null && !traceId.isBlank()) {
            map.put("traceId", traceId);
        }
        return map;
    }
}
