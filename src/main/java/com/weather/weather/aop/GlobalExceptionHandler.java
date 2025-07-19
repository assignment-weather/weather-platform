package com.weather.weather.aop;

import com.weather.weather.exceptions.CityNotFoundException;
import com.weather.weather.model.dto.ApiErrorData;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
@Log4j2
public class GlobalExceptionHandler {


    @ExceptionHandler(CityNotFoundException.class)
    public ResponseEntity<ApiErrorData> handleCityNotFoundException(CityNotFoundException ex, WebRequest request) {
        log.warn("Handling CityNotFoundException: {}", ex.getMessage());
        return buildErrorResponseEntity(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorData> handleAllUncaughtException(Exception ex, WebRequest request) {
        log.error("An unexpected error occurred: ", ex);
        return buildErrorResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected internal server error occurred.");
    }

    private ResponseEntity<ApiErrorData> buildErrorResponseEntity(HttpStatus status, String message) {
        ApiErrorData apiErrorData = ApiErrorData.builder()
                .code(status.value())
                .message(message)
                .build();
        return new ResponseEntity<>(apiErrorData, status);
    }
}
