package com.demo.githubapi.exception;

import com.demo.githubapi.model.dto.ExceptionDto;
import org.kohsuke.github.GHFileNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final MediaType defaultMediaType = MediaType.APPLICATION_JSON;

    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    public ResponseEntity<ExceptionDto> handleWrongMediaTypeException(HttpMediaTypeNotAcceptableException e) {
        HttpStatus resultStatus = HttpStatus.NOT_ACCEPTABLE;
        return ResponseEntity
                .status(resultStatus)
                .contentType(defaultMediaType)
                .body(new ExceptionDto(resultStatus.value(), e.getMessage()));
    }

    @ExceptionHandler(GitHubServiceException.class)
    public ResponseEntity<ExceptionDto> handleGitHubServiceException(GitHubServiceException e) {
        HttpStatus resultStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        return ResponseEntity
                .status(resultStatus)
                .contentType(defaultMediaType)
                .body(new ExceptionDto(resultStatus.value(), e.getMessage()));
    }

    @ExceptionHandler(GHFileNotFoundException.class)
    public ResponseEntity<ExceptionDto> handleGitHubFileNotFoundException(GHFileNotFoundException e) {
        HttpStatus resultStatus = HttpStatus.NOT_FOUND;
        return ResponseEntity
                .status(resultStatus)
                .contentType(defaultMediaType)
                .body(new ExceptionDto(resultStatus.value(), e.getMessage()));
    }
}
