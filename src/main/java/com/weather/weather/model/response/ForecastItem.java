package com.weather.weather.model.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ForecastItem {
    private MainData main;
    private List<Weather> weather;
    private Wind wind;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dt_txt;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class MainData {
        private double temp_min;
        private double temp_max;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Weather {
        private String main;
        private String description;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Wind {
        private double speed;
    }
}
