package com.weather.weather.model.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenWeatherApiResponse {
    private List<ForecastItem> list;
    private City city;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class City {
        private String name;
        private String country;
    }
}
