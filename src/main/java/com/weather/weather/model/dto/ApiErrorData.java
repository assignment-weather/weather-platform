package com.weather.weather.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiErrorData {
    private int code;
    private String message;
    private Map<String, Object> metadata;
}
