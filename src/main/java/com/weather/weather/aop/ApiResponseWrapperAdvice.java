package com.weather.weather.aop;


import com.weather.weather.model.dto.ApiErrorData;
import com.weather.weather.model.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.MDC;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@RestControllerAdvice
public class ApiResponseWrapperAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request, ServerHttpResponse response) {

        HttpServletRequest httpServletRequest = ((ServletServerHttpRequest) request).getServletRequest();
        int status = ((ServletServerHttpResponse) response).getServletResponse().getStatus();

        // Do not wrap responses for Swagger/Actuator endpoints
        String requestURI = httpServletRequest.getRequestURI();
        if (requestURI.startsWith("/swagger-ui") || requestURI.startsWith("/v3/api-docs") || requestURI.startsWith("/actuator")) {
            return body;
        }

        String requestId = MDC.get("requestId");
        if (MediaType.APPLICATION_JSON.equals(selectedContentType)) {
            String path = request.getURI().getPath();
            if (status == HttpStatus.OK.value()) {
                return ApiResponse.builder()
                        .data(body)
                        .status(status)
                        .requestId(requestId)
                        .requestURI(path)
                        .build();
            } else {
                if (body instanceof ApiErrorData apiErrorData) {
                    return ApiResponse.builder()
                            .error(apiErrorData)
                            .status(status)
                            .requestURI(path)
                            .requestId(requestId)
                            .build();
                }
            }
        }
        return body;
    }
}