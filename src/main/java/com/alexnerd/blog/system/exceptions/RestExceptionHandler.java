package com.alexnerd.blog.system.exceptions;

import io.github.resilience4j.bulkhead.BulkheadFullException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.FileNotFoundException;

@ControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(BulkheadFullException.class)
    public ResponseEntity<?> handleBulkheadException() {
        return ResponseEntity
                .status(HttpStatus.BANDWIDTH_LIMIT_EXCEEDED)
                .header("detail", "Service overload")
                .build();
    }

    @ExceptionHandler(FileNotFoundException.class)
    public ResponseEntity<?> handleFileNotfoundException() {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .header("detail", "Not found")
                .build();
    }
}
