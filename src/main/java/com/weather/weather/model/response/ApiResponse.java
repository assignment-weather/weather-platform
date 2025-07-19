package com.weather.weather.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.weather.weather.model.dto.ApiErrorData;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private int status;
    private String requestId;
    private String requestURI;
    private T data;
    private ApiErrorData error;
}
