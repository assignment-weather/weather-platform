package com.weather.weather.model.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class WeatherForecastResponse {
    private String cityName;
    private String country;
    private List<DailyForecast> dailyForecasts;

    @Data
    @Builder
    public static class DailyForecast {
        private LocalDate date;
        private double highTemp;
        private double lowTemp;
        private List<String> messages;
    }
}
