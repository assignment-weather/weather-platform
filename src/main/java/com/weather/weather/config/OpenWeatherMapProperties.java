package com.weather.weather.config;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "openweathermap")
@Getter
@Setter
public class OpenWeatherMapProperties {

    @NotBlank
    private String apiUrl;

    @NotBlank
    private String apiKey;

    private int connectTimeout = 10000;

    private int readTimeout = 10000;
}
