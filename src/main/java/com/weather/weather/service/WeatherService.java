package com.weather.weather.service;

import com.weather.weather.config.OpenWeatherMapClient;
import com.weather.weather.model.response.ForecastItem;
import com.weather.weather.model.response.OpenWeatherApiResponse;
import com.weather.weather.model.response.WeatherForecastResponse;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class WeatherService {

    private static final Logger logger = LoggerFactory.getLogger(WeatherService.class);
    private final OpenWeatherMapClient weatherClient;
    private static final double MPS_TO_MPH_CONVERSION = 2.23694;

    @Cacheable(value = "weatherCache", key = "T(java.util.Locale).getDefault().getLanguage() + T(org.springframework.util.StringUtils).capitalize(#city)")
    public WeatherForecastResponse getForecast(String city) {
        logger.info("Fetching forecast for city '{}'. Cache miss or cache not used.", city);
        OpenWeatherApiResponse rawData = weatherClient.fetchWeather(city);

        Map<LocalDate, List<ForecastItem>> forecastsByDay = rawData.getList().stream()
                .filter(item -> {
                    LocalDate itemDate = item.getDt_txt().toLocalDate();
                    LocalDate today = LocalDate.now();
                    return !itemDate.isBefore(today) && itemDate.isBefore(today.plusDays(3));
                })
                .collect(Collectors.groupingBy(
                        item -> item.getDt_txt().toLocalDate(),
                        LinkedHashMap::new,
                        Collectors.toList()
                ));

        List<WeatherForecastResponse.DailyForecast> dailyForecasts = new ArrayList<>();
        for (Map.Entry<LocalDate, List<ForecastItem>> entry : forecastsByDay.entrySet()) {
            LocalDate date = entry.getKey();
            List<ForecastItem> dayItems = entry.getValue();

            double minTemp = dayItems.stream().mapToDouble(item -> item.getMain().getTemp_min()).min().orElse(0);
            double maxTemp = dayItems.stream().mapToDouble(item -> item.getMain().getTemp_max()).max().orElse(0);

            List<String> messages = generateAdvisoryMessages(dayItems, maxTemp);

            dailyForecasts.add(WeatherForecastResponse.DailyForecast.builder()
                    .date(date)
                    .highTemp(maxTemp)
                    .lowTemp(minTemp)
                    .messages(messages)
                    .build());
        }

        return WeatherForecastResponse.builder()
                .cityName(rawData.getCity().getName())
                .country(rawData.getCity().getCountry())
                .dailyForecasts(dailyForecasts)
                .build();
    }

    private List<String> generateAdvisoryMessages(List<ForecastItem> dayItems, double maxTemp) {
        List<String> messages = new ArrayList<>();
        if (maxTemp > 40) {
            messages.add("Use sunscreen lotion");
        }
        boolean willRain = dayItems.stream().anyMatch(item -> "Rain".equalsIgnoreCase(item.getWeather().get(0).getMain()));
        boolean isWindy = dayItems.stream().anyMatch(item -> item.getWind().getSpeed() * MPS_TO_MPH_CONVERSION > 10);
        boolean hasThunderstorm = dayItems.stream().anyMatch(item -> "Thunderstorm".equalsIgnoreCase(item.getWeather().get(0).getMain()));

        if (willRain) messages.add("Carry umbrella");
        if (isWindy) messages.add("It’s too windy, watch out!");
        if (hasThunderstorm) messages.add("Don’t step out! A Storm is brewing!");

        return messages;
    }
}